package assetmanagement.serviceImpl.observation;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import assetmanagement.model.audit.Observation;
import assetmanagement.repository.observation.ObservationRepository;
import assetmanagement.response.CountDTO;
import assetmanagement.response.ObservationCount;
import assetmanagement.response.ObservationResponse;
import assetmanagement.service.observation.ObservationService;
import assetmanagement.util.AuthUser;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ObservationServiceImpl implements ObservationService {

    private final ObservationRepository observationRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public Observation create(Observation observation) {
        Observation observation2 = new Observation();
        observation2.setCompanyId(AuthUser.getCompanyId());
        observation2.setPlant(AuthUser.getPlant());
        observation2.setAssetId(observation.getAssetId());
        observation2.setObservationDate(observation.getObservationDate());
        observation2.setRemarks(observation.getRemarks());
        observation2.setCreatedAt(observation.getCreatedAt());
        observation2.setCreatedBy(observation.getCreatedBy());
        observation2.setUpdatedAt(observation.getUpdatedAt());
        observation2.setUpdatedBy(observation.getUpdatedBy());

        return observationRepository.save(observation2);
    }

    @Override
    public ObservationCount getList(String assetClass, boolean search, String value, Integer page, Integer size) {
        page = page == null ? 0 : page;
        size = size == null ? 10 : size;
        Criteria criteria = Criteria.where("companyId").is(AuthUser.getCompanyId())
                .and("plant").is(AuthUser.getPlant());

        if (assetClass != null) {
            criteria.and("assetData.assetClass").is(assetClass);
        }
        if (search && value != null && !value.isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("assetData.assetClass").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("assetId").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)));
            criteria.andOperator(searchCriteria);
        }
        long count = getCount(assetClass, search, value);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup("asset", "assetId", "assetId", "assetData"),
                Aggregation.unwind("assetData"),
                Aggregation.match(criteria),
                Aggregation.sort(Sort.Direction.DESC, "createdAt"),
                Aggregation.group("$assetId")
                        .first("assetData.assetId").as("assetId")
                        .first("assetData.assetClass").as("assetClass")
                        .first("assetData.plant").as("plant")
                        .first("observationDate").as("observationDate")
                        .first("assetData.picture").as("previousImage")
                        .first("assetData.picture").as("previousImageWithPath")
                        .first("remarks").as("remarks"),
                Aggregation.sort(Sort.Direction.DESC, "remarks"),
                Aggregation.project()
                        .and("assetId").as("assetId")
                        .and("assetClass").as("assetClass")
                        .and("plant").as("plant")
                        .and("observationDate").as("observationDate")
                        .and("previousImage").as("previousImage")
                        .and("previousImageWithPath").as("previousImageWithPath")
                        .and("remarks").as("remarks"),
                Aggregation.skip(page * size),
                Aggregation.limit(size));

        AggregationResults<ObservationResponse> aggregationResults = mongoTemplate.aggregate(aggregation, "observation",
                ObservationResponse.class);

        List<ObservationResponse> dataList = aggregationResults.getMappedResults();
        ObservationCount response = new ObservationCount();
        response.setObservationCount(count);
        response.setObservationData(dataList);
        return response;
    }

    private long getCount(String assetClass, boolean search, String value) {

        Criteria criteria = Criteria.where("companyId").is(AuthUser.getCompanyId())
                .and("plant").is(AuthUser.getPlant());

        if (assetClass != null) {
            criteria.and("assetData.assetClass").is(assetClass);
        }
        if (search && value != null && !value.isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("assetData.assetClass").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("assetId").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)));
            criteria.andOperator(searchCriteria);
        }
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup("asset", "assetId", "assetId", "assetData"),
                Aggregation.unwind("assetData"),
                Aggregation.match(criteria),
                Aggregation.sort(Sort.Direction.DESC, "createdAt"),
                Aggregation.group("$assetId")
                        .first("assetData.assetId").as("assetId")
                        .first("assetData.assetClass").as("assetClass")
                        .first("assetData.plant").as("plant")
                        .first("observationDate").as("observationDate")
                        .first("assetData.picture").as("previousImage")
                        .first("remarks").as("remarks"),
                Aggregation.sort(Sort.Direction.DESC, "remarks"),
                Aggregation.project()
                        .and("assetId").as("assetId")
                        .and("assetClass").as("assetClass")
                        .and("plant").as("plant")
                        .and("observationDate").as("observationDate")
                        .and("previousImage").as("previousImage")
                        .and("remarks").as("remarks"));

        AggregationResults<CountDTO> aggregationResults = mongoTemplate.aggregate(aggregation, "observation",
                CountDTO.class);
        List<CountDTO> counts = aggregationResults.getMappedResults();
        return counts.size();
    }

    @Override
    public ObservationCount getParticularList(String assetId, Integer page, Integer size) {
        page = page == null ? 0 : page;
        size = size == null ? 10 : size;
        Criteria criteria = Criteria.where("companyId").is(AuthUser.getCompanyId())
                .and("plant").is(AuthUser.getPlant())
                .and("assetId").is(assetId);
        long count = getParticularCount(assetId);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.lookup("asset", "assetId", "assetId", "assetData"),
                Aggregation.unwind("assetData"),
                Aggregation.sort(Sort.Direction.DESC, "observationDate"),
                Aggregation.project()
                        .and("assetId").as("assetId")
                        .and("assetData.assetClass").as("assetClass")
                        .and("plant").as("plant")
                        .and("observationDate").as("observationDate")
                        .and("assetData.picture").as("previousImage")
                        .and("remarks").as("remarks"),
                Aggregation.skip(page * size),
                Aggregation.limit(size));
        AggregationResults<ObservationResponse> aggregationResults = mongoTemplate.aggregate(aggregation, "observation",
                ObservationResponse.class);
        List<ObservationResponse> dataList = aggregationResults.getMappedResults();
        ObservationCount response = new ObservationCount();
        response.setObservationCount(count);
        response.setObservationData(dataList);
        return response;
    }

    private long getParticularCount(String assetId) {
        Criteria criteria = Criteria.where("companyId").is(AuthUser.getCompanyId())
                .and("plant").is(AuthUser.getPlant())
                .and("assetId").is(assetId);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.lookup("asset", "assetId", "assetId", "assetData"),
                Aggregation.unwind("assetData"),
                Aggregation.sort(Sort.Direction.DESC, "observationDate"),
                Aggregation.project()
                        .and("assetId").as("assetId")
                        .and("assetData.assetClass").as("assetClass")
                        .and("plant").as("plant")
                        .and("observationDate").as("observationDate")
                        .and("assetData.picture").as("previousImage")
                        .and("remarks").as("remarks"));
        AggregationResults<CountDTO> aggregationResults = mongoTemplate.aggregate(aggregation, "observation",
                CountDTO.class);
        List<CountDTO> counts = aggregationResults.getMappedResults();
        return counts.size();
    }

}
