package assetmanagement.repository.masters;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import assetmanagement.model.masters.AssetType;

@Repository
public interface AssetTypeRepository extends MongoRepository<AssetType, String> {
    boolean existsByNameIgnoreCaseAndCompanyIdAndStatus(String name, String companyId, String status);

    boolean existsByNameIgnoreCaseAndCompanyIdAndStatusAndIdNot(String name, String companyId, String status,
            String Id);

    Optional<AssetType> findByIdAndStatus(String Id, String status);

    List<AssetType> findAllByCompanyIdAndPlantAndStatus(String companyId, String plant, String status);

    List<AssetType> findByAssetCategoryAndCompanyIdAndStatus(String categoryId, String companyId, String status);

    Optional<AssetType> findByNameAndStatus(String assettype, String value);
}
