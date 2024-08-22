package assetmanagement.controller.masters;

import assetmanagement.model.masters.AssetCategory;
import assetmanagement.response.ApiResponse;
import assetmanagement.service.masters.AssetCategoryService;
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
@RequestMapping("/asset-category")
@RequiredArgsConstructor
public class AssetCategoryController {
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    private static final String ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE = "Id must not be null or empty";
    public final AssetCategoryService assetCategoryService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody AssetCategory assetCategory) {

        try {
            AssetCategory createAssetCategory = assetCategoryService.createAssetCategory(assetCategory);
            return ResponseEntity.ok(new ApiResponse(true, "Asset Category created Successfully", createAssetCategory));

        } catch (IllegalArgumentException | DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, e.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }

    }

    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse> getAssetCategoryModelgetAll() {
        try {

            List<AssetCategory> assetCategory = assetCategoryService.getallAssetCategory();
            return ResponseEntity.ok().body(new ApiResponse(true, "Data Fetched Sucessfully", assetCategory));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, null));
        }
    }

    @GetMapping("/get")
    public ResponseEntity<ApiResponse> getassetCategoryById(@RequestParam String id) {
        try {
            if (StringUtils.isEmpty(id)) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
            }
            AssetCategory assetCategory = assetCategoryService.getbyIdAssetCategory(id);
            if (assetCategory != null) {
                return ResponseEntity.ok().body(new ApiResponse(true, "Data Fetched Sucessfully", assetCategory));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Not Found", Collections.emptyList()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, null));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> putAssetCategoryModel(@RequestBody AssetCategory assetCategory) {
        try {
            String id = assetCategory.getId();
            if (StringUtils.isEmpty(id)) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
            }

            AssetCategory updatedassetCategory = assetCategoryService.updateAssetCategory(assetCategory);
            if (updatedassetCategory != null) {
                return ResponseEntity.ok().body(new ApiResponse(true, "Asset Category Updated Sucessfully", updatedassetCategory));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Asset Category Not found  ", Collections.emptyList()));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, e.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, null));
        }
    }


    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deleteAssetCategory(@RequestParam String id) {
        try {
            if (StringUtils.isEmpty(id)) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
            }
            AssetCategory delete = assetCategoryService.deleteAssetCategory(id);
            if (delete != null) {
                return ResponseEntity.ok().body(new ApiResponse(true, "Deleted Sucessfully", null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Not Found  ", Collections.emptyList()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, null));
        }

    }


}
