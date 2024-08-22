package assetmanagement.repository.masters;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.util.Streamable;

import assetmanagement.model.masters.AssetClass;
import assetmanagement.model.masters.Plant;
import java.util.List;


public interface PlantRepository extends MongoRepository<Plant, String> {

    boolean existsByPlant(String plant);

    List<Plant> findByCompanyId(String companyId,Pageable pageable);

    long countByCompanyId(String companyId);

    Streamable<AssetClass> findByCompanyId(String companyId);
}
