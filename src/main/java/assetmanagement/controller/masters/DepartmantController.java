package assetmanagement.controller.masters;

import assetmanagement.exception.ResourceNotFoundException;
import assetmanagement.model.masters.Department;
import assetmanagement.response.ApiResponse;
import assetmanagement.service.masters.DepartmentService;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/department")
@RequiredArgsConstructor
public class DepartmantController {
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    private static final String ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE = "Id must not be null or empty";
    public final DepartmentService departmentService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody Department department) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Department Created Successfully", departmentService.create(department)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, e.getMessage(), Collections.emptyList()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, "Already exist", Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse> getAllDepartment() {
        try {
            List<Department> allDepartment = departmentService.getAllDepartment();
            if (allDepartment.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "No Data Found", Collections.emptyList()));
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Data Fetched Successfully", allDepartment));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, "Department already exist", Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/get")
    public ResponseEntity<ApiResponse> getDepartmentById(@RequestParam(required = false) String id) {

        try {
            if (StringUtils.isEmpty(id)) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
            }

            Optional<Department> getdepartment = departmentService.getDepartmentById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Data Fetched Successfully", getdepartment));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> update(@Valid @RequestBody Department department) {
        String id = department.getId();
        if (StringUtils.isEmpty(id)) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
        }
        try {
            Department updatedData = departmentService.update(department);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Department Updated Successfully", updatedData));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, e.getMessage(), Collections.emptyList()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, "Department already exist", Collections.emptyList()));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> delete(@RequestParam(required = false) String id) {

        try {
            if (StringUtils.isEmpty(id)) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
            }
            Department deletedData = departmentService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Department Deleted Successfully", deletedData));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }
}
