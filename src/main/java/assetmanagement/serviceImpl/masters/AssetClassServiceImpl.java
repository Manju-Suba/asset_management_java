package assetmanagement.serviceImpl.masters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import assetmanagement.model.masters.AssetClass;
import assetmanagement.repository.masters.AssetClassRepository;
import assetmanagement.repository.masters.CheckListRepository;
import assetmanagement.repository.masters.PlantRepository;
import assetmanagement.response.AssetClassResponse;
import assetmanagement.response.PlantDTO;
import assetmanagement.response.PlantResponse;
import assetmanagement.service.masters.AssetClassService;
import assetmanagement.util.AuthUser;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssetClassServiceImpl implements AssetClassService {

    public final AssetClassRepository assetClassRepository;
    public final PlantRepository plantRepository;
    public final CheckListRepository checkListRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public AssetClassResponse getAllAssetClass(Integer page, Integer size) {

        if (page == null && size == null || page == null || size == null) {
            page = 0;
            size = 10;
        }

        long counts = assetClassRepository.countByCompanyIdAndPlant(AuthUser.getCompanyId(), AuthUser.getPlant());
        PageRequest pageable = PageRequest.of(page, size);
        Page<AssetClass> pageAssetClasses = assetClassRepository.findByCompanyIdAndPlant(AuthUser.getCompanyId(),
                AuthUser.getPlant(), pageable);
        List<AssetClass> assetClasses = pageAssetClasses.getContent();
        AssetClassResponse response = new AssetClassResponse();
        response.setAssetClasses(assetClasses);
        response.setAssetClassCount(counts);
        return response;
    }

    @Override
    public List<AssetClass> getAllAssetClass() {
        return assetClassRepository.findByCompanyIdAndPlant(AuthUser.getCompanyId(), AuthUser.getPlant());
    }

    @Override
    public PlantResponse getAllPlant(Integer page, Integer size) {

        if (page == null && size == null || page == null || size == null) {
            PlantResponse response = new PlantResponse();

            List<PlantDTO> streamPlantResponse = plantRepository.findByCompanyId(AuthUser.getCompanyId())
                    .stream()
                    .map(asset -> new PlantDTO(asset.getId(), asset.getPlant()))
                    .collect(Collectors.toList());
            response.setPlant(streamPlantResponse);
            return response;
        } else {
            long counts = plantRepository.countByCompanyId(AuthUser.getCompanyId());
            PageRequest pageable = PageRequest.of(page, size);
            PlantResponse response = new PlantResponse();

            List<PlantDTO> streamPlantResponse = plantRepository.findByCompanyId(AuthUser.getCompanyId(), pageable)
                    .stream()
                    .map(asset -> new PlantDTO(asset.getId(), asset.getPlant()))
                    .collect(Collectors.toList());
            response.setPlantCounts(counts);
            response.setPlant(streamPlantResponse);
            return response;
        }
    }

    @Override
    public List<AssetClass> getCheckListAssetClass() {

        List<AssetClass> assetClassList = assetClassRepository.findByCompanyIdAndPlant(AuthUser.getCompanyId(),
                AuthUser.getPlant());
        List<AssetClass> result = new ArrayList<>();
        for (AssetClass assetClass : assetClassList) {
            Boolean isAssetClass = checkListRepository.existsByAssetClassAndStatus(assetClass.getAssetClass(), true);
            if (!isAssetClass) {
                result.add(assetClass);
            }
        }

        return result;

        // AddFieldsOperation convertAssetClassId =
        // Aggregation.addFields().addField("assetClassId")
        // .withValue(ConvertOperators.ToObjectId.toObjectId("$assetClass")).build();

        // Aggregation aggregation = Aggregation.newAggregation(
        // Aggregation.match(Criteria.where("companyId").is(AuthUser.getCompanyId())
        // .and("plant").is(AuthUser.getPlant())),
        // Aggregation.addFields().addField("assetClassId")
        // .withValue(ConvertOperators.ToObjectId.toObjectId("$assetClass")).build(),
        // Aggregation.lookup("checkList", "_id", "assetClassId", "checkList"),
        // Aggregation.unwind("checkList"),
        // Aggregation.unwind("assetClass")
        // // Aggregation.match(Criteria.where("checkList.status").is(true))
        // // Aggregation.group("_id").push("$$ROOT").as("assets"),
        // // Aggregation.replaceRoot("assets")
        // );

        // AggregationResults<AssetClass> results = mongoTemplate.aggregate(aggregation,
        // "asset_class", AssetClass.class);
        // List<AssetClass> result = results.getMappedResults();
        // return result;
    }

}
