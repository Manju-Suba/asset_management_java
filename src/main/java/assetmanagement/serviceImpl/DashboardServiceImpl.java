package assetmanagement.serviceImpl;

import assetmanagement.dto.RejectAsstCount;
import assetmanagement.dto.RejectedAssets;
import assetmanagement.enumData.*;
import assetmanagement.model.Asset;
import assetmanagement.model.masters.AssetClass;
import assetmanagement.model.masters.AssetType;
import assetmanagement.repository.asset.AssetRepository;
import assetmanagement.repository.audit.AuditRepository;
import assetmanagement.repository.masters.AssetClassRepository;
import assetmanagement.repository.masters.AssetTypeRepository;
import assetmanagement.response.CountDTO;
import assetmanagement.response.MajorMinorAssetsDTO;
import assetmanagement.response.MajorMinorAssetsResponse;
import assetmanagement.service.DashboardService;
import assetmanagement.service.asset.DisposedService;
import assetmanagement.util.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private static final String ALLOCATED_STATUS = "allocated";
    private static final String DAMAGED_STATUS = "damaged";
    private static final String STOCK_STATUS = "stock";
    private static final String ASSET_ID = "assetId";
    private static final String ASSET_DATA_MODEL = "asset";
    private static final String REJECTED_COUNT = "rejectedCount";
    private static final String LATEST_AUDITS = "latestAudits";
    private static final String ASSET_CLASS_DATA = "assetClass";
    private static final String CHILD_ID = "childId";
    private static final String COST_BASED_MAJOR_ASSET = "costBasedMajorAsset";
    private static final String COST_BASED_MINOR_ASSET = "costBasedMinorAsset";
    private static final String COST_OF_ASSET = "costOfAsset";
    private final AssetRepository assetRepository;
    private final AssetTypeRepository assetTypeRepository;
    private final AssetClassRepository assetClassRepository;
    private final DisposedService disposedService;
    private final MongoTemplate mongoTemplate;
    private final AuditRepository auditRepository;

    @Override
    public List<Asset> getRecentlyPurchasedAssets() {
        Sort sortByRecent = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(0, 10, sortByRecent);
        Page<Asset> recentPurchased = assetRepository.findByCompanyIdAndPlantAndStatus(AuthUser.getCompanyId(),
                AuthUser.getPlant(), ActiveInActive.ACTIVE.getValue(), pageable);
        return recentPurchased.getContent();
    }

    @Override
    public Map<String, Object> getAssetsCounts() {
        Map<String, Object> response = new HashMap<>();

        // Get counts of different asset statuses
        long totalAssets = assetRepository.count();
        long totalAllocatedAssets = assetRepository.countByCompanyIdAndPlantAndAvailableStatus(
                AuthUser.getCompanyId(),
                AuthUser.getPlant(), ALLOCATED_STATUS);
        long totalDamagedAssets = assetRepository.countByCompanyIdAndPlantAndAvailableStatus(
                AuthUser.getCompanyId(),
                AuthUser.getPlant(), DAMAGED_STATUS);
        long totalStocks = assetRepository.countByCompanyIdAndPlantAndAvailableStatus(AuthUser.getCompanyId(),
                AuthUser.getPlant(), STOCK_STATUS);
        long totalRejectedAssets = assetRepository.countByCompanyIdAndPlantAndAvailableStatus(
                AuthUser.getCompanyId(),
                AuthUser.getPlant(), "rejected");

        response.put("Total_Assets", totalAssets);
        response.put("Total_AllocatedAssets", totalAllocatedAssets);
        response.put("Total_DamagedAssets", totalDamagedAssets);
        response.put("Total_Stocks", totalStocks);
        response.put("Total_RejectedAssets", totalRejectedAssets);

        // Get counts of different asset types
        List<Map<String, Object>> assetTypeCounts = new ArrayList<>();
        List<AssetType> allAssetTypes = assetTypeRepository.findAll();
        for (AssetType assetType : allAssetTypes) {
            Map<String, Object> assetTypeCount = new HashMap<>();
            assetTypeCount.put("name", assetType.getName());
            assetTypeCount.put(STOCK_STATUS,
                    assetRepository.countByCompanyIdAndPlantAndAssetTypeIdAndAvailableStatus(
                            AuthUser.getCompanyId(), AuthUser.getPlant(), assetType.getId(),
                            STOCK_STATUS));
            assetTypeCount.put(ALLOCATED_STATUS,
                    assetRepository.countByCompanyIdAndPlantAndAssetTypeIdAndAvailableStatus(
                            AuthUser.getCompanyId(), AuthUser.getPlant(), assetType.getId(),
                            ALLOCATED_STATUS));
            assetTypeCount.put(DAMAGED_STATUS,
                    assetRepository.countByCompanyIdAndPlantAndAssetTypeIdAndAvailableStatus(
                            AuthUser.getCompanyId(), AuthUser.getPlant(), assetType.getId(),
                            DAMAGED_STATUS));
            assetTypeCounts.add(assetTypeCount);
        }
        response.put("asset_type", assetTypeCounts);

        return response;

    }

    @Override
    public Map<String, Object> getDamagedAssetsCountByYearAndMonth(Integer year) {

        Map<String, Object> response = new LinkedHashMap<>();

        for (int month = 1; month <= 12; month++) {
            LocalDate beginningOfMonth = LocalDate.of(year, month, 1);
            LocalDate lastDayOfMonth = beginningOfMonth.withDayOfMonth(beginningOfMonth.lengthOfMonth());
            long damageCount = assetRepository
                    .countByCompanyIdAndPlantAndStatusAndAvailableStatusAndUpdatedAtBetween(
                            AuthUser.getCompanyId(),
                            AuthUser.getPlant(),
                            ActiveInActive.ACTIVE.getValue(),
                            AuditStatus.Disposed.getValue(),
                            beginningOfMonth.minusDays(1),
                            lastDayOfMonth.plusDays(1));

            String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            response.put(monthName, damageCount);
        }

        return response;
    }

    @Override
    public Map<String, Object> getAssetCategoryCounts() {

        long statusCount = assetRepository.countByCompanyIdAndPlantAndStatus(AuthUser.getCompanyId(),
                AuthUser.getPlant(), ActiveInActive.ACTIVE.getValue());

        List<AssetClass> assetClassList = assetClassRepository.findByCompanyIdAndPlant(AuthUser.getCompanyId(),
                AuthUser.getPlant());
        Map<String, Object> totalCounts = new LinkedHashMap<>();
        totalCounts.put("AllAsset", statusCount);
        for (AssetClass assetClass : assetClassList) {
            long categoryCount = assetRepository.countByCompanyIdAndPlantAndStatusAndAssetClass(
                    AuthUser.getCompanyId(), AuthUser.getPlant(), ActiveInActive.ACTIVE.getValue(),
                    assetClass.getAssetClass());
            totalCounts.put(assetClass.getAssetClass(), categoryCount);
        }
        return totalCounts;
    }

    @Override
    public List<Map<String, Object>> assetTypeCount(Integer page, Integer size) {

        if (page == null && size == null || page == null || size == null) {
            page = 0;
            size = 6;
        }
        Set<String> processedAssetClasses = new HashSet<>();

        Sort sortByAscId = Sort.by(Sort.Direction.ASC, "id");

        PageRequest pageable = PageRequest.of(page, size, sortByAscId);
        Page<AssetClass> pageAssetClasses = assetClassRepository
                .findByCompanyIdAndPlant(AuthUser.getCompanyId(), AuthUser.getPlant(), pageable);

        List<AssetClass> allAssetClasses = pageAssetClasses.getContent();
        List<Map<String, Object>> assetTypeCounts = new ArrayList<>();

        for (AssetClass assetClass2 : allAssetClasses) {
            Map<String, Object> assetTypeCount = new HashMap<>();
            if (processedAssetClasses.contains(assetClass2.getAssetClass())) {
                continue;
            }
            assetTypeCount.put("name", assetClass2.getAssetClass());
            assetTypeCount.put(STOCK_STATUS,
                    assetRepository.countByCompanyIdAndPlantAndAssetClassAndAvailableStatus(
                            AuthUser.getCompanyId(), AuthUser.getPlant(),
                            assetClass2.getAssetClass(),
                            AvailableStatus.Stock.getValue()));
            assetTypeCount.put("dispose",
                    assetRepository.countByCompanyIdAndPlantAndAssetClassAndAvailableStatus(
                            AuthUser.getCompanyId(),
                            AuthUser.getPlant(), assetClass2.getAssetClass(),
                            AuditStatus.Disposed.getValue()));
            assetTypeCounts.add(assetTypeCount);
            processedAssetClasses.add(assetClass2.getAssetClass());
        }
        return assetTypeCounts;
    }

    public Map<String, Long> overallAsset() {
        Map<String, Long> counts = new HashMap<>();
        long totalAllocatedAssets = assetRepository.countByCompanyIdAndPlantAndStatusAndAvailableStatus(
                AuthUser.getCompanyId(), AuthUser.getPlant(), ActiveInActive.ACTIVE.getValue(),
                AuditStatus.Disposed.getValue());
        long totalStocks = assetRepository.countByCompanyIdAndPlantAndStatusAndAvailableStatus(
                AuthUser.getCompanyId(),
                AuthUser.getPlant(), ActiveInActive.ACTIVE.getValue(),
                AvailableStatus.Stock.getValue());
        counts.put("totalStocks", totalStocks);
        counts.put("totalDamage", totalAllocatedAssets);
        return counts;

    }

    @Override
    public Map<String, Long> totalDamagedRejectedAssets() {
        Map<String, Long> counts = new HashMap<>();
        long totalDamagedAssets = assetRepository.countByCompanyIdAndPlantAndStatusAndAvailableStatus(
                AuthUser.getCompanyId(), AuthUser.getPlant(), ActiveInActive.ACTIVE.getValue(),
                DisposedStatus.Disposed.getValue());
        long totalRejectedAssets = disposedService.countRejectedRequests();
        counts.put("totalDamagedAsset", totalDamagedAssets);
        counts.put("totalRejectedAsset", totalRejectedAssets);
        return counts;
    }

    @Override
    public RejectAsstCount getRejectedAssets(Integer page, Integer size, boolean search, String value) {
        page = page == null ? 0 : page;
        size = size == null ? 10 : size;
        long skipCount = (long) page * size;

        Criteria criteria = Criteria.where("status")
                .is(TransferStatus.Rejected.getValue())
                .and("latestAudits.companyId").is(AuthUser.getCompanyId())
                .and("latestAudits.plant").is(AuthUser.getPlant());

        if (search && value != null && !value.isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where(ASSET_ID)
                            .regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("latestAudits.assetClass")
                            .regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)));
            criteria.andOperator(searchCriteria);
        }

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup(ASSET_DATA_MODEL, ASSET_ID, ASSET_ID, LATEST_AUDITS),
                Aggregation.unwind(LATEST_AUDITS),
                Aggregation.match(criteria),

                Aggregation.group("$assetId")
                        .first("_id").as("oid")
                        .first(ASSET_ID).as(ASSET_ID)
                        .first("latestAudits.assetClass").as(ASSET_CLASS_DATA)
                        .first("latestAudits.childId").as(CHILD_ID)
                        .count().as(REJECTED_COUNT),

                Aggregation.skip(skipCount),
                Aggregation.limit(size),
                Aggregation.project()
                        .andInclude("_id")
                        .and("oid").as("_id")
                        .and(ASSET_ID).as(ASSET_ID)
                        .and(ASSET_CLASS_DATA).as(ASSET_CLASS_DATA)
                        .and(CHILD_ID).as(CHILD_ID)
                        .and(REJECTED_COUNT).as(REJECTED_COUNT));

        Aggregation countAggregation = Aggregation.newAggregation(
                Aggregation.lookup(ASSET_DATA_MODEL, ASSET_ID, ASSET_ID, LATEST_AUDITS),
                Aggregation.match(criteria),
                Aggregation.group(ASSET_ID));

        AggregationResults<CountDTO> countAggregationResults = mongoTemplate.aggregate(countAggregation,
                "audit",
                CountDTO.class);
        List<CountDTO> counts = countAggregationResults.getMappedResults();

        AggregationResults<RejectedAssets> aggregationResults = mongoTemplate.aggregate(aggregation, "audit",
                RejectedAssets.class);
        List<RejectedAssets> auditResponses = aggregationResults.getMappedResults();

        List<RejectedAssets> auditResult = new ArrayList<>();
        for (RejectedAssets rejectedAsset : auditResponses) {
            Long statusCount = auditRepository.countByAssetIdAndStatus(rejectedAsset.getAssetId(),
                    AuditStatus.Rejected.getValue());
            rejectedAsset.setRejectedCount(statusCount);
            auditResult.add(rejectedAsset);
        }

        RejectAsstCount response = new RejectAsstCount();
        response.setAssetrecord(auditResponses);
        response.setAssetCounts(counts.size());
        return response;
    }

    @Override
    public List<Map<String, Object>> assetsCount(Integer page, Integer size, Boolean search, String value) {

        page = Optional.ofNullable(page).orElse(0);
        size = Optional.ofNullable(size).orElse(6);

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        Set<String> processedAssetClasses = new HashSet<>();
        List<Map<String, Object>> allAssetCounts = new ArrayList<>();
        Page<AssetClass> pageAssetClasses;

        if ((Boolean.TRUE.equals(search) && value != null) && !value.isEmpty()) {
            pageAssetClasses = assetClassRepository.findByCompanyIdAndPlantAndAssetClass(
                    AuthUser.getCompanyId(), AuthUser.getPlant(), value, pageable);
        } else {
            pageAssetClasses = assetClassRepository.findByCompanyIdAndPlant(AuthUser.getCompanyId(),
                    AuthUser.getPlant(), pageable);
        }
        List<AssetClass> allAssetClasses = pageAssetClasses.getContent();
        for (AssetClass assetClass2 : allAssetClasses) {
            Map<String, Object> assetCount = new LinkedHashMap<>();
            if (processedAssetClasses.contains(assetClass2.getAssetClass())) {
                continue;
            }
            assetCount.put(ASSET_CLASS_DATA, assetClass2.getAssetClass());
            assetCount.put("Online",
                    assetRepository.countByCompanyIdAndPlantAndAssetClassAndAssetStatus(
                            AuthUser.getCompanyId(), AuthUser.getPlant(),
                            assetClass2.getAssetClass(),
                            AvailableStatus.Online.getValue()));
            assetCount.put("Offline",
                    assetRepository.countByCompanyIdAndPlantAndAssetClassAndAssetStatus(
                            AuthUser.getCompanyId(), AuthUser.getPlant(),
                            assetClass2.getAssetClass(),
                            AvailableStatus.Offline.getValue()));
            assetCount.put("Maintenance",
                    assetRepository.countByCompanyIdAndPlantAndAssetClassAndAssetStatus(
                            AuthUser.getCompanyId(), AuthUser.getPlant(),
                            assetClass2.getAssetClass(),
                            AvailableStatus.Maintenance.getValue()));
            assetCount.put("Scrapped",
                    assetRepository.countByCompanyIdAndPlantAndAssetClassAndAssetStatus(
                            AuthUser.getCompanyId(), AuthUser.getPlant(),
                            assetClass2.getAssetClass(), AvailableStatus.Scrap.getValue()));
            allAssetCounts.add(assetCount);
            processedAssetClasses.add(assetClass2.getAssetClass());
        }
        return allAssetCounts;
    }

    @Override
    public MajorMinorAssetsResponse getMajorAndMinorAsset(Integer page, Integer size, Boolean search,
                                                          String value) {

        page = page == null ? 0 : page;
        size = size == null ? 10 : size;

        Criteria criteria = Criteria.where("companyId").is(AuthUser.getCompanyId())
                .and("plant").is(AuthUser.getPlant());

        if ((Boolean.TRUE.equals(search) && value != null) && !value.isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where(ASSET_CLASS_DATA)
                            .regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where(COST_OF_ASSET)
                            .regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE))

            );
            criteria.andOperator(searchCriteria);
        }
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("$assetClass")
                        .max(COST_OF_ASSET).as(COST_BASED_MAJOR_ASSET)
                        .min(COST_OF_ASSET).as(COST_BASED_MINOR_ASSET)
                        .first(ASSET_CLASS_DATA).as(ASSET_CLASS_DATA),
                Aggregation.skip((long) page * size),
                Aggregation.limit(size),
                Aggregation.project()
                        .andExclude("_id")
                        .and(ASSET_CLASS_DATA).as(ASSET_CLASS_DATA)
                        .and(COST_BASED_MAJOR_ASSET).as(COST_BASED_MAJOR_ASSET)
                        .and(COST_BASED_MINOR_ASSET).as(COST_BASED_MINOR_ASSET));
        Aggregation countAggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("$assetClass"));

        AggregationResults<CountDTO> countAggregationResults = mongoTemplate.aggregate(countAggregation,
                ASSET_DATA_MODEL, CountDTO.class);
        List<CountDTO> counts = countAggregationResults.getMappedResults();

        AggregationResults<MajorMinorAssetsDTO> aggregationResults = mongoTemplate.aggregate(aggregation,
                ASSET_DATA_MODEL, MajorMinorAssetsDTO.class);
        List<MajorMinorAssetsDTO> majorMinorAssetsResponses = aggregationResults.getMappedResults();
        MajorMinorAssetsResponse response = new MajorMinorAssetsResponse();
        response.setMajorMinorAssetsCounts(counts.size());
        response.setMajorMinorList(majorMinorAssetsResponses);
        return response;
    }

}
