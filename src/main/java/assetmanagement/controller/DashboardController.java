package assetmanagement.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import assetmanagement.model.Asset;
import assetmanagement.response.ApiResponse;
import assetmanagement.service.DashboardService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    public final DashboardService dashboardService;

    private static final String DATA_FETCHED_SUCCESSFULLY = "Data Fetched Successfully";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";

    @GetMapping("/asset-category-count")
    public ResponseEntity<ApiResponse> getAssetCategoryCounts() {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY, dashboardService.getAssetCategoryCounts()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/damaged-and-rejected")
    public ResponseEntity<ApiResponse> totalDamagedRejectedAssets() {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY,
                            dashboardService.totalDamagedRejectedAssets()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/recently-purchased")
    public ResponseEntity<List<Asset>> getRecentlyPurchasedAssets() {
        List<Asset> recentlyPurchasedAssets = dashboardService.getRecentlyPurchasedAssets();
        return new ResponseEntity<>(recentlyPurchasedAssets, HttpStatus.OK);
    }

    @GetMapping("/damaged-assets-rate")
    public ResponseEntity<ApiResponse> getDamagedAssetsCountByYearAndMonth(
            @RequestParam(required = false) Integer year) {

        try {
            if (year == null) {
                year = LocalDate.now().getYear();
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY,
                    dashboardService.getDamagedAssetsCountByYearAndMonth(year)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/asset-type-count")
    public ResponseEntity<ApiResponse> assetTypeCount(Integer page, Integer size) {

        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY,
                            dashboardService.assetTypeCount(page, size)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/overall-stocks-and-dispose")
    public ResponseEntity<ApiResponse> overallAsset() {

        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY, dashboardService.overallAsset()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/rejected-assets")
    public ResponseEntity<ApiResponse> getRejectedList(Integer page, Integer size, boolean search, String value) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, "DATA_FETCHED_SUCCESSFULLY",
                            dashboardService.getRejectedAssets(page, size, search, value)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/assets-count")
    public ResponseEntity<ApiResponse> assetsCount(Integer page, Integer size, boolean search, String value) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY,
                            dashboardService.assetsCount(page, size, search, value)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/major-minor")
    public ResponseEntity<ApiResponse> fetchMajorAndMinorAsset(Integer page, Integer size, boolean search,
            String value) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY,
                            dashboardService.getMajorAndMinorAsset(page, size, search, value)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

}
