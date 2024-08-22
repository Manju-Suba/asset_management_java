package assetmanagement.controller.masters;

import assetmanagement.model.masters.Business;
import assetmanagement.response.ApiResponse;
import assetmanagement.service.masters.BusinessService;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/business")
@RequiredArgsConstructor
public class BusinessController {

    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    private static final String ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE = "Id must not be null or empty";
    public final BusinessService businessService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody Business business) {

        try {
            Business business2 = businessService.create(business);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Business Added Successfully", business2));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, e.getMessage(), Collections.emptyList()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, "Business already exist", Collections.emptyList()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }

    }

    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse> getAllBusiness() {
        try {
            List<Business> business = businessService.getAll();
            return ResponseEntity.ok().body(new ApiResponse(true, "Successfully Fetched Data", business));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/get")
    public ResponseEntity<ApiResponse> get(@RequestParam String id) {
        try {
            if (StringUtils.isEmpty(id)) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
            }

            Business businessModel = businessService.getBusinessById(id);
            if (businessModel != null) {

                return ResponseEntity.ok().body(new ApiResponse(true, "Successfully Fetched Data", businessModel));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "No Data Found", Collections.emptyList()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> update(@Valid @RequestBody Business business) {

        try {
            String id = business.getId();
            if (StringUtils.isEmpty(id)) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
            }
            Business updatedBusiness = businessService.update(business);
            if (updatedBusiness != null) {
                return ResponseEntity.ok().body(new ApiResponse(true, "Successfully Updated Data", updatedBusiness));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Business not found", Collections.emptyList()));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, e.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }

    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> delete(@RequestParam String id) {

        try {
            if (StringUtils.isEmpty(id)) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
            }
            Business delete = businessService.delete(id);
            if (delete != null) {
                return ResponseEntity.ok().body(new ApiResponse(true, "Successfully Deleted Data", Collections.emptyList()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Business Not Found", Collections.emptyList()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

}
