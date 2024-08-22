package assetmanagement.controller.asset;

import assetmanagement.enumData.DisposedStatus;
import assetmanagement.exception.ResourceNotFoundException;
import assetmanagement.model.Asset;
import assetmanagement.model.audit.Audit;
import assetmanagement.model.disposed.DisposedDetail;
import assetmanagement.request.RenewedRequest;
import assetmanagement.request.ReplacedRequest;
import assetmanagement.request.RequestWithFilter;
import assetmanagement.response.*;
import assetmanagement.service.asset.DisposedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/disposed")
@RequiredArgsConstructor
public class DisposedController {

    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    private static final String NO_DATA_FOUND_MESSAGE = "No Data Found";
    private static final String DISPOSED_FETCH_SUCCESS_MESSAGE = "Disposed Fetch Successfully";
    private final DisposedService disposedService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> create(@RequestBody DisposedDetail disposedDetail) throws IOException {
        try {
            DisposedDetail createdDisposedRequest = disposedService.create(disposedDetail);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Disposed Request Created Successfully", createdDisposedRequest));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/get-by-id")
    public ResponseEntity<ApiResponse> getById(@RequestParam String id) {
        try {
            DisposedDetail getDisposed = disposedService.getById(id);
            return ResponseEntity.ok().body(new ApiResponse(true, DISPOSED_FETCH_SUCCESS_MESSAGE, getDisposed));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse> getAll(@RequestParam String status) {
        try {
            List<DisposedDetail> fetchDisposed = disposedService.getAll(status);
            if (fetchDisposed.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.ok().body(new ApiResponse(true, DISPOSED_FETCH_SUCCESS_MESSAGE, fetchDisposed));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @PutMapping("/update-status")
    public ResponseEntity<ApiResponse> updateStatus(@RequestParam String id, @RequestParam String status,
                                                    @RequestParam String reason, @RequestParam String userId) throws IOException {
        try {
            DisposedDetail changeDisposed = disposedService.updateStatus(id, status, reason, userId);
            String message = "Successfully";
            if (status.equals(DisposedStatus.Disposed.getValue())) {
                message = "Disposed Successfully";
            }
            if (status.equals(DisposedStatus.Renewed.getValue())) {
                message = "Renewed Successfully";
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, message, changeDisposed));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/closed-disposed")
    public ResponseEntity<ApiResponse> closedDisposed(RequestWithFilter requestWithFilter, boolean search, String value, Integer page, Integer size) {
        try {
            CloseToDisposedResponse fetchDisposed = disposedService.closedDisposed(requestWithFilter, search, value, page, size);
            if (fetchDisposed == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.ok().body(new ApiResponse(true, DISPOSED_FETCH_SUCCESS_MESSAGE, fetchDisposed));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/pending-request")
    public ResponseEntity<ApiResponse> pendingRequest(RequestWithFilter requestWithFilter, Integer page, Integer size) {
        try {
            PendingRequestResponse auditRequest = disposedService.pendingRequest(requestWithFilter, page, size);
            if (auditRequest == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.ok().body(new ApiResponse(true, "Pending Fetch Successfully", auditRequest));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @PutMapping("/approved-request")
    public ResponseEntity<ApiResponse> approvedRequest(@RequestParam String id) {
        try {
            Audit auditRequest = disposedService.approvedRequest(id);
            return ResponseEntity.ok().body(new ApiResponse(true, "Approved Successfully", auditRequest));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @PutMapping("/rejected-request")
    public ResponseEntity<ApiResponse> rejectedRequest(@RequestParam String id) {
        try {
            Audit auditRequest = disposedService.rejectedRequest(id);
            return ResponseEntity.ok().body(new ApiResponse(true, "Rejected Successfully", auditRequest));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/fetch-data")
    public ResponseEntity<ApiResponse> fetchData(RequestWithFilter requestWithFilter, Integer page, Integer size, boolean search, String value) {

        try {
            AssetDisposedResponse auditRequest = disposedService.fetchData(requestWithFilter, page, size, search, value);
            if (auditRequest == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.ok().body(new ApiResponse(true, "Fetch Successfully", auditRequest));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/fetch-by-status")
    public ResponseEntity<ApiResponse> fetchByStatus(@RequestParam String status, RequestWithFilter requestWithFilter, boolean search, String value, Integer page, Integer size) {
        try {
            ReplacedResponse assetByStatus = disposedService.fetchByStatus(status, requestWithFilter, search, value, page, size);
            if (assetByStatus == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.ok().body(new ApiResponse(true, "Fetched Successfully", assetByStatus));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @PostMapping("/action-replace")
    public ResponseEntity<ApiResponse> actionReplace(ReplacedRequest replacedRequest, MultipartFile file)
            throws IOException {
        try {
            Asset asset = disposedService.actionReplace(replacedRequest, file);
            return ResponseEntity.ok().body(new ApiResponse(true, "Updated Successfully", asset));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @PostMapping("/action-renewed")
    public ResponseEntity<ApiResponse> actionRenew(RenewedRequest renewedRequest, MultipartFile file)
            throws IOException {
        try {
            Asset asset = disposedService.actionRenew(renewedRequest, file);
            return ResponseEntity.ok().body(new ApiResponse(true, "Updated Successfully", asset));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/get-all-renew")
    public ResponseEntity<ApiResponse> getAllRenewed(RequestWithFilter requestWithFilter, boolean search, String value, Integer page, Integer size) {
        try {
            RenewedResponse fetchListRenew = disposedService.getAllRenewed(requestWithFilter, search, value, page, size);
            if (fetchListRenew == null) {
                return ResponseEntity.ok().body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.ok().body(new ApiResponse(true, "Data Fetched Successfully", fetchListRenew));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

}