package assetmanagement.controller.asset;


import assetmanagement.enumData.TransferStatus;
import assetmanagement.exception.ResourceNotFoundException;
import assetmanagement.model.ScrappedDetails;
import assetmanagement.model.transfer.TransferDetail;
import assetmanagement.request.RequestWithFilter;
import assetmanagement.request.ScrapRequest;
import assetmanagement.response.ApiResponse;
import assetmanagement.response.AssetListResponse;
import assetmanagement.response.MultiUsersTransferPendingResponse;
import assetmanagement.response.ScrapPendingResponse;
import assetmanagement.service.masters.MultiUsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;


@RestController
@RequestMapping("/multiUsers")
@RequiredArgsConstructor
public class MultiUsersController {
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    public final MultiUsersService multiUsersService;

    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse> getAllAsset(@RequestParam(required = false) String assetClass,
                                                   @RequestParam(required = false) String assetStatus, @RequestParam(required = false) String assetId,
                                                   @RequestParam(required = false) String childId, @RequestParam(required = false) Boolean search,
                                                   @RequestParam(required = false) String value, Integer page, Integer size) {
        try {
            AssetListResponse assetList = multiUsersService.getAllAsset(assetClass, assetStatus, assetId, childId, search, value, page, size);
            if (assetList == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, "No Data Found", Collections.emptyList()));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, "Data fetched successfully", assetList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/scrapped-by-status")
    public ResponseEntity<ApiResponse> getAllScrapped(RequestWithFilter requestWithFilter, Boolean search, String value, Integer page, Integer size) {
        if (requestWithFilter.getStatus() == null || requestWithFilter.getStatus().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "status must not be null or empty", Collections.emptyList()));
        }
        try {
            ScrapPendingResponse scrapResponse = multiUsersService.getAllScrapped(requestWithFilter, search, value, page, size);
            if (scrapResponse == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, "No data found", Collections.emptyList()));
            }
            return ResponseEntity.ok().body(new ApiResponse(true, "Pending Fetch Successfully", scrapResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Internal server error", e.getMessage()));
        }
    }


    @GetMapping("/transferred-by-status")
    public ResponseEntity<ApiResponse> getAllTransferred(RequestWithFilter requestWithFilter, Boolean search, String value, Integer page, Integer size) {
        if (requestWithFilter.getStatus() == null || requestWithFilter.getStatus().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "status must not be null or empty", Collections.emptyList()));
        }
        try {
            MultiUsersTransferPendingResponse transferResponse = multiUsersService.getAllTransferred(requestWithFilter, search, value, page, size);
            if (transferResponse == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, "No data found", Collections.emptyList()));
            }
            return ResponseEntity.ok().body(new ApiResponse(true, "Pending Fetch Successfully", transferResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @PostMapping("/scrap-request")
    public ResponseEntity<ApiResponse> scrapRequest(@RequestBody ScrapRequest scrapRequest) {

        if (scrapRequest.getObjectId() == null || scrapRequest.getObjectId().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "assetId must not be null or empty", Collections.emptyList()));
        }
        try {
            ScrappedDetails scrappedResponse = multiUsersService.createScrapRequest(scrapRequest);
            if (scrappedResponse == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, "No Data Found", Collections.emptyList()));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, "Scrapped Request Created Successfully", scrappedResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @PostMapping("/transfer-request")
    public ResponseEntity<ApiResponse> create(@RequestBody TransferDetail transferDetail) {
        if (transferDetail.getAssetId() == null || transferDetail.getAssetId().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "assetId must not be null or empty", Collections.emptyList()));
        }
        try {
            TransferDetail createdTransfer = multiUsersService.create(transferDetail);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Transfer Request Created Successfully", createdTransfer));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @PutMapping("/multiple-asset-status-update") //admin transfer approve - reject Response
    public ResponseEntity<ApiResponse> bulkAssetUpdateStatus(@RequestBody RequestWithFilter requestWithFilter) {
        try {
            TransferDetail changeTransfer = multiUsersService.bulkAssetUpdateStatus(requestWithFilter.getId(), requestWithFilter.getAssetId(), requestWithFilter.getStatus());
            String message = "Successfully";

            if (requestWithFilter.getStatus().equals(TransferStatus.Approved.getValue())) {
                message = "Transfer Request Approved Successfully";
            }
            if (requestWithFilter.getStatus().equals(TransferStatus.Rejected.getValue())) {
                message = "Transfer Request Rejected Successfully";
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, message, changeTransfer));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }


    @PutMapping("/multiple-scrap-status-update") //admin scrapped approve - reject Response
    public ResponseEntity<ApiResponse> bulkScrapUpdateStatus(@RequestBody RequestWithFilter requestWithFilter) {
        try {
            ScrappedDetails changeScrappedDetails = multiUsersService.bulkScrapUpdateStatus(requestWithFilter.getAssetId(), requestWithFilter.getStatus());
            String message = "Successfully";

            if (requestWithFilter.getStatus().equals(TransferStatus.Approved.getValue())) {
                message = "Scrapped Request Approved Successfully";
            }
            if (requestWithFilter.getStatus().equals(TransferStatus.Rejected.getValue())) {
                message = "Scrapped Request Rejected Successfully";
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, message, changeScrappedDetails));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }


}
