package assetmanagement.controller.reports;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import assetmanagement.response.ActivityReportDTO;
import assetmanagement.response.ApiResponse;
import assetmanagement.response.AssetResponse;
import assetmanagement.service.reports.ReportService;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;
    //pending
    @GetMapping("/get-activity-report")
    public ResponseEntity<ApiResponse> assetActivityReport(){

        try{
            List<ActivityReportDTO> assetActivityList = reportService.getAllActivityReport();
            if(assetActivityList.isEmpty()){
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true,"No Data Found",Collections.emptyList()));
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true,"Data Fetched Successfully",assetActivityList));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Internal Server Error", e.getMessage()));
        }  
    }
    //completed
    @GetMapping("/get-asset-byLocation")
    public ResponseEntity<ApiResponse> assetAssetByLocation(@RequestParam(required=false) String locationId){

        try{
            List<AssetResponse> assetByLocation = reportService.assetByLocation(locationId);
            if(assetByLocation.isEmpty()){
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true,"No Data Found",Collections.emptyList()));
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true,"Data Fetched Successfully",assetByLocation));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Internal Server Error", e.getMessage()));
        }  
    }

    @GetMapping("/get-asset-byType")
    public ResponseEntity<ApiResponse> assetAssetByType(@RequestParam(required=false) String typeId){

        try{
            List<AssetResponse> assetByLocation = reportService.assetAssetByType(typeId);
            if(assetByLocation.isEmpty()){
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true,"No Data Found",Collections.emptyList()));
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true,"Data Fetched Successfully",assetByLocation));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Internal Server Error", e.getMessage()));
        }  
    }
}
