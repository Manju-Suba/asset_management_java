package assetmanagement.repository.masters;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import assetmanagement.model.masters.Location;

@Repository
public interface LocationRepository extends MongoRepository<Location,String>{
    boolean existsByNameIgnoreCaseAndCompanyIdAndStatus(String name,String companyId,String status);
    boolean existsByNameIgnoreCaseAndCompanyIdAndStatusAndIdNot(String name,String companyId,String status,String Id);

    Optional<Location> findByIdAndStatus(String locationId,String status);

    List<Location> findAllByCompanyIdAndStatus(String companyId,String status);

    Optional<Location> findByNameAndStatus(String locationname, String value);
}
