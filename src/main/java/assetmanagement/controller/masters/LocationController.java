package assetmanagement.controller.masters;

import assetmanagement.model.masters.Location;
import assetmanagement.response.ApiResponse;
import assetmanagement.service.masters.LocationService;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class LocationController {
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    private static final String ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE = "Id must not be null or empty";
    public final LocationService locationService;

    //add new location
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody Location location) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Location Created Successfully", locationService.create(location)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, e.getMessage(), Collections.emptyList()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, "Location already exist", Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }


    //get all location table data
    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse> getAllLocation() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Data fetched successfully", locationService.getAll()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "failed to fetch Data", Collections.emptyList()));
        }
    }

    //get particular data
    @GetMapping("/get")
    public ResponseEntity<ApiResponse> getLocationById(@RequestParam String id) {
        try {
            if (StringUtils.isEmpty(id)) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
            }
            Location location = locationService.getLocationbyId(id);
            if (location != null) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Data fetched successfully", location));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
        return null;
    }

    // update the location
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> update(@RequestBody Location location) {
        try {
            String id = location.getId();
            if (StringUtils.isEmpty(id)) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
            }
            Location updatedLocation = locationService.update(location);
            if (updatedLocation != null) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Data Updated successfully", updatedLocation));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Location Not Found", Collections.emptyList()));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, e.getMessage(), Collections.emptyList()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, "Location already exist", Collections.emptyList()));

        } catch (Exception e) {
            // Handle other exceptions, return 500 Internal Server Error for simplicity
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    //remove the location
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> delete(@RequestParam String id) {
        try {
            if (StringUtils.isEmpty(id)) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, ID_MUST_NOT_BE_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
            }
            Location delete = locationService.delete(id);
            if (delete != null) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Data Deleted successfully", null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Not Found", Collections.emptyList()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));

        }
    }


}
