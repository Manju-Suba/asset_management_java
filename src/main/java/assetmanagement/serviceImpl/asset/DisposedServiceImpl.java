package assetmanagement.serviceImpl.asset;

import assetmanagement.dto.AuditDto;
import assetmanagement.enumData.ActiveInActive;
import assetmanagement.enumData.AuditStatus;
import assetmanagement.enumData.AvailableStatus;
import assetmanagement.enumData.DisposedStatus;
import assetmanagement.exception.ResourceNotFoundException;
import assetmanagement.model.Asset;
import assetmanagement.model.AssetHistory;
import assetmanagement.model.Users;
import assetmanagement.model.audit.Audit;
import assetmanagement.model.disposed.DisposedDetail;
import assetmanagement.model.disposed.DisposedHistory;
import assetmanagement.repository.UserRepository;
import assetmanagement.repository.asset.AssetHistoryRepository;
import assetmanagement.repository.asset.AssetRepository;
import assetmanagement.repository.audit.AuditRepository;
import assetmanagement.repository.disposed.DisposedDetailRepository;
import assetmanagement.repository.disposed.DisposedHistoryRepository;
import assetmanagement.request.RenewedRequest;
import assetmanagement.request.ReplacedRequest;
import assetmanagement.request.RequestWithFilter;
import assetmanagement.response.*;
import assetmanagement.service.asset.DisposedService;
import assetmanagement.util.AuthUser;
import assetmanagement.util.FileHandle;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisposedServiceImpl implements DisposedService {
    private final DisposedDetailRepository disposedDetailRepository;
    private final DisposedHistoryRepository disposedHistoryRepository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final AuditRepository auditRepository;
    private final AssetHistoryRepository assetHistoryRepository;
    private final MongoTemplate mongoTemplate;

    @Value("${upload.path}")
    private String fileBasePath;

    @Override
    public DisposedDetail create(DisposedDetail disposedDetail) throws IOException {
        DisposedDetail details = disposedDetailRepository.save(disposedDetail);
        createHistory(details, details.getStatus());
        return details;
    }

    @Override
    public DisposedDetail getById(String id) {
        Optional<DisposedDetail> optionalDisposedDetail = disposedDetailRepository.findById(id);

        if (optionalDisposedDetail.isPresent()) {
            return optionalDisposedDetail.get();
        }
        throw new ResourceNotFoundException("Data not found");
    }

    @Override
    public List<DisposedDetail> getAll(String status) {
        return disposedDetailRepository.findAllByStatus(status);

    }

    @Override
    public DisposedDetail updateStatus(String id, String status, String reason, String userId) throws IOException {
        Optional<DisposedDetail> optionalDisposedDetail = disposedDetailRepository.findById(id);

        if (optionalDisposedDetail.isPresent()) {
            DisposedDetail disposedDetail = optionalDisposedDetail.get();
            disposedDetail.setStatus(status);
            disposedDetail.setReason(reason);
            Optional<Users> user = userRepository.findById(userId);
            disposedDetail.setActionBy(user.get());
            disposedDetail.setActionDate(LocalDate.now());
            DisposedDetail detailsUpdate = disposedDetailRepository.save(disposedDetail);
            createHistory(detailsUpdate, detailsUpdate.getStatus());

            return detailsUpdate;
        }
        throw new ResourceNotFoundException("Data not found");
    }

    @Override
    public CloseToDisposedResponse closedDisposed(RequestWithFilter requestWithFilter, boolean search, String value,
            Integer page, Integer size) {
        String statusNotDisposed = AuditStatus.Disposed.getValue();
        String statusNotRenew = DisposedStatus.Replaced.getValue();
        String statusNotReplaced = DisposedStatus.Renewed.getValue();
        LocalDate today = LocalDate.now().plusDays(0);
        LocalDate tomorrow = today.plusMonths(2);
        List<String> statusList = Arrays.asList(statusNotDisposed, statusNotRenew, statusNotReplaced);

        page = page == null ? 0 : page;
        size = size == null ? 10 : size;
        Criteria criteria = Criteria.where("companyId").is(AuthUser.getCompanyId())
                .and("plant").is(AuthUser.getPlant())
                .and("status").nin(statusList)
                .and("availableStatus").nin(statusList)
                .and("expiryDate").gte(today).lt(tomorrow);

        if (requestWithFilter.getAssetClass() != null && !requestWithFilter.getAssetClass().isEmpty()) {
            criteria.and("assetClass").is(requestWithFilter.getAssetClass());
        }

        if (search && value != null && !value.isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("assetId").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("assetClass").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)));
            criteria.andOperator(searchCriteria);
        }

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.skip((long) page * size),
                Aggregation.limit(size),
                Aggregation.sort(Sort.Direction.DESC, "expiryDate"));

        AggregationResults<DisposedResponse> aggregationResults = mongoTemplate.aggregate(aggregation, "asset",
                DisposedResponse.class);
        List<DisposedResponse> disposedResponses = aggregationResults.getMappedResults();
        long count = mongoTemplate.count(Query.query(criteria), "asset");

        CloseToDisposedResponse response = new CloseToDisposedResponse();
        response.setClosedToDisposedCounts(count);
        response.setAssets(disposedResponses);
        return response;
    }

    // @Override
    // public List<AuditDto> pendingRequest(RequestWithFilter requestWithFilter) {
    // String status = AuditStatus.Waiting.getValue();
    // List<AuditDto> auditResponse = auditRepository.findByStatus(status);
    // Predicate<AuditDto> assetClassFilter = null;
    // Predicate<AuditDto> assetTypeFilter = null;
    // Predicate<AuditDto> assetIdFilter = null;
    // Predicate<AuditDto> combinedFilter = null;

    // if (requestWithFilter.getAssetCategoryId() != null &&
    // !requestWithFilter.getAssetCategoryId().isEmpty()) {
    // assetClassFilter = auditResp -> auditResp.getAssetId().getAssetClass()
    // .equals(requestWithFilter.getAssetCategoryId());
    // combinedFilter = assetClassFilter;
    // }

    // // if (requestWithFilter.getAssetTypeId() != null &&
    // // !requestWithFilter.getAssetTypeId().isEmpty()) {
    // // assetTypeFilter = auditResp ->
    // auditResp.getAssetId().getAssetType().getId()
    // // .equals(requestWithFilter.getAssetTypeId());
    // // if (combinedFilter == null) {
    // // combinedFilter = assetTypeFilter;
    // // } else {
    // // combinedFilter = combinedFilter.and(assetTypeFilter);
    // // }

    // // }
    // if (requestWithFilter.getAssetNo() != null &&
    // !requestWithFilter.getAssetNo().isEmpty()) {
    // assetIdFilter = auditResp ->
    // auditResp.getAssetId().getAssetId().equals(requestWithFilter.getAssetNo());
    // if (combinedFilter == null) {
    // combinedFilter = assetIdFilter;
    // } else {
    // combinedFilter = combinedFilter.and(assetIdFilter);
    // }

    // }
    // if (combinedFilter == null) {
    // return auditResponse;
    // }
    // return
    // auditResponse.stream().filter(combinedFilter).collect(Collectors.toList());

    // }

    @Override
    public Audit approvedRequest(String id) {
        // Audit auditDisposed =
        // auditRepository.findByStatusAndId(AuditStatus.Waiting.getValue(), id);
        // auditDisposed.setStatus(AuditStatus.Disposed.getValue());
        // List<Asset> assetDisposed =
        // assetRepository.findByAssetId(auditDisposed.getAssetId());
        // if (assetDisposed.isPresent()) {
        // Asset asset = assetDisposed.get();
        // asset.setStatus(AuditStatus.Disposed.getValue());
        // assetRepository.save(asset);
        // }
        // auditRepository.save(auditDisposed);
        // return auditDisposed;
        return null;
    }

    @Override
    public Audit rejectedRequest(String id) {
        Audit auditDisposed = auditRepository.findByStatusAndId(AuditStatus.Waiting.getValue(), id);
        auditDisposed.setStatus(AuditStatus.Rejected.getValue());
        auditRepository.save(auditDisposed);
        return auditDisposed;
    }

    @Override
    public AssetDisposedResponse fetchData(RequestWithFilter requestWithFilter, Integer page, Integer size,
            boolean search, String value) {
        page = page == null ? 0 : page;
        size = size == null ? 10 : size;

        Criteria criteria = Criteria.where("companyId").is(AuthUser.getCompanyId())
                .and("plant").is(AuthUser.getPlant())
                .and("availableStatus").is(AuditStatus.Disposed.getValue());

        if (requestWithFilter.getAssetClass() != null) {
            criteria = criteria.and("assetClass").is(requestWithFilter.getAssetClass());
        }
        if (requestWithFilter.getAssetId() != null) {
            criteria.and("assetId").is(requestWithFilter.getAssetId());
        }
        if (search && value != null && !value.isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("assetId").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("assetClass").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)));
            criteria.andOperator(searchCriteria);
        }

        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("scrappedDetails")
                .localField("assetId")
                .foreignField("assetId")
                .as("scrappedDetails");

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                lookupOperation,
                Aggregation.project("id", "companyId", "assetId", "assetClass", "plant", "childId", "availableStatus",
                        "picture", "scrappedDetails.remarks"),
                Aggregation.skip((long) page * size),
                Aggregation.limit(size));

        AggregationResults<DisposedAsset> aggregationResults = mongoTemplate.aggregate(aggregation, "asset",
                DisposedAsset.class);
        List<DisposedAsset> auditResponses = aggregationResults.getMappedResults();

        long count = mongoTemplate.count(Query.query(criteria), "asset");
        AssetDisposedResponse response = new AssetDisposedResponse();
        response.setAssetDisposed(auditResponses);
        response.setAssetDisposedCount(count);
        return response;
    }

    @Override
    public ReplacedResponse fetchByStatus(String status, RequestWithFilter requestWithFilter, boolean search,
            String value, Integer page, Integer size) {
        String statusBy = null;
        if (status.equals(DisposedStatus.Renewed.getValue())) {
            statusBy = DisposedStatus.Renewed.getValue();
        } else if (status.equals(DisposedStatus.Replaced.getValue())) {
            statusBy = DisposedStatus.Replaced.getValue();
        }
        if (page == null && size == null || page == null || size == null) {
            page = 0;
            size = 10;
        }

        Criteria criteria = Criteria.where("companyId").is(AuthUser.getCompanyId())
                .and("plant").is(AuthUser.getPlant())
                .and("status").is(statusBy);

        if (requestWithFilter.getAssetClass() != null && !requestWithFilter.getAssetClass().isEmpty()) {
            criteria = criteria.and("assetClass").is(requestWithFilter.getAssetClass());
        }

        if (requestWithFilter.getAssetNo() != null && !requestWithFilter.getAssetNo().isEmpty()) {
            criteria = criteria.and("assetId").is(requestWithFilter.getAssetNo());
        }

        if (search && value != null && !value.isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("assetId").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("assetClass").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)));
            criteria.andOperator(searchCriteria);
        }

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.project("id", "companyId", "assetId", "assetClass", "plant", "status", "picture"),
                Aggregation.skip((long) page * size),
                Aggregation.limit(size));

        AggregationResults<AssetReplaced> aggregationResults = mongoTemplate.aggregate(aggregation, "asset",
                AssetReplaced.class);
        List<AssetReplaced> replacedAssets = aggregationResults.getMappedResults();
        long count = mongoTemplate.count(Query.query(criteria), "asset");

        ReplacedResponse response = new ReplacedResponse();
        response.setReplacedCount(count);
        response.setReplacedassets(replacedAssets);

        return response;
    }

    @Override
    public Asset actionReplace(ReplacedRequest replacedRequest, MultipartFile file) throws IOException {

        final List<String> allowedImageExtensions = Arrays.asList("jpg", "jpeg", "png");

        Asset asset = assetRepository.findById(replacedRequest.getAssetId().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found"));

        asset.setAvailableStatus(DisposedStatus.Replaced.getValue());
        asset.setStatus(DisposedStatus.Replaced.getValue());
        Asset replaceAsset = assetRepository.findById(replacedRequest.getReplaceAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Replaced Asset not found"));

        if (replaceAsset.getAuditDate() == null) {
            replaceAsset.setAuditDate(asset.getAuditDate());
        }
        AssetHistory assetHistory = new AssetHistory();

        if (file != null && !file.isEmpty()) {
            String originalName = file.getOriginalFilename();
            String extension;
            if (originalName != null) {
                extension = getFileExtension(originalName);
            } else {
                extension = null;
            }
            if (extension == null || !allowedImageExtensions.contains(extension.toLowerCase())) {
                throw new IllegalArgumentException("Invalid image format. Only JPG, JPEG, and PNG files are allowed.");
            }
            String pictureFileName = file != null && !file.isEmpty() ? FileHandle.saveAssetPicture(file)
                    : "no_image.png";

            assetHistory.setPicture(pictureFileName);
        }
        BeanUtils.copyProperties(replacedRequest, assetHistory);
        assetHistoryRepository.save(assetHistory);
        assetRepository.save(asset);
        assetRepository.save(replaceAsset);
        return replaceAsset;
    }

    private String getFileExtension(String originalName) {
        int lastIndex = originalName.lastIndexOf(".");
        if (lastIndex == -1) {
            return null;
        }
        return originalName.substring(lastIndex + 1);
    }

    @Override
    public Asset actionRenew(RenewedRequest renewedRequest, MultipartFile file) throws IOException {

        final List<String> allowedImageExtensions = Arrays.asList("jpg", "jpeg", "png");

        Asset asset = assetRepository.findById(renewedRequest.getAssetId().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found"));

        asset.setRenewStatus(DisposedStatus.Renewed.getValue());
        asset.setExpiryDate(renewedRequest.getExpiryDate());
        assetRepository.save(asset);

        AssetHistory assetHistory = new AssetHistory();

        if (file != null && !file.isEmpty()) {
            String originalName = file.getOriginalFilename();
            String extension;
            if (originalName != null) {
                extension = getFileExtension(originalName);
            } else {
                extension = null;
            }
            if (extension == null || !allowedImageExtensions.contains(extension.toLowerCase())) {
                throw new IllegalArgumentException("Invalid image format. Only JPG, JPEG, and PNG files are allowed.");
            }
            String pictureFileName = file != null && !file.isEmpty() ? FileHandle.saveAssetPicture(file)
                    : "no_image.png";
            assetHistory.setPicture(pictureFileName);
        }

        BeanUtils.copyProperties(renewedRequest, assetHistory);
        assetHistoryRepository.save(assetHistory);

        return asset;
    }

    public void createHistory(DisposedDetail disposedId, String status) throws IOException {
        DisposedHistory history = new DisposedHistory();
        if (status.equals(DisposedStatus.Pending.getValue())) {
            history.setStatusFrom(DisposedStatus.Created.getValue());
            history.setStatusTo(status);
        } else if (status.equals(DisposedStatus.Disposed.getValue())) {
            history.setStatusFrom(DisposedStatus.Pending.getValue());
            history.setStatusTo(status);
        }
        history.setDisposedId(disposedId);
        disposedHistoryRepository.save(history);
    }

    @Override
    public RenewedResponse getAllRenewed(RequestWithFilter requestWithFilter, boolean search, String value,
            Integer page, Integer size) {
        String statusBy = null;
        statusBy = DisposedStatus.Renewed.getValue();
        if (page == null && size == null || page == null || size == null) {
            page = 0;
            size = 10;
        }

        Criteria criteria = Criteria.where("companyId").is(AuthUser.getCompanyId())
                .and("plant").is(AuthUser.getPlant())
                .and("renewStatus").is(statusBy);

        if (requestWithFilter.getAssetClass() != null && !requestWithFilter.getAssetClass().isEmpty()) {
            criteria = criteria.and("assetClass").is(requestWithFilter.getAssetClass());
        }

        if (requestWithFilter.getAssetNo() != null && !requestWithFilter.getAssetNo().isEmpty()) {
            criteria = criteria.and("assetId").is(requestWithFilter.getAssetNo());
        }

        if (search && value != null && !value.isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("assetId").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("assetClass").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)));
            criteria.andOperator(searchCriteria);
        }

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.project("id", "companyId", "assetId", "assetClass", "plant", "status", "renewStatus",
                        "picture"),
                Aggregation.skip((long) page * size),
                Aggregation.limit(size),
                Aggregation.sort(Sort.Direction.DESC, "expiryDate"));

        AggregationResults<AssetReplaced> aggregationResults = mongoTemplate.aggregate(aggregation, "asset",
                AssetReplaced.class);
        List<AssetReplaced> replacedAssets = aggregationResults.getMappedResults();
        long count = mongoTemplate.count(Query.query(criteria), "asset");

        RenewedResponse response = new RenewedResponse();
        response.setRenewedCount(count);
        response.setRenewedassets(replacedAssets);

        return response;
    }

    @Override
    public PendingRequestResponse pendingRequest(RequestWithFilter requestWithFilter, Integer page, Integer size) {
        Sort sortById = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sortById);
        List<Asset> assetsRecords;
        long counts;
        if (requestWithFilter.getAssetClass() != null && !requestWithFilter.getAssetClass().isEmpty()
                && requestWithFilter.getAssetId() != null && !requestWithFilter.getAssetId().isEmpty()
                && requestWithFilter.getChildId() != null && !requestWithFilter.getChildId().isEmpty()) {
            assetsRecords = assetRepository
                    .findByAssetClassAndAssetIdAndChildIdAndAvailableStatusAndCompanyIdAndPlantAndStatus(
                            requestWithFilter.getAssetClass(), requestWithFilter.getAssetId(),
                            requestWithFilter.getChildId(), AvailableStatus.Stock.getValue(),
                            AuthUser.getCompanyId(), AuthUser.getPlant(), ActiveInActive.ACTIVE.getValue(), pageable)
                    .getContent();
        } else if (requestWithFilter.getAssetClass() != null && !requestWithFilter.getAssetClass().isEmpty()
                && requestWithFilter.getAssetId() != null && !requestWithFilter.getAssetId().isEmpty()) {
            assetsRecords = assetRepository.findByAssetClassAndAssetIdAndAvailableStatusAndCompanyIdAndPlantAndStatus(
                    requestWithFilter.getAssetClass(), requestWithFilter.getAssetId(), AvailableStatus.Stock.getValue(),
                    AuthUser.getCompanyId(), AuthUser.getPlant(), ActiveInActive.ACTIVE.getValue(), pageable)
                    .getContent();
        } else if (requestWithFilter.getAssetClass() != null && !requestWithFilter.getAssetClass().isEmpty()) {
            assetsRecords = assetRepository.findByAssetClassAndAvailableStatusAndCompanyIdAndPlantAndStatus(
                    requestWithFilter.getAssetClass(), AvailableStatus.Stock.getValue(),
                    AuthUser.getCompanyId(), AuthUser.getPlant(), ActiveInActive.ACTIVE.getValue(), pageable)
                    .getContent();
        } else {
            assetsRecords = assetRepository.findByAvailableStatusAndCompanyIdAndPlantAndStatus(
                    AvailableStatus.Stock.getValue(), AuthUser.getCompanyId(), AuthUser.getPlant(),
                    ActiveInActive.ACTIVE.getValue(), pageable).getContent();
        }

        List<Audit> pendingRequest = new ArrayList<>();
        for (Asset asset : assetsRecords) {
            List<Audit> audits = auditRepository.findByAssetId(asset.getId(), sortById);
            if (!audits.isEmpty()) {
                Audit auditData = audits.get(0);
                if (auditData.getStatus().equals(AuditStatus.Waiting.getValue())) {
                    pendingRequest.add(auditData);
                }
            }
        }
        PendingRequestResponse response = new PendingRequestResponse();
        counts = pendingRequest.size();
        response.setPendingRequestCounts(counts);
        response.setAssets(pendingRequest);
        return response;
    }

    public Long countPendingRequests() {
        long count = 0;
        Sort sortById = Sort.by(Sort.Direction.DESC, "id");
        List<Asset> assetsRecords = assetRepository.findByAvailableStatusAndCompanyIdAndPlantAndStatus(
                AvailableStatus.Stock.getValue(), AuthUser.getCompanyId(), AuthUser.getPlant(),
                ActiveInActive.ACTIVE.getValue(), sortById);

        for (Asset asset : assetsRecords) {
            List<Audit> audits = auditRepository.findByAssetId(asset.getId(), sortById);
            if (!audits.isEmpty()) {
                Audit auditData = audits.get(0);
                if (auditData.getStatus().equals(AuditStatus.Waiting.getValue())) {
                    count++;
                }
            }
        }
        return count;
    }

    public Long countRejectedRequests() {
        long count = 0;
        List<Audit> audits = auditRepository.findByPlant(AuthUser.getPlant());
        for (Audit audit : audits) {
            if (audit.getStatus().equals(AuditStatus.Rejected.getValue())) {
                count++;
            }
        }
        return count;
    }

}
