package assetmanagement.repository.masters;

import assetmanagement.model.CheckList;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CheckListRepository extends MongoRepository<CheckList, String> {

    Boolean existsByAssetClassAndStatus(String assetClass, Boolean status);

    List<CheckList> findByCompanyIdAndPlantAndStatus(String companyId, String plant, Boolean status,Pageable pageable);

    long countByCompanyIdAndPlantAndStatus(String companyId, String plant, Boolean status);

    CheckList findByAssetClassAndPlantAndCompanyId(String assetClass, String plant, String companyId);
}
