package assetmanagement.controller.masters;

import assetmanagement.exception.ResourceNotFoundException;
import assetmanagement.model.masters.Employee;
import assetmanagement.response.ApiResponse;
import assetmanagement.response.masters.EmployeeResponse;
import assetmanagement.service.masters.EmployeeService;
import assetmanagement.service.masters.ExcelUploadService;
import assetmanagement.util.uniqueError;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    private static final String ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE = "Id must not be null or empty";
    public final EmployeeService employeeService;
    private final ExcelUploadService excelUploadService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody Employee employee) {

        try {
            Employee employeeData = employeeService.create(employee);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Employee Created Successfully", employeeData));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, e.getMessage(), Collections.emptyList()));
        } catch (DataIntegrityViolationException e) {
            return uniqueError.handleUnique(e);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, Collections.emptyList()));

        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse> getAllEmployee() {
        try {
            List<Employee> allEmployees = employeeService.getAllEmployee();
            if (allEmployees.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "No Data Found", Collections.emptyList()));
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Data Fetched Successfully", allEmployees));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/get")
    public ResponseEntity<ApiResponse> getDepartmentById(@RequestParam(required = false) String id) {
        if (StringUtils.isEmpty(id)) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
        }
        try {
            Optional<EmployeeResponse> getdepartment = employeeService.getEmployeeById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Data Fetched Successfully", getdepartment));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> update(@Valid @RequestBody Employee employee) {
        String id = employee.getId();
        if (StringUtils.isEmpty(id)) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
        }
        try {
            Employee updatedData = employeeService.update(employee);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Employee Updated Successfully", updatedData));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, e.getMessage(), Collections.emptyList()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, "Employee already exist", Collections.emptyList()));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> delete(@RequestParam(required = false) String id) {

        if (StringUtils.isEmpty(id)) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
        }
        try {
            Employee deletedData = employeeService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Employee Deleted Successfully", deletedData));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @PostMapping("/bulk-upload")
    public ResponseEntity<ApiResponse> employeeBulkUpload(MultipartFile file) {

        try {
            if (ExcelUploadService.isValidExcelFile(file)) {
                List<Employee> employeeList = excelUploadService.getEmployeesFromExcel(file.getInputStream());
                if (!employeeList.isEmpty()) {
                    excelUploadService.saveBulkOfEmployees(employeeList);
                }
                return ResponseEntity.ok().body(new ApiResponse(true, "Uploaded Successfully", employeeList));
            } else {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ApiResponse(false, "File is Required", Collections.emptyList()));
            }
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }

    }

}
