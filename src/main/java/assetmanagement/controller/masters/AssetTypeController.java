package assetmanagement.controller.masters;

import assetmanagement.model.masters.AssetType;
import assetmanagement.response.ApiResponse;
import assetmanagement.service.masters.AssetTypeService;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;


@RestController
@RequestMapping("/asset-type")
@RequiredArgsConstructor
public class AssetTypeController {
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    private static final String ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE = "Id must not be null or empty";
    private final AssetTypeService assetTypeService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody AssetType assetType) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Asset Type Created Successfully", assetTypeService.createAssetType(assetType)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, e.getMessage(), Collections.emptyList()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, "Asset type already exist", Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse> getAllAssetType() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Data fetched successfully", assetTypeService.getAllAssetType()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, null));
        }
    }

    @GetMapping("/get")
    public ResponseEntity<ApiResponse> getAssetTypebyId(@RequestParam String id) {
        try {
            if (StringUtils.isEmpty(id)) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
            }
            AssetType assetType = assetTypeService.getAssetTypeById(id);
            if (assetType != null) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Data fetched successfully", assetType));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(true, "No Data Found", Collections.emptyList()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, null));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> update(@RequestBody AssetType assetType) {
        try {
            String id = assetType.getId();
            if (StringUtils.isEmpty(id)) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
            }
            AssetType updatedAssetType = assetTypeService.updateAssetType(assetType);
            if (updatedAssetType != null) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Data Updated successfully", updatedAssetType));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Not Found", Collections.emptyList()));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, e.getMessage(), Collections.emptyList()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, "Asset type already exist", Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, null));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> delete(@RequestParam String id) {
        try {
            if (StringUtils.isEmpty(id)) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
            }
            AssetType delete = assetTypeService.deleteAssetType(id);
            if (delete != null) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Data Deleted successfully", Collections.emptyList()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Not Found", Collections.emptyList()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, null));
        }
    }


}
