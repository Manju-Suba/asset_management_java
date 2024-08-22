package assetmanagement.controller.masters;

import assetmanagement.model.SubClass;
import assetmanagement.model.masters.AssetClass;
import assetmanagement.response.ApiResponse;
import assetmanagement.response.AssetClassResponse;
import assetmanagement.response.PlantResponse;
import assetmanagement.response.SubClassResponse;
import assetmanagement.service.masters.AssetClassService;
import assetmanagement.service.masters.SubClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/asset-class/")
@RequiredArgsConstructor
public class AssetClassController {
    private static final String DATA_FETCHED_SUCCESSFULLY = "Data Fetched Successfully";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    public final AssetClassService assetClassService;
    public final SubClassService subClassService;

    @GetMapping("get-all-master")
    public ResponseEntity<ApiResponse> getAllAssetClass(Integer page, Integer size) {

        try {
            AssetClassResponse assetClass = assetClassService.getAllAssetClass(page, size);
            if (assetClass == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY, Collections.emptyList()));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY, assetClass));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }

    }

    @GetMapping("get-all")
    public ResponseEntity<ApiResponse> getAllAssetClass() {

        try {
            List<AssetClass> assetClass = assetClassService.getAllAssetClass();
            if (assetClass.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY, Collections.emptyList()));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY, assetClass));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }

    }

    @GetMapping("fetchall-subClass")
    public ResponseEntity<ApiResponse> getAllSubClass(Integer page, Integer size) {

        try {
            SubClassResponse assetClassWithSubClass = subClassService.getAllSubClass(page, size);
            if (assetClassWithSubClass == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY, Collections.emptyList()));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY, assetClassWithSubClass));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }

    }

    @GetMapping("fetchBy-subClass")
    public ResponseEntity<ApiResponse> getAllAssetClassWithSubClass(String assetClass, Integer page, Integer size) {

        try {
            SubClassResponse assetClassWithSubClass = subClassService.getAllAssetClass(assetClass, page, size);
            if (assetClassWithSubClass == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY, Collections.emptyList()));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY, assetClassWithSubClass));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }

    }

    @GetMapping("get-subclass")
    public ResponseEntity<ApiResponse> getAllAssetClassBasedSubClass(@RequestParam String assetClass) {
        try {
            List<SubClass> assetClassWithSubClass = subClassService.getAllAssetClassBasedSubClass(assetClass);
            if (assetClassWithSubClass == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY, Collections.emptyList()));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY, assetClassWithSubClass));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("get-plant")
    public ResponseEntity<ApiResponse> getAllPlant(Integer page, Integer size) {

        try {
            PlantResponse assetClassWithSubClass = assetClassService.getAllPlant(page, size);
            if (assetClassWithSubClass == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY, Collections.emptyList()));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY, assetClassWithSubClass));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }

    }

    @GetMapping("check-list-asset-class")
    public ResponseEntity<ApiResponse> getCheckListAssetClass() {
        try {
            List<AssetClass> assetClassWithSubClass = assetClassService.getCheckListAssetClass();
            if (assetClassWithSubClass == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY, Collections.emptyList()));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY, assetClassWithSubClass));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

}
