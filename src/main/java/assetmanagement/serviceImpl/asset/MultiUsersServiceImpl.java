package assetmanagement.serviceImpl.asset;

import assetmanagement.dto.MultiUsersTransferDTO;
import assetmanagement.enumData.ActiveInActive;
import assetmanagement.enumData.AvailableStatus;
import assetmanagement.enumData.DisposedStatus;
import assetmanagement.enumData.TransferStatus;
import assetmanagement.exception.ResourceNotFoundException;
import assetmanagement.model.Asset;
import assetmanagement.model.ScrappedDetails;
import assetmanagement.model.transfer.TransferDetail;
import assetmanagement.model.transfer.TransferHistory;
import assetmanagement.repository.ScrappedDetailsRepository;
import assetmanagement.repository.asset.AssetRepository;
import assetmanagement.repository.transfer.TransferDetailRepository;
import assetmanagement.repository.transfer.TransferHistoryRepository;
import assetmanagement.request.RequestWithFilter;
import assetmanagement.request.ScrapRequest;
import assetmanagement.response.*;
import assetmanagement.service.masters.MultiUsersService;
import assetmanagement.util.AuthUser;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MultiUsersServiceImpl implements MultiUsersService {

    private final AssetRepository assetRepository;
    private final ScrappedDetailsRepository scrappedDetailsRepository;
    private final TransferDetailRepository transferDetailRepository;
    private final TransferHistoryRepository transferHistoryRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public AssetListResponse getAllAsset(String assetClass, String assetStatus, String assetId,
                                         String childId, Boolean search, String value, Integer page, Integer size) {

        String availStatus = AvailableStatus.Stock.getValue();
        page = page == null ? 0 : page;
        size = size == null ? 10 : size;
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        AssetListResponse response = new AssetListResponse();

        Criteria criteria = Criteria.where("companyId").is(AuthUser.getCompanyId())
                .and("plant").is(AuthUser.getPlant())
                .and("status").is(ActiveInActive.ACTIVE.getValue())
                .and("availableStatus").is(availStatus);

        if (assetClass != null) {
            criteria.and("assetClass").is(assetClass);
        }
        if (assetStatus != null) {
            criteria.and("assetStatus").is(assetStatus);
        }
        if (assetId != null) {
            criteria.and("assetId").is(assetId);
        }
        if (childId != null) {
            criteria.and("childId").is(childId);
        }
        if (search && value != null && !value.isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("assetId").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("assetClass").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("assetStatus").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("childId").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("serialNumber").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)));
            criteria.andOperator(searchCriteria);
        }

        Query query = new Query(criteria);
        long totalCount = mongoTemplate.count(query, Asset.class);
        query.with(pageable);
        List<Asset> assetList = mongoTemplate.find(query, Asset.class);
        response.setAssetsCount(totalCount);
        response.setAssets(assetList);
        return response;
    }

    @Override
    public ScrapPendingResponse getAllScrapped(RequestWithFilter requestWithFilter, Boolean search, String value,
                                               Integer page, Integer size) {
        page = page == null ? 0 : page;
        size = size == null ? 10 : size;
        Criteria criteria = Criteria.where("companyId").is(AuthUser.getCompanyId())
                .and("plant").is(AuthUser.getPlant())
                .and("status").is(requestWithFilter.getStatus());

        if (requestWithFilter.getAssetClass() != null) {
            criteria = criteria.and("assetClass").is(requestWithFilter.getAssetClass());
        }
        if (requestWithFilter.getAssetId() != null) {
            criteria.and("assetId").is(requestWithFilter.getAssetId());
        }
        if (requestWithFilter.getChildId() != null) {
            criteria.and("childId").is(requestWithFilter.getChildId());
        }
        if (search && value != null && !value.isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("assetId").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("assetClass").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)));
            criteria.andOperator(searchCriteria);
        }

        long count = getScrappedCount(requestWithFilter, search, value);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.lookup("asset", "objectId", "_id", "latestScrapped"),
                Aggregation.unwind("latestScrapped"),
                Aggregation.sort(Sort.Direction.DESC, "createdAt"),
                Aggregation.group("$objectId")
                        .first("_id").as("id")
                        .first("_id").as("oid")
                        .first("assetId").as("assetId")
                        .first("assetClass").as("assetClass")
                        .first("latestScrapped.picture").as("getPictureWithPath()")
                        .first("objectId").as("objectId")
                        .first("childId").as("childId")
                        .first("status").as("status")
                        .first("remarks").as("remarks")
                        .first("companyId").as("companyId")
                        .first("plant").as("plant")
                        .first("createdAt").as("createdAt")
                        .first("updatedAt").as("updatedAt")
                        .first("createdBy").as("createdBy")
                        .first("updatedBy").as("updatedBy"),
                Aggregation.sort(Sort.Direction.DESC, "createdAt"),
                Aggregation.skip((long) page * size),
                Aggregation.limit(size),
                Aggregation.project()
                        .andInclude("_id")
                        .and("oid").as("assetId") // assuming oid is the assetId
                        .and("assetId").as("assetId")
                        .and("objectId").as("objectId")
                        .and("assetClass").as("assetClass")
                        .and("getPictureWithPath()").as("picture")
                        .and("childId").as("childId")
                        .and("status").as("status")
                        .and("remarks").as("remarks")
                        .and("companyId").as("companyId")
                        .and("plant").as("plant")
                        .and("createdAt").as("createdAt")
                        .and("updatedAt").as("updatedAt")
                        .and("createdBy").as("createdBy")
                        .and("updatedBy").as("updatedBy")
                        .and("oid").as("_id") // duplicate field just to set correct _id
        );

        AggregationResults<ScrappedDetailsDTO> aggregationResults = mongoTemplate.aggregate(aggregation,
                "scrappedDetails", ScrappedDetailsDTO.class);
        List<ScrappedDetailsDTO> scrappedResponses = aggregationResults.getMappedResults();
        ScrapPendingResponse response = new ScrapPendingResponse();
        response.setWaitingAssets(scrappedResponses);
        response.setPendingRequestCounts(count);
        return response;
    }

    @Override
    public MultiUsersTransferPendingResponse getAllTransferred(RequestWithFilter requestWithFilter, Boolean search,
                                                               String value,
                                                               Integer page, Integer size) {

        page = page == null ? 0 : page;
        size = size == null ? 10 : size;

        Criteria criteria = Criteria.where("companyId").is(AuthUser.getCompanyId())
                .and("fromPlant").is(AuthUser.getPlant())
                .and("status").is(requestWithFilter.getStatus());

        if (requestWithFilter.getAssetId() != null) {
            criteria.and("assetId").is(requestWithFilter.getAssetId());
        }
        if (requestWithFilter.getAssetClass() != null) {
            criteria.and("assetClass").is(requestWithFilter.getAssetClass());
        }
        if (requestWithFilter.getAssetStatus() != null) {
            criteria.and("latestTransferred.assetStatus").is(requestWithFilter.getAssetStatus());
        }
        if (search && value != null && !value.isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("assetId").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("toPlant").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("latestTransferred.assetClass")
                            .regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)));
            criteria.andOperator(searchCriteria);
        }

        long count = getTransferredCount(requestWithFilter, search, value);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup("asset", "assetId", "assetId", "latestTransferred"),

                Aggregation.unwind("latestTransferred"),
                Aggregation.match(criteria),

                Aggregation.sort(Sort.Direction.DESC, "createdAt"),
                Aggregation.group("$latestTransferred.assetId")
                        .first("_id").as("id")
                        .first("_id").as("oid")
                        .first("companyId").as("companyId")
                        .first("assetId").as("assetId")
                        .first("latestTransferred.assetClass").as("assetClass")
                        .first("latestTransferred.picture").as("getPictureWithPath()")
                        .first("fromPlant").as("fromPlant")
                        .first("toPlant").as("toPlant")
                        .first("status").as("status")
                        .first("remarks").as("remarks")
                        .first("createdAt").as("createdAt")
                        .first("updatedAt").as("updatedAt")
                        .first("createdBy").as("createdBy")
                        .first("updatedBy").as("updatedBy"),
                Aggregation.sort(Sort.Direction.DESC, "createdAt"),
                Aggregation.skip((long) page * size),
                Aggregation.limit(size),
                Aggregation.project()
                        .andInclude("_id")
                        .and("oid").as("_id")
                        .and("companyId").as("companyId")
                        .and("assetId").as("assetId")
                        .and("assetClass").as("assetClass")
                        .and("getPictureWithPath()").as("picture")
                        .and("fromPlant").as("fromPlant")
                        .and("toPlant").as("toPlant")
                        .and("status").as("status")
                        .and("remarks").as("remarks")
                        .and("createdAt").as("createdAt")
                        .and("updatedAt").as("updatedAt")
                        .and("createdBy").as("createdBy")
                        .and("updatedBy").as("updatedBy"));

        AggregationResults<MultiUsersTransferDTO> aggregationResults = mongoTemplate.aggregate(aggregation,
                "transfer_details", MultiUsersTransferDTO.class);
        List<MultiUsersTransferDTO> transferredResponses = aggregationResults.getMappedResults();
        MultiUsersTransferPendingResponse response = new MultiUsersTransferPendingResponse();
        response.setTransferDetails(transferredResponses);
        response.setTransferDetailsCount(count);
        return response;
    }

    @Override
    public ScrappedDetails createScrapRequest(ScrapRequest scrapRequest) {
        Optional<Asset> assetOptional = assetRepository.findById(scrapRequest.getObjectId());
        return assetOptional
                .map(asset -> {
                    asset.setAvailableStatus(AvailableStatus.Waiting.getValue());
                    assetRepository.save(asset);
                    ScrappedDetails assetDetails = new ScrappedDetails();
                    assetDetails.setAssetClass(asset.getAssetClass());
                    assetDetails.setAssetId(asset.getAssetId());
                    String objectIdString = asset.getId();
                    ObjectId objectId = new ObjectId(objectIdString);
                    assetDetails.setObjectId(objectId);
                    assetDetails.setChildId(asset.getChildId());
                    assetDetails.setStatus(AvailableStatus.Waiting.getValue());
                    asset.setAvailableStatus(AvailableStatus.Waiting.getValue());
                    assetDetails.setRemarks(scrapRequest.getRemark());
                    assetDetails.setCompanyId(AuthUser.getCompanyId());
                    assetDetails.setPlant(AuthUser.getPlant());
                    scrappedDetailsRepository.save(assetDetails);
                    return assetDetails;
                })
                .orElseThrow(
                        () -> new ResourceNotFoundException("Asset not found with ID: " + scrapRequest.getObjectId()));

    }

    @Override
    public TransferDetail create(TransferDetail transferDetail) throws IOException {
        List<Asset> assets = assetRepository.findByAssetIdAndPlant(transferDetail.getAssetId(), AuthUser.getPlant());
        if (!assets.isEmpty()) {
            for (Asset asset : assets) {
                asset.setAvailableStatus(AvailableStatus.Waiting.getValue());
                assetRepository.save(asset);
                transferDetail.setAssetClass(asset.getAssetClass());
                transferDetail.setChildId(asset.getChildId());
            }
            LocalDate today = LocalDate.now();
            transferDetail.setRequestRaisedDate(today);
            transferDetail.setStatus(TransferStatus.Pending.getValue());
            transferDetail.setCompanyId(AuthUser.getCompanyId());
            TransferDetail details = transferDetailRepository.save(transferDetail);
            createHistory(details, details.getStatus());
            return details;
        } else {
            throw new ResourceNotFoundException("Asset not found with ID: " + transferDetail.getAssetId());
        }

    }

    public void createHistory(TransferDetail transferId, String status) throws IOException {
        TransferHistory history = new TransferHistory();
        if (status.equals(TransferStatus.Pending.getValue())) {
            history.setStatusFrom(TransferStatus.Created.getValue());
            history.setStatusTo(status);
        } else if (status.equals(TransferStatus.Approved.getValue())) {
            history.setStatusFrom(TransferStatus.Pending.getValue());
            history.setStatusTo(status);
        } else if (status.equals(TransferStatus.Rejected.getValue())) {
            history.setStatusFrom(TransferStatus.Pending.getValue());
            history.setStatusTo(status);
        }
        history.setTransferId(transferId);
        transferHistoryRepository.save(history);
    }

    @Override
    public TransferDetail bulkAssetUpdateStatus(String id, String assetId, String status) {

        Optional<TransferDetail> transferDetails = transferDetailRepository.findById(id);
        List<Asset> assetDetails = assetRepository.findByAssetIdAndPlant(assetId, AuthUser.getPlant());
        if (transferDetails.isEmpty()) {
            throw new ResourceNotFoundException("TransferDetail with ID: " + id + " not found");
        }
        if (transferDetails.isPresent()) {
            TransferDetail transferDetail = transferDetails.get();
            transferDetail.setStatus(status);
            TransferDetail detailsUpdate = transferDetailRepository.save(transferDetail);
            createHistory(detailsUpdate, detailsUpdate.getStatus());
            if (!assetDetails.isEmpty()) {
                for (Asset assets : assetDetails) {
                    if (status.equals(TransferStatus.Approved.getValue())) {
                        assets.setPlant(transferDetail.getToPlant());
                        assets.setAvailableStatus(AvailableStatus.Stock.getValue());
                        assetRepository.save(assets);
                    }
                    if (status.equals(TransferStatus.Rejected.getValue())) {
                        assets.setAvailableStatus(AvailableStatus.Stock.getValue());
                        assetRepository.save(assets);
                    }
                }
            }
        }
        return transferDetails.get();
    }

    @Override
    public ScrappedDetails bulkScrapUpdateStatus(String assetId, String status) {

        String objectIdString = assetId;
        ObjectId objectId = new ObjectId(objectIdString); // To find objectId field in scrappedDetails

        Optional<ScrappedDetails> scrapDetails = scrappedDetailsRepository.findById(objectId);
        if (scrapDetails.isEmpty()) {
            throw new ResourceNotFoundException("Scrapped with ID: " + assetId + " not found");
        }
        if (scrapDetails.isPresent()) {
            if (status.equals(TransferStatus.Approved.getValue())
                    || status.equals(TransferStatus.Rejected.getValue())) {
                scrapDetails.get().setStatus(status);
                scrappedDetailsRepository.save(scrapDetails.get());
                ObjectId oid = scrapDetails.get().getObjectId();
                Optional<Asset> assetDetails = assetRepository.findById(oid);
                if (assetDetails.isPresent()) {
                    Asset assets = assetDetails.get();
                    if (status.equals(TransferStatus.Approved.getValue())) {
                        assets.setAvailableStatus(DisposedStatus.Disposed.getValue());
                        assets.setAssetStatus(AvailableStatus.Scrap.getValue());
                        assetRepository.save(assets);
                    }
                    if (status.equals(TransferStatus.Rejected.getValue())) {
                        assets.setAvailableStatus(AvailableStatus.Stock.getValue());
                        assetRepository.save(assets);
                    }
                }
            }
        }
        return scrapDetails.get();
    }

    @Override
    public long getTransferredCount(RequestWithFilter requestWithFilter, Boolean search, String value) {
        Criteria criteria = Criteria.where("companyId").is(AuthUser.getCompanyId())
                .and("fromPlant").is(AuthUser.getPlant())
                .and("status").is(requestWithFilter.getStatus());
        if (requestWithFilter.getAssetId() != null) {
            criteria.and("assetId").is(requestWithFilter.getAssetId());
        }
        if (requestWithFilter.getAssetClass() != null) {
            criteria.and("assetClass").is(requestWithFilter.getAssetClass());
        }
        if (requestWithFilter.getAssetStatus() != null) {
            criteria.and("latestTransferred.assetStatus").is(requestWithFilter.getAssetStatus());
        }
        if (search && value != null && !value.isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("assetId").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("toPlant").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("latestTransferred.assetClass")
                            .regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)));
            criteria.andOperator(searchCriteria);
        }
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup("asset", "assetId", "assetId", "latestTransferred"),

                Aggregation.unwind("latestTransferred"),
                Aggregation.match(criteria),
                Aggregation.group("latestTransferred.assetId"));
        AggregationResults<CountDTO> aggregationResults = mongoTemplate.aggregate(aggregation, "transfer_details",
                CountDTO.class);
        List<CountDTO> counts = aggregationResults.getMappedResults();
        return counts.size();
    }

    @Override
    public long getScrappedCount(RequestWithFilter requestWithFilter, Boolean search, String value) {
        Criteria criteria = Criteria.where("companyId").is(AuthUser.getCompanyId())
                .and("plant").is(AuthUser.getPlant())
                .and("status").is(requestWithFilter.getStatus());

        if (requestWithFilter.getAssetClass() != null) {
            criteria = criteria.and("assetClass").is(requestWithFilter.getAssetClass());
        }
        if (requestWithFilter.getAssetId() != null) {
            criteria.and("assetId").is(requestWithFilter.getAssetId());
        }
        if (requestWithFilter.getChildId() != null) {
            criteria.and("childId").is(requestWithFilter.getChildId());
        }

        if (search && value != null && !value.isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("assetId").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("assetClass").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)));
            criteria.andOperator(searchCriteria);
        }
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("objectId"));
        AggregationResults<CountDTO> aggregationResults = mongoTemplate.aggregate(aggregation, "scrappedDetails",
                CountDTO.class);
        List<CountDTO> counts = aggregationResults.getMappedResults();
        return counts.size();
    }

}
