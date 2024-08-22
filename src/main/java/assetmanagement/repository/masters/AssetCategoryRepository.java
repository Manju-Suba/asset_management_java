package assetmanagement.repository.masters;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import assetmanagement.model.masters.AssetCategory;


@Repository
public interface AssetCategoryRepository extends MongoRepository<AssetCategory,String>{
    boolean existsByNameIgnoreCaseAndCompanyIdAndStatusAndIdNot(String name,String companyId,String status,String Id);
    boolean existsByNameIgnoreCaseAndCompanyIdAndStatus(String name,String companyId,String status);
    
    Optional<AssetCategory> findByIdAndStatus(String Id,String status);

    List<AssetCategory> findAllByCompanyIdAndPlantAndStatus(String companyId,String plant,String status);

    Optional<AssetCategory> findByNameContainingIgnoreCaseAndCompanyIdAndStatus(String categoryName,String companyId,String status);

}
