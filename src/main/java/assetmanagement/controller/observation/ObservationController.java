package assetmanagement.controller.observation;

import assetmanagement.model.audit.Observation;
import assetmanagement.response.ApiResponse;
import assetmanagement.response.ObservationCount;
import assetmanagement.service.observation.ObservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/observation")
@RequiredArgsConstructor
public class ObservationController {
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    public final ObservationService observationService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody Observation observation) {
        try {
            Observation data = observationService.create(observation);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Created Successfully", data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(false, e.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, Collections.emptyList()));
        }
    }

    @PreAuthorize("hasAuthority('Auditor')")
    @GetMapping("/getList")
    public ResponseEntity<ApiResponse> getListOfRecords(String assetClass, boolean search, String value, Integer page,
                                                        Integer size) {
        try {
            ObservationCount observationResponse = observationService.getList(assetClass, search, value, page, size);
            if (observationResponse == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, "No Data Found", Collections.emptyList()));
            }
            return ResponseEntity.ok().body(new ApiResponse(true, "Data Fetched Successfully", observationResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }

    }

    @GetMapping("/getParticularList")
    public ResponseEntity<ApiResponse> getParticularlist(String assetId, Integer page, Integer size) {
        try {
            ObservationCount observationResponse = observationService.getParticularList(assetId, page, size);
            if (observationResponse == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, "NO Data Found ", Collections.emptyList()));
            }
            return ResponseEntity.ok().body(new ApiResponse(true, "Data Fetched Successfully", observationResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

}
