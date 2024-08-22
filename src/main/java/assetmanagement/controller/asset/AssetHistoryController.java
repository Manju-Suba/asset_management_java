package assetmanagement.controller.asset;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import assetmanagement.model.AssetHistory;
import assetmanagement.response.ApiResponse;
import assetmanagement.service.asset.AssetHistoryService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RequestMapping("/asset-history")
@RestController
@RequiredArgsConstructor
public class AssetHistoryController {

    private final AssetHistoryService assetHistoryService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> create(@RequestBody AssetHistory assetHistory) {
        
        try {
            AssetHistory saveHistory = assetHistoryService.create(assetHistory);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Asset Created Successfully", saveHistory));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Internal Server Error", e.getMessage()));
        }
       
    }
    
}   
