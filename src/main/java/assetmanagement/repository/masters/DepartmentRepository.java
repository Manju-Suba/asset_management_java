package assetmanagement.repository.masters;

import assetmanagement.model.masters.Department;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DepartmentRepository extends MongoRepository<Department,String> {
    boolean existsByNameIgnoreCaseAndCompanyIdAndStatus(String name,String companyId,String status);
    boolean existsByNameIgnoreCaseAndCompanyIdAndStatusAndIdNot(String name,String companyId,String status,String Id);

    List<Department> findByCompanyIdAndStatus(String companyId,String status);

    Optional<Department> findByIdAndCompanyIdAndStatus(String id,String companyId,String status);

    Optional<Department> findByIdAndStatus(String id, String status);

    Optional<Department> findByNameAndStatus(String departmentname, String value);
}
