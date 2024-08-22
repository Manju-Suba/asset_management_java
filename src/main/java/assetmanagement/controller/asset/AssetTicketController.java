package assetmanagement.controller.asset;

import assetmanagement.dto.AssetTicketDTO;
import assetmanagement.exception.ResourceNotFoundException;
import assetmanagement.model.AssetTicket;
import assetmanagement.request.AssetTicketRequest;
import assetmanagement.response.ApiResponse;
import assetmanagement.response.AssetTicketResponse;
import assetmanagement.response.SapAssetTicketResponse;
import assetmanagement.response.TicketNoResponse;
import assetmanagement.service.UserService;
import assetmanagement.service.asset.AssetTicketService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/asset-ticket")
@RequiredArgsConstructor
public class AssetTicketController {
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    private static final String DATA_FETCHED_SUCCESSFULLY = "Data Fetched Successfully";
    private static final String ID_NOT_NULL_OR_EMPTY_MESSAGE = "Id must not be null or empty";
    private static final String NO_DATA_FOUND_MESSAGE = "No Data Found";
    private static final String ASSET_ALREADY_EXIST = "Asset already exist";
    private static final String ASSET_TICKET_FETCHED_SUCCESSFULLY = "Asset Ticket Fetched Successfully";

    public final AssetTicketService assetTicketService;
    public final UserService userService;

    @GetMapping("/fetch-all-ticket")
    public ResponseEntity<ApiResponse> fetchAllTicket(@RequestParam(required = false) String assetClass,
                                                      @RequestParam(required = false) String ticketNo, @RequestParam(required = false) Boolean search,
                                                      @RequestParam(required = false) String value,
                                                      Integer page, Integer size) {
        try {
            AssetTicketResponse assetTicket = assetTicketService.fetchAllTicket(assetClass, ticketNo, search, value,
                    page, size);
            if (assetTicket == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, ASSET_TICKET_FETCHED_SUCCESSFULLY, assetTicket));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/get-by-id")
    public ResponseEntity<ApiResponse> getAssetById(@RequestParam(required = true) String id) {
        if (StringUtils.isEmpty(id)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, ID_NOT_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
        }
        try {
            Optional<AssetTicket> getAsset = assetTicketService.getAssetById(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, DATA_FETCHED_SUCCESSFULLY, getAsset));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> update(@RequestParam(required = true) String id,
                                              AssetTicketRequest assetTicket) {
        if (id == null || id.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, ID_NOT_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
        }
        try {
            AssetTicket updatedData = assetTicketService.update(id, assetTicket);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Asset Ticket Updated Successfully", updatedData));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(false, ASSET_ALREADY_EXIST, Collections.emptyList()));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/ticketNo")
    public ResponseEntity<ApiResponse> fetchAllTicket() {
        try {
            List<AssetTicket> assetTicket = assetTicketService.getTicketNo();
            if (assetTicket == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, ASSET_TICKET_FETCHED_SUCCESSFULLY, assetTicket));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/asset-creation-filter-data")
    public ResponseEntity<ApiResponse> assetFilterData(@RequestParam(required = true) String type) {
        try {
            List<AssetTicketDTO> assetTicket = assetTicketService.assetCreationFilterData(type);
            if (assetTicket == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, ASSET_TICKET_FETCHED_SUCCESSFULLY, assetTicket));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> create(AssetTicketRequest assetTicket) {
        try {
            AssetTicket createdTicket = assetTicketService.create(assetTicket);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Asset Created Successfully", createdTicket));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(false, ASSET_ALREADY_EXIST, Collections.emptyList()));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @DeleteMapping("/detele")
    public ResponseEntity<ApiResponse> deleteAssetTicket(@RequestParam(required = true) String id) {
        if (StringUtils.isEmpty(id)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, ID_NOT_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
        }
        try {
            Optional<AssetTicket> getAsset = assetTicketService.deleteAssetTicket(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, "Asset Ticket Deleted Successfully", getAsset));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @PutMapping("/staus-update")
    public ResponseEntity<ApiResponse> statusUpdate(@RequestParam(required = true) String id,
                                                    @RequestParam(required = true) String status) {
        if (id == null || id.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, ID_NOT_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
        }
        try {
            AssetTicket updatedData = assetTicketService.statusUpdate(id, status);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Asset Updated Successfully", updatedData));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(false, ASSET_ALREADY_EXIST, Collections.emptyList()));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @PreAuthorize("hasAuthority('Sap')")
    @GetMapping("/sap-request")
    public ResponseEntity<ApiResponse> sapRequestTicket() {
        try {
            SapAssetTicketResponse assetTicket = assetTicketService.sapRequestTicket();
            if (assetTicket == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, "No data found", Collections.emptyList()));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, ASSET_TICKET_FETCHED_SUCCESSFULLY, assetTicket));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Internal server error", e.getMessage()));
        }
    }

    @GetMapping("/ticket-no-count")
    public ResponseEntity<ApiResponse> getTicketNoCount() {
        try {
            TicketNoResponse assetTicket = assetTicketService.getTicketNoCount();
            if (assetTicket == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, "Asset Ticket No Count Fetched Successfully", assetTicket));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }


    @GetMapping("/fetch-statuswise-asset-ticket")
    public ResponseEntity<ApiResponse> fetchStatusWiseAssetTicket(@RequestParam(required = false) String assetClass,
                                                      @RequestParam(required = false) String ticketNo, @RequestParam(required = false) boolean search,
                                                      @RequestParam(required = false) String value,
                                                      Integer page, Integer size, @RequestParam String ticketType) {
        try {
            AssetTicketResponse assetTicket = assetTicketService.fetchStatusWiseTicket(assetClass, ticketNo, search, value,
                    page, size, ticketType);
            if (assetTicket == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, NO_DATA_FOUND_MESSAGE, Collections.emptyList()));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, "Asset Ticket Fetched Successfully", assetTicket));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @PutMapping("/sap-update")
    public ResponseEntity<ApiResponse> sapUpdate(@RequestParam(required = true) String ticketNo,@RequestParam(required = true) String sapStatus) {
        System.out.println("SAP Status: " + sapStatus);
        try {
            AssetTicket updatedData = assetTicketService.sapUpdate(ticketNo, sapStatus);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Asset Ticket Approved Successfully", updatedData));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

}
