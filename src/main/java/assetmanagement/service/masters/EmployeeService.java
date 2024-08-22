package assetmanagement.service.masters;

import java.util.List;
import java.util.Optional;
import assetmanagement.model.masters.Employee;
import assetmanagement.response.masters.EmployeeResponse;

public interface EmployeeService {

    Employee create(Employee employee);
    List<Employee> getAllEmployee();
    Optional<EmployeeResponse> getEmployeeById(String id);
    Employee update(Employee employee);
    Employee delete(String id);

}
