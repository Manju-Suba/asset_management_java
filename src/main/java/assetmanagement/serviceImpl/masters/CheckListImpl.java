package assetmanagement.serviceImpl.masters;

import assetmanagement.dto.CheckListDto;
import assetmanagement.exception.ResourceNotFoundException;
import assetmanagement.model.CheckList;
import assetmanagement.repository.masters.CheckListRepository;
import assetmanagement.response.CheckListResponse;
import assetmanagement.response.DisposedResponse;
import assetmanagement.service.masters.CheckListService;
import assetmanagement.util.AuthUser;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CheckListImpl implements CheckListService {

    public final CheckListRepository checkListRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public CheckList saveCheckList(CheckListDto checkListDto) {

        if (checkListRepository.existsByAssetClassAndStatus(checkListDto.getAssetClass(), true)) {
            throw new IllegalArgumentException("Asset Class Entry already exist ");
        }
        CheckList saveCheckList = new CheckList();
        saveCheckList.setAssetClass(checkListDto.getAssetClass());
        saveCheckList.setCheckList(checkListDto.getCheckList());
        saveCheckList.setCompanyId(AuthUser.getCompanyId());
        saveCheckList.setPlant(AuthUser.getPlant());
        checkListRepository.save(saveCheckList);

        return saveCheckList;
    }

    @Override
    public CheckListResponse getCheckList(Integer page, Integer size, Boolean search, String value, String assetClass) {

        page = page == null ? 0 : page;
        size = size == null ? 10 : size;

        Criteria criteria = Criteria.where("companyId").is(AuthUser.getCompanyId())
                .and("plant").is(AuthUser.getPlant())
                .and("status").is(true);

        if (assetClass != null && !assetClass.isEmpty()) {
            criteria.and("assetClass").is(assetClass);
        }

        if (search && value != null && !value.isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("checkList").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("assetClass").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)));
            criteria.andOperator(searchCriteria);
        }

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.skip((long) page * size),
                Aggregation.limit(size));

        AggregationResults<CheckList> aggregationResults = mongoTemplate.aggregate(aggregation, "checkList",
                CheckList.class);
        List<CheckList> checkListResponses = aggregationResults.getMappedResults();
        long count = mongoTemplate.count(Query.query(criteria), "checkList");
        CheckListResponse checkListResponse = new CheckListResponse();
        checkListResponse.setCheckListCount(count);
        checkListResponse.setCheckListResponse(checkListResponses);

        return checkListResponse;
        // Pageable pageable = PageRequest.of(page, size);
        // long checkListCount =
        // checkListRepository.countByCompanyIdAndPlantAndStatus(AuthUser.getCompanyId(),
        // AuthUser.getPlant(), true);
        // List<CheckList> checkListData =
        // checkListRepository.findByCompanyIdAndPlantAndStatus(AuthUser.getCompanyId(),
        // AuthUser.getPlant(), true,
        // pageable);
        // Map<String, Object> result = new HashMap<>();
        // result.put("checkListCount", checkListCount);
        // result.put("checkListResponse", checkListData);
        // return result;
    }

    @Override
    public CheckList update(CheckListDto checkListDto) {
        Optional<CheckList> checkList = checkListRepository.findById(checkListDto.getId());
        if (checkList.isPresent()) {
            CheckList toUpdate = checkList.get();
            toUpdate.setCheckList(checkListDto.getCheckList());
            checkListRepository.save(toUpdate);
            return toUpdate;
        }
        throw new ResourceNotFoundException("Id not found");
    }

    @Override
    public CheckList delete(String id) {

        Optional<CheckList> checkList = checkListRepository.findById(id);

        if (checkList.isPresent()) {
            CheckList deletedData = checkList.get();
            deletedData.setStatus(false);
            checkListRepository.save(deletedData);
            return deletedData;
        }
        throw new ResourceNotFoundException("Id not found");
    }

    @Override
    public CheckList getCheckListByAssetClass(String assetClass) {
        return checkListRepository.findByAssetClassAndPlantAndCompanyId(assetClass, AuthUser.getPlant(),
                AuthUser.getCompanyId());
    }

    @Override
    public CheckList getByAssetClass(String assetClass, String companyId, String plant) {
        return checkListRepository.findByAssetClassAndPlantAndCompanyId(assetClass, plant, companyId);
    }

    @Override
    public CheckList getCheckListToAudit(String assetClass, String plant, String companyId) {
        return checkListRepository.findByAssetClassAndPlantAndCompanyId(assetClass, plant, companyId);
    }

}
