package assetmanagement.service.masters;

import java.util.List;
import java.util.Optional;

import assetmanagement.model.masters.Department;

public interface DepartmentService {

    public Department create(Department department);
    public List<Department> getAllDepartment();
    public Optional<Department> getDepartmentById(String id);
    public Department update(Department department);
    public Department delete(String id);
}
