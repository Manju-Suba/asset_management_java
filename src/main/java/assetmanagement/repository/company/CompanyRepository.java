package assetmanagement.repository.company;

import org.springframework.data.mongodb.repository.MongoRepository;
import assetmanagement.model.Company;

import java.util.Optional;

public interface CompanyRepository extends MongoRepository<Company, String>{

    boolean existsByName(String name);
    Optional<Company> findByName(String getCompanyName);
}
