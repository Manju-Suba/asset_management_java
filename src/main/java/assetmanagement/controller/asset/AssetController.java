package assetmanagement.controller.asset;

import assetmanagement.dto.AssetIdDTO;
import assetmanagement.exception.ResourceNotFoundException;
import assetmanagement.model.Asset;
import assetmanagement.model.masters.AssetType;
import assetmanagement.response.ApiResponse;
import assetmanagement.response.AssetAllocationResponse;
import assetmanagement.response.AssetListResponse;
import assetmanagement.response.SapResponse;
import assetmanagement.service.asset.AssetService;
import assetmanagement.service.masters.ExcelUploadService;
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

@CrossOrigin
@RestController
@RequestMapping("/asset")
@RequiredArgsConstructor
public class AssetController {

    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    private static final String DATA_FETCHED_SUCCESSFULLY = "Data Fetched Successfully";
    private static final String ID_NOT_NULL_OR_EMPTY_MESSAGE = "Id must not be null or empty";
    private static final String NO_DATA_FOUND_MESSAGE = "No Data Found";
    public final AssetService assetService;
    private final ExcelUploadService excelUploadService;

    @GetMapping("/fetch-and-insert")
    public ResponseEntity<ApiResponse> fetchDataAndInsert() {
        try {
            SapResponse assetList = assetService.fetchDataAndInsert();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Asset Created Successfully", assetList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/fetch-and-update")
    public ResponseEntity<ApiResponse> fetchDataAndUpdate() {
        try {
            SapResponse assetList = assetService.fetchDataAndUpdate();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Asset update Successfully", assetList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> create(@Valid Asset asset) {

        try {
            Asset createdAsset = assetService.create(asset);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Asset Created Successfully", createdAsset));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/get-all-assets")
    public ResponseEntity<ApiResponse> getAllAsset(@RequestParam(required = false) String availableStatus,
                                                   @RequestParam(required = false) String assetClass, @RequestParam(required = false) String assetStatus,
                                                   @RequestParam(required = false) String assetId, @RequestParam(required = false) String childId,
                                                   @RequestParam(required = false) Boolean search, @RequestParam(required = false) String value,
                                                   Integer page, Integer size) {
        try {
            AssetListResponse assetList = assetService.getAllAsset(availableStatus, assetClass, assetStatus, assetId,
                    childId, search, value, page, size);
            if (assetList == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY, assetList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse> getAllAsset(@RequestParam(required = false) String availableStatus,
                                                   @RequestParam(required = false) String assetClass, @RequestParam(required = false) String assetStatus,
                                                   @RequestParam(required = false) String childId) {
        try {
            List<Asset> assetList = assetService.getAllAsset(availableStatus, assetClass, assetStatus, childId);
            if (assetList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY, assetList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/get-by-id")
    public ResponseEntity<ApiResponse> getAssetById(@RequestParam(required = false) String id,
                                                    @RequestParam(required = false) String company, @RequestParam(required = false) String plant) {
        if (StringUtils.isEmpty(id)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, ID_NOT_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
        }
        try {
            Optional<Asset> getAsset = assetService.getAssetById(id, company, plant);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY, getAsset));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> update(@Valid Asset asset, MultipartFile imageUpload,
                                              MultipartFile documentUpload) {
        String id = asset.getId();
        if (id == null || id.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, ID_NOT_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
        }
        try {
            Asset updatedData = assetService.update(asset, imageUpload, documentUpload);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Asset Updated Successfully", updatedData));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(false, "Asset already exist", Collections.emptyList()));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> delete(@RequestParam(required = false) String id) {

        if (id == null || id.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, ID_NOT_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
        }
        try {
            Asset deletedData = assetService.delete(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, "Asset Deleted Successfully", deletedData));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/get-type-by-category")
    public ResponseEntity<ApiResponse> getAssetBasedOnCategory(@RequestParam(required = false) String categoryId) {

        if (categoryId == null || categoryId.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Category Id must not be null or empty", Collections.emptyList()));
        }
        try {
            List<AssetType> assetType = assetService.getAssetBasedOnCategory(categoryId);
            if (assetType.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Fetch Successfully", assetType));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/get-asset-allocation")
    public ResponseEntity<ApiResponse> getAssetAllocation(
            @RequestParam(required = false) String assetCategoryId,
            @RequestParam(required = false) String assetStatus, @RequestParam(required = false) String availableStatus,
            @RequestParam(required = false) String allocateType,
            @RequestParam(required = false) String subClass, Integer page, Integer size) {
        try {
            AssetAllocationResponse assetsModels = assetService.getAssetAllocation(assetCategoryId, assetStatus,
                    availableStatus,
                    allocateType, subClass, page, size);

            if (assetsModels == null) {
                return ResponseEntity.ok().body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.ok().body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY, assetsModels));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @GetMapping("/get-byclass")
    public ResponseEntity<ApiResponse> getByClass(@RequestParam String assetClass, String assetStatus, String assetId) {
        if (assetClass == null || assetClass.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, ID_NOT_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
        }
        try {
            List<Asset> getAsset = assetService.getByClass(assetClass, assetStatus, assetId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY, getAsset));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> uploadExcelFile(MultipartFile file) {

        try {
            if (ExcelUploadService.isValidExcelFile(file)) {
                List<Asset> assetsModels = excelUploadService.getAssetModelfromExcel(file.getInputStream());

                return ResponseEntity.ok().body(new ApiResponse(true, " Uploaded Successfully", assetsModels));
            } else {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(new ApiResponse(false, "File is Required", Collections.emptyList()));
            }
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), Collections.emptyList()));
        }

    }

    @GetMapping("/get-all-by-asset-status")
    public ResponseEntity<ApiResponse> getAllByAssetStatus(@RequestParam(required = false) String assetStatus) {
        if (assetStatus == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "assetStatus must not be null or empty", Collections.emptyList()));
        }
        try {
            List<Asset> allAssetsByStatus = assetService.getAllByAssetStatus(assetStatus);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY, allAssetsByStatus));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), Collections.emptyList()));
        }

    }

    @GetMapping("/get-by-assetId")
    public ResponseEntity<ApiResponse> getByAssetId(@RequestParam String assetId) {
        if (assetId == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Asset Id must not be null or empty", Collections.emptyList()));
        }
        try {
            Asset assetsById = assetService.getByAssetByAssetId(assetId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY, assetsById));

        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), Collections.emptyList()));
        }
    }

    @GetMapping("/fetch-not-replaced")
    public ResponseEntity<ApiResponse> getNotReplacedAssetID() {

        try {
            List<AssetIdDTO> assetIds = assetService.getNotReplacedAssetID();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY, assetIds));

        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), Collections.emptyList()));
        }
    }

    @GetMapping("/get-by-assetId-audit")
    public ResponseEntity<ApiResponse> getAssetByAssetIdToAudit(@RequestParam(required = false) String assetId,
                                                                @RequestParam(required = false) String company, @RequestParam(required = false) String plant,
                                                                @RequestParam(required = false) String assetClass) {
        if (StringUtils.isEmpty(assetId)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, ID_NOT_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
        }
        try {
            Optional<Asset> getAsset = assetService.getAssetByAssetIdToAudit(assetId, company, plant, assetClass);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY, getAsset));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }
}
