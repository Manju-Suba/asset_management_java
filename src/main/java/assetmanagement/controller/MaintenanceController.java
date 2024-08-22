package assetmanagement.controller;

import assetmanagement.dto.MaintenanceDTO;
import assetmanagement.model.Maintenance;
import assetmanagement.response.ApiResponse;
import assetmanagement.response.MaintenanceResponse;
import assetmanagement.response.SapResponse;
import assetmanagement.service.MaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    private static final String DATA_FETCHED_SUCCESSFULLY = "Data Fetched Successfully";
    private static final String NO_DATA_FOUND_MESSAGE = "No Data Found";
    private final MaintenanceService maintenanceService;

    @GetMapping("/fetch-by-assetId")
    public ResponseEntity<ApiResponse> getByAssetId(@RequestParam(required = true) String assetId,
                                                    @RequestParam(required = false) String fromDate,
                                                    @RequestParam(required = false) String toDate,
                                                    Integer page, Integer size) {
        try {
            MaintenanceResponse maintenanceResponse = maintenanceService.getByAssetId(assetId, fromDate, toDate, page,
                    size);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, "Maintenance Fetched Successfully", maintenanceResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/webclient")
    public ResponseEntity<ApiResponse> webclientCalling() {
        try {
            SapResponse assetList = maintenanceService.addApiData();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Data Saved Successfully", assetList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse> getAllMaintenances() {
        try {
            List<Maintenance> maintenanceList = maintenanceService.getAllMaintenances();
            if (maintenanceList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY, maintenanceList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }

    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> create(@RequestBody MaintenanceDTO maintenanceDTO) {
        try {
            Maintenance assetList = maintenanceService.create(maintenanceDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Maintenance Created Successfully", assetList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

}
