package assetmanagement.serviceImpl.masters;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import assetmanagement.enumData.ActiveInActive;
import assetmanagement.exception.ResourceNotFoundException;
import assetmanagement.model.masters.Employee;
import assetmanagement.repository.masters.BusinessRepository;
import assetmanagement.repository.masters.DepartmentRepository;
import assetmanagement.repository.masters.EmployeeRepository;
import assetmanagement.response.masters.EmployeeResponse;
import assetmanagement.service.masters.EmployeeService;
import assetmanagement.util.AuthUser;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService{

    public final EmployeeRepository employeeRepository;
    public final DepartmentRepository departmentRepository;
    public final BusinessRepository businessRepository;

    @Override
    public Employee create(Employee employee) {
        if(employeeRepository.existsByEmpIdAndEmailAndCompanyIdAndStatus(employee.getEmpId(),employee.getEmail(),AuthUser.getCompanyId(),ActiveInActive.ACTIVE.getValue())){
            throw new IllegalArgumentException("Employee already exist ");
        }
       employee.setCompanyId(AuthUser.getCompanyId()); 
       return employeeRepository.save(employee);

    }

    @Override
    public List<Employee> getAllEmployee() {
        List<Employee> employee=employeeRepository.findByCompanyIdAndStatus(AuthUser.getCompanyId(),ActiveInActive.ACTIVE.getValue());
        return employee;
    }

    @Override
    public Optional<EmployeeResponse> getEmployeeById(String id) {
        if(!employeeRepository.existsById(id)){
            throw new ResourceNotFoundException("Id not found");
        }
        return employeeRepository.findByIdAndCompanyIdAndStatus(id,AuthUser.getCompanyId(),ActiveInActive.ACTIVE.getValue()); 
       
    }

    @Override
    public Employee update(Employee employee) {
        if(employeeRepository.existsByEmailAndCompanyIdAndStatusAndIdNot(employee.getEmail(),AuthUser.getCompanyId(),ActiveInActive.ACTIVE.getValue(),employee.getId())){
            throw new IllegalArgumentException("Employee with email already exists ");
        }
        Employee existingEmployee = employeeRepository.findById(employee.getId())
        .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        existingEmployee.setFullName(employee.getFullName());
        existingEmployee.setBusinessId(employee.getBusinessId());
        existingEmployee.setDepartment(employee.getDepartment());
        existingEmployee.setAddress(employee.getAddress());
        existingEmployee.setCity(employee.getCity());
        existingEmployee.setCostCenter(employee.getCostCenter());
        existingEmployee.setCountry(employee.getCountry());
        existingEmployee.setEmail(employee.getEmail());
        existingEmployee.setJobRole(employee.getJobRole());
        existingEmployee.setSpecialRole(employee.getSpecialRole());
        existingEmployee.setSupervisor(employee.getSupervisor());
            return employeeRepository.save(existingEmployee);
    }
    
    @Override
    public Employee delete(String id) {
        Optional<Employee> employeeOptional=employeeRepository.findByIdAndStatus(id,ActiveInActive.ACTIVE.getValue());
        if(employeeOptional.isPresent()){
            Employee departmentDelete= employeeOptional.get();
            departmentDelete.setStatus(ActiveInActive.INACTIVE.getValue());
            return employeeRepository.save(departmentDelete);
        }
        throw new ResourceNotFoundException("Data not found");
    }


}
