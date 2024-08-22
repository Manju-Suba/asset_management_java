package assetmanagement.serviceImpl.masters;

import assetmanagement.enumData.ActiveInActive;
import assetmanagement.exception.ResourceNotFoundException;
import assetmanagement.model.masters.Department;
import assetmanagement.repository.masters.DepartmentRepository;
import assetmanagement.service.masters.DepartmentService;
import assetmanagement.util.AuthUser;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    public final DepartmentRepository departmentRepository;

    @Override
    public Department create(Department department) {
        if(departmentRepository.existsByNameIgnoreCaseAndCompanyIdAndStatus(department.getName().trim(),AuthUser.getCompanyId(),ActiveInActive.ACTIVE.getValue())){
            throw new IllegalArgumentException("Department already exist ");
        }
       department.setCompanyId(AuthUser.getCompanyId());
       return departmentRepository.save(department);
    }

    @Override
    public List<Department> getAllDepartment() {
        
        List<Department> department=departmentRepository.findByCompanyIdAndStatus(AuthUser.getCompanyId(),ActiveInActive.ACTIVE.getValue());
        return department;
    }

    @Override
    public Optional<Department> getDepartmentById(String id) {

        if(!departmentRepository.existsById(id)){
            throw new ResourceNotFoundException("Id not found");
        }
        return departmentRepository.findByIdAndCompanyIdAndStatus(id,AuthUser.getCompanyId(),ActiveInActive.ACTIVE.getValue()); 
        
    }

    @Override
    public Department update(Department department) {
        if(departmentRepository.existsByNameIgnoreCaseAndCompanyIdAndStatusAndIdNot(department.getName().trim(),AuthUser.getCompanyId(),ActiveInActive.ACTIVE.getValue(),department.getId())){
            throw new IllegalArgumentException("Department already exists ");
        }
        Department existingDepartment = departmentRepository.findById(department.getId())
        .orElseThrow(() -> new IllegalArgumentException("Department not found"));
        existingDepartment.setName(department.getName());
        existingDepartment.setBusinessId(department.getBusinessId());
        existingDepartment.setDescription(department.getDescription());
            return departmentRepository.save(existingDepartment);
        }

    @Override
    public Department delete(String id) {
        Optional<Department> departmentOptional=departmentRepository.findByIdAndStatus(id,ActiveInActive.ACTIVE.getValue());
        if(departmentOptional.isPresent()){
            Department departmentDelete= departmentOptional.get();
            departmentDelete.setStatus(ActiveInActive.INACTIVE.getValue());
            return departmentRepository.save(departmentDelete);
        }
        throw new ResourceNotFoundException("Data not found");
    }

}
