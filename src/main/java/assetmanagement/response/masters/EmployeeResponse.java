package assetmanagement.response.masters;

import org.springframework.data.annotation.Id;

import assetmanagement.model.masters.Department;
import lombok.Data;

@Data
public class EmployeeResponse {
    
    @Id
    private String id ;
    private String empId;
    private Department department;
    private String fullName;
    private String email;
    private String jobRole;
    private String costCenter;
    
}
