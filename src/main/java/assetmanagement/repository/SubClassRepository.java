package assetmanagement.repository;

import org.springframework.data.domain.Pageable;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;

import assetmanagement.model.SubClass;

public interface SubClassRepository extends MongoRepository<SubClass, String> {

      boolean existsByAssetIdAndChildId(String assetId,String childId);

      Page<SubClass> findByCompanyIdAndPlantAndAssetClass(String companyId, String plant, String assetClass,
                  Pageable pageable);

      List<SubClass> findByCompanyIdAndPlantAndAssetClass(String companyId, String plant, String assetClass);

      List<SubClass> findByCompanyIdAndPlant(String companyId, String plant,Pageable pageable);

      long countByCompanyIdAndPlant(String companyId, String plant);

}
