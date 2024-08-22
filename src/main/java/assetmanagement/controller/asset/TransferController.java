package assetmanagement.controller.asset;

import assetmanagement.enumData.TransferStatus;
import assetmanagement.exception.ResourceNotFoundException;
import assetmanagement.model.transfer.TransferDetail;
import assetmanagement.response.ApiResponse;
import assetmanagement.response.TransferPendingResponse;
import assetmanagement.service.asset.TransferService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
public class TransferController {

    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    private static final String NO_DATA_FOUND_MESSAGE = "No Data Found";
    private final TransferService transferService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> create(@RequestBody TransferDetail transferDetail) {
        try {
            TransferDetail createdTransfer = transferService.create(transferDetail);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Transfer Request Created Successfully", createdTransfer));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse> getAll(@RequestParam String status) {
        try {
            List<TransferDetail> fetchTransfer = transferService.getAll(status);
            if (fetchTransfer.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.ok()
                    .body(new ApiResponse(true, "Transfer Request Fetch Successfully", fetchTransfer));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @PutMapping("/update-status")
    public ResponseEntity<ApiResponse> updateStatus(@RequestParam String status, @RequestParam String id) {
        try {
            TransferDetail changeTransfer = transferService.updateStatus(status, id);
            String message = "Successfully";

            if (status.equals(TransferStatus.Approved.getValue())) {
                message = "Transfer Request Approved Successfully";
            }
            if (status.equals(TransferStatus.Rejected.getValue())) {
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

    @PutMapping("/multiple-asset-status-update")
    public ResponseEntity<ApiResponse> bulkAssetUpdateStatus(@RequestParam String status,
                                                             @RequestParam List<String> ids) {
        try {
            TransferDetail changeTransfer = transferService.bulkAssetUpdateStatus(status, ids);
            String message = "Successfully";

            if (status.equals(TransferStatus.Approved.getValue())) {
                message = "Transfer Request Approved Successfully";
            }
            if (status.equals(TransferStatus.Rejected.getValue())) {
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

    @GetMapping("/get-byid")
    public ResponseEntity<ApiResponse> getById(@RequestParam String id) {
        try {
            TransferDetail changeTransfer = transferService.getById(id);
            return ResponseEntity.ok().body(new ApiResponse(true, "Transfer Fetch Successfully", changeTransfer));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/get-status-wise-data-with-filter")
    public ResponseEntity<ApiResponse> getFilter(@RequestParam(required = false) String assetClass,
                                                 @RequestParam(required = false) String assetType, @RequestParam(required = false) String assetId,
                                                 @RequestParam(required = false) String status, Integer page, Integer size,
                                                 @RequestParam(required = false) String subClass, @RequestParam(required = false) String childId) {
        try {
            if (StringUtils.isEmpty(status)) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Status must not be null or empty", Collections.emptyList()));
            }
            TransferPendingResponse filterData = transferService.getDataWithFilter(assetClass, assetType, assetId,
                    status, page, size, subClass, childId);
            if (filterData == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.ok().body(new ApiResponse(true, "Transfer Fetch Successfully", filterData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/get-all-transfer-history-by-assetid")
    public ResponseEntity<ApiResponse> getAllTransferDataByAssetid(@RequestParam String assetId) {
        try {
            List<TransferDetail> transferData = transferService.getAllTransferDataByAssetid(assetId);
            if (transferData.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.ok().body(new ApiResponse(true, "Transfer History Fetch Successfully", transferData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }
}
