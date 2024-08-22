package assetmanagement.controller.asset;

import assetmanagement.exception.ResourceNotFoundException;
import assetmanagement.model.Asset;
import assetmanagement.model.SoftwareHistory;
import assetmanagement.response.ApiResponse;
import assetmanagement.service.asset.SoftwareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/software")
@RequiredArgsConstructor
public class SoftwareController {
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    public final SoftwareService softwareService;

    @GetMapping("/data")
    public ResponseEntity<ApiResponse> getSoftwareDataByExpiry(@RequestParam(required = false) String type) {

        try {
            List<Asset> assetList = softwareService.getSoftwareData(type);
            if (assetList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "No Data Found", Collections.emptyList()));
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Data Fetched Successfully", assetList));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @PostMapping("/save-renewal")
    public ResponseEntity<ApiResponse> saveRenewal(SoftwareHistory softwareHistory, MultipartFile image) {

        try {
            SoftwareHistory savedRecord = softwareService.saveRenewal(softwareHistory, image);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Software History Created Successfully", savedRecord));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }

    }

    @GetMapping("/get-software-history-by-asset")
    public ResponseEntity<ApiResponse> getSoftwareHistoryByAssetId(@RequestParam String assetId) {
        try {
            List<SoftwareHistory> softwareHistoryList = softwareService.getSoftwareHistoryByAssetId(assetId);
            if (softwareHistoryList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "No Data Found", Collections.emptyList()));

            }
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Software History Fetched Successfully", softwareHistoryList));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }


}
