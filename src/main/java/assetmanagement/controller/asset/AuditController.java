package assetmanagement.controller.asset;

import assetmanagement.dto.AuditDto;
import assetmanagement.exception.EntityNotFoundException;
import assetmanagement.exception.ResourceNotFoundException;
import assetmanagement.model.Asset;
import assetmanagement.model.audit.Audit;
import assetmanagement.request.AuditRequest;
import assetmanagement.request.RequestWithFilter;
import assetmanagement.response.*;
import assetmanagement.service.asset.AuditService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
public class AuditController {

    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    private static final String NO_DATA_FOUND_MESSAGE = "No Data Found";
    private static final String AUDIT_FETCH_SUCCESSFULLY = "Audit Fetch Successfully";
    private final AuditService auditService;

    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse> getAll(@RequestParam String status) {
        try {
            List<AuditDto> auditFetch = auditService.getAll(status);
            if (auditFetch.isEmpty()) {
                return ResponseEntity.ok().body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.ok().body(new ApiResponse(true, AUDIT_FETCH_SUCCESSFULLY, auditFetch));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/get-byid")
    public ResponseEntity<ApiResponse> getById(@RequestParam String id) {
        try {
            Audit auditFetch = auditService.getById(id);
            return ResponseEntity.ok().body(new ApiResponse(true, AUDIT_FETCH_SUCCESSFULLY, auditFetch));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @PutMapping("/update-status")
    public ResponseEntity<ApiResponse> updateStatus(@RequestParam String status, @RequestParam String id,
            @RequestParam String userId) {
        try {
            Audit auditUpdate = auditService.updateStatus(status, id, userId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Audit Updated Successfully", auditUpdate));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (EntityNotFoundException entityNotFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, entityNotFoundException.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/get-filter")
    public ResponseEntity<ApiResponse> getFilter(@RequestParam String assetClass, @RequestParam String assetType,
            @RequestParam String status) {
        try {
            if (StringUtils.isEmpty(status)) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Status must not be null or empty", Collections.emptyList()));
            }
            List<Audit> filterData = auditService.getFilter(assetClass, assetType, status);
            if (filterData.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.ok().body(new ApiResponse(true, AUDIT_FETCH_SUCCESSFULLY, filterData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/pending-audit-asset")
    public ResponseEntity<ApiResponse> getPendingAuditAsset(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, boolean search, String value,
            RequestWithFilter requestWithFilter) {
        try {

            PendingAuditAssetResponse filterData = auditService.getPendingAuditAsset(requestWithFilter, page, size,
                    search, value);
            if (filterData == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.ok().body(new ApiResponse(true, AUDIT_FETCH_SUCCESSFULLY, filterData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/audit-fetch-pending")
    public ResponseEntity<ApiResponse> auditorFetch(RequestWithFilter requestWithFilter, Integer page, Integer size) {
        try {
            PendingAuditAssetResponse fetchData = auditService.auditorFetch(requestWithFilter, page, size);
            if (fetchData == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.ok().body(new ApiResponse(true, AUDIT_FETCH_SUCCESSFULLY, fetchData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/audit-fetch-byid")
    public ResponseEntity<ApiResponse> auditorFetchById(@RequestParam String id) {
        try {
            List<AuditResponse> fetchData = auditService.auditorFetchById(id);
            if (fetchData.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.ok().body(new ApiResponse(true, AUDIT_FETCH_SUCCESSFULLY, fetchData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @PostMapping("/save-audit-date")
    public ResponseEntity<ApiResponse> saveAuditDate(@RequestParam(required = false) String assetId,
            @RequestParam(required = false) LocalDate auditDate) {
        try {
            List<Asset> savedRecord = auditService.saveAuditDate(assetId, auditDate);
            return ResponseEntity.ok().body(new ApiResponse(true, AUDIT_FETCH_SUCCESSFULLY, savedRecord));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @PutMapping("/update-pre-image")
    public ResponseEntity<ApiResponse> uploadPreImage(MultipartFile file, @RequestParam String id) {
        try {
            if (!file.isEmpty()) {
                Asset assetsModels = auditService.uploadPreImage(file, id);

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
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @PostMapping("/action-create") // status Approved , Disposed
    public ResponseEntity<ApiResponse> createAudit(AuditRequest auditRequest,
            @RequestParam(required = false) MultipartFile file) throws IOException {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(new ApiResponse(false, "File is Required", Collections.emptyList()));
            }
            Audit auditAsset = auditService.createAudit(file, auditRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Audit Successfully", auditAsset));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/asset-audit-by-status") // with Filter
    public ResponseEntity<ApiResponse> assetAuditByStatus(RequestWithFilter requestWithFilter) {
        try {
            AuditorResponse audit = auditService.assetAuditByStatus(requestWithFilter);
            if (audit == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.ok().body(new ApiResponse(true, "Audit Successfully", audit));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/request-audit-asset")
    public ResponseEntity<ApiResponse> getRequestAuditAsset(@RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size, String assetClass, boolean search, String value) {

        try {
            RequestAuditAssetResponse audit = auditService.getRequestAuditAsset(page, size, assetClass, search, value);
            if (audit == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }

            return ResponseEntity.ok().body(new ApiResponse(true, AUDIT_FETCH_SUCCESSFULLY, audit));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    // @GetMapping("/completed-assets")
    public ResponseEntity<ApiResponse> getAuditCompletedAsset(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, boolean search, String value,
            RequestWithFilter requestWithFilter) {
        try {
            RequestAuditAssetResponse audit = auditService.getAuditCompletedAsset(requestWithFilter, page, size, search,
                    value);

            if (audit == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.ok().body(new ApiResponse(true, AUDIT_FETCH_SUCCESSFULLY, audit));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/audit-fetch-by-asset-id")
    public ResponseEntity<ApiResponse> auditorFetchByAssetId(@RequestParam String assetId) {
        try {
            List<AuditResponse> fetchData = auditService.auditorFetchByAssetId(assetId);
            if (fetchData.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.ok().body(new ApiResponse(true, AUDIT_FETCH_SUCCESSFULLY, fetchData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/completed-history")
    public ResponseEntity<ApiResponse> getAuditCompletedHistory(@RequestParam String assetId) {

        try {
            List<AuditHistoryResponse> audit = auditService.getAuditCompletedHistory(assetId);
            if (audit.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }

            return ResponseEntity.ok().body(new ApiResponse(true, AUDIT_FETCH_SUCCESSFULLY, audit));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/get-all-audit-data-by-assetid")
    public ResponseEntity<ApiResponse> getAllAuditDataByAssetId(@RequestParam String assetId) {
        try {
            List<Audit> auditData = auditService.allAuditDataByAssetId(assetId);
            if (auditData.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.ok().body(new ApiResponse(true, AUDIT_FETCH_SUCCESSFULLY, auditData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/completed-assets")
    public ResponseEntity<ApiResponse> getCompletedAudit(RequestWithFilter requestWithFilter) {

        try {
            AuditCompletedResponse audit = auditService.getCompletedAudit(requestWithFilter);
            if (audit == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }

            return ResponseEntity.ok().body(new ApiResponse(true, AUDIT_FETCH_SUCCESSFULLY, audit));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

}
