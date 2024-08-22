package assetmanagement.serviceImpl.asset;

import assetmanagement.dto.CountResult;
import assetmanagement.dto.TransferDetailDto;
import assetmanagement.enumData.TransferStatus;
import assetmanagement.exception.ResourceNotFoundException;
import assetmanagement.model.transfer.TransferDetail;
import assetmanagement.model.transfer.TransferHistory;
import assetmanagement.repository.transfer.TransferDetailRepository;
import assetmanagement.repository.transfer.TransferHistoryRepository;
import assetmanagement.response.TransferPendingResponse;
import assetmanagement.service.asset.TransferService;
import assetmanagement.util.AuthUser;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {
    private final MongoTemplate mongoTemplate;

    // public TransferServiceImpl(MongoTemplate mongoTemplate) {
    // this.mongoTemplate = mongoTemplate;
    // }

    private final TransferDetailRepository transferDetailRepository;
    private final TransferHistoryRepository transferHistoryRepository;

    @Override
    public TransferDetail create(TransferDetail transferDetail) throws IOException {

        LocalDate today = LocalDate.now();

        transferDetail.setRequestRaisedDate(today);
        transferDetail.setStatus(TransferStatus.Pending.getValue());
        transferDetail.setCompanyId(AuthUser.getCompanyId());
        TransferDetail details = transferDetailRepository.save(transferDetail);
        createHistory(details, details.getStatus());
        return details;
    }

    @Override
    public List<TransferDetail> getAll(String status) {
        return transferDetailRepository.findAllByStatus(status);
    }

    @Override
    public List<TransferDetail> getAllTransferDataByAssetid(String assetId) {
        return transferDetailRepository.findByAssetIdAndStatus(assetId,
                TransferStatus.Approved.getValue());

    }

    @Override
    public TransferDetail updateStatus(String status, String id) {
        Optional<TransferDetail> optionalTransferDetail = transferDetailRepository.findById(id);

        if (optionalTransferDetail.isPresent()) {
            TransferDetail transferDetail = optionalTransferDetail.get();
            transferDetail.setStatus(status);
            TransferDetail detailsUpdate = transferDetailRepository.save(transferDetail);
            createHistory(detailsUpdate, detailsUpdate.getStatus());

            return detailsUpdate;
        }
        throw new ResourceNotFoundException("Data not found");
    }

    @Override
    public TransferDetail bulkAssetUpdateStatus(String status, List<String> ids) {

        for (String id : ids) {
            Optional<TransferDetail> optionalTransferDetail = transferDetailRepository.findById(id);

            if (optionalTransferDetail.isPresent()) {
                TransferDetail transferDetail = optionalTransferDetail.get();
                transferDetail.setStatus(status);
                TransferDetail detailsUpdate = transferDetailRepository.save(transferDetail);
                createHistory(detailsUpdate, detailsUpdate.getStatus());
            } else {
                throw new ResourceNotFoundException("TransferDetail with ID: " + id + " not found");
            }
        }
        return new TransferDetail();
    }

    @Override
    public TransferDetail getById(String id) {
        Optional<TransferDetail> optionalTransferDetail = transferDetailRepository.findById(id);

        if (optionalTransferDetail.isPresent()) {
            TransferDetail transferDetail = optionalTransferDetail.get();
            return transferDetail;
        }
        throw new ResourceNotFoundException("Data not found");
    }

    @Override
    public TransferPendingResponse getDataWithFilter(String assetClass, String assetType, String assetId, String status,
            Integer page, Integer size, String subClass, String childId) {
        if (page == null && size == null || page == null || size == null) {
            page = 0;
            size = 10;
        }

        Criteria criteria = Criteria.where("companyId").is(AuthUser.getCompanyId());
        criteria.and("status").is(status);
        criteria.and("fromPlant").is(AuthUser.getPlant());

        if (assetClass != null && !assetClass.isEmpty()) {
            criteria.and("assetData.assetClass").is(assetClass);
        }
        if (assetType != null && !assetType.isEmpty()) {
            criteria.and("assetData.assetStatus").is(assetType);
        }
        if (assetId != null && !assetId.isEmpty()) {
            criteria.and("assetData.assetId").is(assetId);
        }
        if (subClass != null && !subClass.isEmpty()) {
            criteria.and("assetData.subClass").is(subClass);
        }
        if (childId != null && !childId.isEmpty()) {
            criteria.and("assetData.childId").is(childId);
        }

        AggregationOperation match = Aggregation.match(criteria);

        AggregationOperation lookup = Aggregation.lookup("asset", "assetId", "assetId", "assetData");

        AggregationOperation unwind = Aggregation.unwind("assetData");

        AggregationOperation skip = Aggregation.skip(page * size);

        AggregationOperation limit = Aggregation.limit(size);

        AggregationOperation project = Aggregation.project()
                .and("assetData.assetId").as("assetId")
                .and("assetData.assetClass").as("assetClass")
                .and("assetData.assetStatus").as("assetStatus")
                .and("assetData.assetAgeing").as("assetAgeing")
                .and("assetData.assetLifetime").as("assetLifetime")
                .and("assetData.costOfAsset").as("costOfAsset")
                .and("assetData.picture").as("picture")
                .and("assetData.subClass").as("subClass")
                .and("assetData.childId").as("childId")
                .and("fromPlant").as("fromPlant")
                .and("toPlant").as("toPlant")
                .and("requestRaisedDate").as("requestRaisedDate")
                .and("actionDate").as("actionDate")
                .and("status").as("status")
                .and("companyId").as("companyId")
                .and("createdBy").as("createdBy")
                .and("updatedBy").as("updatedBy")
                .and("createdAt").as("createdAt")
                .and("updatedAt").as("updatedAt");

        Aggregation countAggregation = Aggregation.newAggregation(
                lookup,
                match,
                Aggregation.group().count().as("total"));

        Aggregation aggregation = Aggregation.newAggregation(
                lookup,
                unwind,
                match,
                skip,
                limit,
                project);

        TransferPendingResponse response = new TransferPendingResponse();

        AggregationResults<TransferDetailDto> results = mongoTemplate.aggregate(aggregation, "transfer_details",
                TransferDetailDto.class);
        List<TransferDetailDto> data = results.getMappedResults();

        CountResult result = mongoTemplate.aggregate(countAggregation, "transfer_details", CountResult.class)
                .getUniqueMappedResult();
        long countResults = result != null ? result.getTotal() : 0L;

        response.setTransferDetailDto(data);
        response.setTransferDetailsCount(countResults);

        return response;

    }

    // @Override
    // public List<TransferDetailDto> getDataForFilter(String assetClass, String
    // assetType, String assetId, String status) {

    // // Define group fields dynamically
    // List<String> groupFields = new ArrayList<>();
    // groupFields.add("assetData.assetId"); // Always group by assetId

    // Criteria criteria = Criteria.where("companyId").is(AuthUser.getCompanyId());
    // criteria.and("status").is(status);

    // if (assetClass != null && !assetClass.isEmpty()) {
    // criteria.and("assetData.assetClass").is(assetClass);
    // groupFields.add("assetStatus.assetStatus");
    // }else{
    // groupFields.add("assetStatus.assetStatus");
    // }

    // if (assetType != null && !assetType.isEmpty()) {
    // criteria.and("assetData.assetStatus").is(assetType);
    // }
    // if (assetId != null && !assetId.isEmpty()) {
    // criteria.and("assetData.assetId").is(assetId);
    // }

    // AggregationOperation[] operations = new AggregationOperation[]{
    // Aggregation.lookup("asset", "assetId", "assetId", "assetData"),
    // Aggregation.unwind("assetData"),
    // Aggregation.match(criteria)
    // };

    // AggregationOperation groupOperation =
    // Aggregation.group(groupFields.toArray(new String[0]))
    // // .first("auditBy").as("auditBy")
    // .first("assetData.assetId").as("assetId")
    // .first("assetData.assetClass").as("assetClass")
    // .first("assetData.assetStatus").as("assetStatus");

    // List<AggregationOperation> aggregationOperations = new
    // ArrayList<>(Arrays.asList(operations));
    // aggregationOperations.add(groupOperation);

    // Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);

    // return mongoTemplate.aggregate(
    // aggregation, "transfer_details", TransferDetailDto.class
    // ).getMappedResults();
    // }

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

}
