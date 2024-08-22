package assetmanagement.repository.masters;

import java.util.List;
import java.util.Optional;
// import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import assetmanagement.model.masters.Business;

public interface BusinessRepository extends MongoRepository<Business, String> {
    boolean existsByNameIgnoreCaseAndCompanyIdAndStatus(String name,String companyId,String status);
    boolean existsByNameIgnoreCaseAndCompanyIdAndStatusAndIdNot(String name,String companyId,String status,String Id);

    Optional<Business> findByIdAndStatus(String id,String status);

    List<Business> findByCompanyIdAndStatus(String companyId,String status);

    Optional<Business> findByNameAndStatus(String businessName, String value);
}
