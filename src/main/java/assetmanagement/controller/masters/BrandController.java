package assetmanagement.controller.masters;

import assetmanagement.model.masters.Brand;
import assetmanagement.response.ApiResponse;
import assetmanagement.service.masters.BrandService;
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
@RequestMapping("/brand")
@RequiredArgsConstructor
public class BrandController {
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    private static final String ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE = "Id must not be null or empty";
    private final BrandService brandService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody Brand brand) {
        try {
            Object result = brandService.create(brand);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Brand Created Successfully", result));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, e.getMessage(), Collections.emptyList()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, "Brand already exist", Collections.emptyList()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/get-all")
    // @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<ApiResponse> getAllBrand() {
        try {
            List<Brand> allBrand = brandService.getAllBrand();
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Data Fetched Successfully", allBrand));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }


    @GetMapping("/get")
    public ResponseEntity<ApiResponse> getBrandbyId(@RequestParam String id) {
        try {
            if (StringUtils.isEmpty(id)) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
            }

            Brand brand = brandService.getById(id);
            if (brand != null) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Data Fetched Successfully", brand));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(true, "No Data Found", Collections.emptyList()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, null));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateBrand(@RequestBody Brand brand) {
        try {
            String id = brand.getId();
            if (StringUtils.isEmpty(id)) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
            }
            Brand updatedBrand = brandService.update(brand);
            if (updatedBrand != null) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Data Updated Successfully", updatedBrand));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Brand Not Found", Collections.emptyList()));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, e.getMessage(), Collections.emptyList()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, "Brand already exist", Collections.emptyList()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, null));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deleteBrand(@RequestParam String id) {
        try {
            if (StringUtils.isEmpty(id)) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
            }
            Brand delete = brandService.delete(id);
            if (delete != null) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Data Deleted successfully", null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Not Found", Collections.emptyList()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, null));
        }
    }


}
