package assetmanagement.repository.masters;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import assetmanagement.model.masters.AssetClass;


public interface AssetClassRepository extends MongoRepository<AssetClass, String> {

    boolean existsByAssetClass(String assetClass);

    List<AssetClass> findByCompanyIdAndPlant(String companyId,String plant);

    List<AssetClass> findByCompanyIdAndPlantAndAssetClass(String companyId,String plant,String assetClass);


    //List<AssetClass> findByCompanyIdAndPlantAndAssetClass(String companyId,String plant,String assetClass);

    Page<AssetClass> findByCompanyIdAndPlant(String companyId,String plant,Pageable pageable);

   @Query("{'companyId': ?0, 'plant': ?1, 'assetClass': { $regex: ?2, $options: 'i' }}")
   Page<AssetClass> findByCompanyIdAndPlantAndAssetClass(String companyId, String plant, String value,Pageable pageable);

    long countByCompanyIdAndPlant(String companyId,String plant);

    List<AssetClass> findByCompanyId(String companyId);

    boolean existsByAssetClassAndPlant(String assetClass, String plant);

}
