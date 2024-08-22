package assetmanagement.repository.masters;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import assetmanagement.model.masters.Brand;


@Repository
public interface BrandRepository extends MongoRepository<Brand,String> {
    boolean existsByNameIgnoreCaseAndCompanyIdAndStatus(String name,String companyId,String status);
    boolean existsByNameIgnoreCaseAndCompanyIdAndStatusAndIdNot(String name,String companyId,String status,String Id);

    Optional<Brand> findByIdAndStatus(String Id,String status);

    List<Brand> findAllByCompanyIdAndStatus(String companyId,String status);


    Optional<Brand> findByNameAndStatus(String brandName, String value);
}
