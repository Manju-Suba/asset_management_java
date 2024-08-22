package assetmanagement.repository.masters;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import assetmanagement.model.masters.Employee;
import assetmanagement.response.masters.EmployeeResponse;

public interface EmployeeRepository extends MongoRepository<Employee,String>{
    boolean existsByEmpIdAndEmailAndCompanyIdAndStatus(String empId,String email,String companyId,String status);
    boolean existsByEmailAndCompanyIdAndStatusAndIdNot(String email,String companyId,String status,String id);

    List<Employee> findByCompanyIdAndStatus(String companyId,String status);

    Optional<EmployeeResponse> findByIdAndCompanyIdAndStatus(String id,String companyId,String status);

    Optional<Employee> findByIdAndStatus(String id,String status);

    Optional<Employee> findByEmpIdAndStatus(String empid, String value);

    Optional<Employee> findByEmailAndStatus(String email, String value);
}
