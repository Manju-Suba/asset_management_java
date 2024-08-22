package assetmanagement.controller.masters;

import assetmanagement.dto.CheckListDto;
import assetmanagement.exception.ResourceNotFoundException;
import assetmanagement.model.CheckList;
import assetmanagement.response.ApiResponse;
import assetmanagement.response.CheckListResponse;
import assetmanagement.service.masters.CheckListService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/check-list/")
@RequiredArgsConstructor
public class CheckListController {
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    private static final String CHECKLIST_FETCHED_SUCCESSFULLY = "Check List Fetched Successfully";
    private static final String ID_NOT_NULL_OR_EMPTY_MESSAGE = "Id must not be null or empty";
    public final CheckListService checkListService;

    @PostMapping("save")
    public ResponseEntity<ApiResponse> saveCheckList(@RequestBody CheckListDto checkListDto) {

        try {
            CheckList createCheckList = checkListService.saveCheckList(checkListDto);
            return ResponseEntity.ok(new ApiResponse(true, "Check List Created Successfully", createCheckList));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(false, e.getMessage(), Collections.emptyList()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(false, "Already exist", Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }

    }

    @GetMapping("get-all")
    public ResponseEntity<ApiResponse> getCheckList(@RequestParam Integer page, @RequestParam Integer size,
                                                    boolean search, String value, String assetClass) {
        try {
            CheckListResponse checkLists = checkListService.getCheckList(page, size, search, value, assetClass);
            return ResponseEntity.ok(new ApiResponse(true, CHECKLIST_FETCHED_SUCCESSFULLY, checkLists));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @PutMapping("update")
    public ResponseEntity<ApiResponse> update(@RequestBody CheckListDto checkListDto) {

        try {
            CheckList checkList = checkListService.update(checkListDto);
            return ResponseEntity.ok(new ApiResponse(true, "Check List Updated Successfully", checkList));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @DeleteMapping("delete")
    public ResponseEntity<ApiResponse> delete(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, ID_NOT_NULL_OR_EMPTY_MESSAGE, Collections.emptyList()));
        }
        try {
            CheckList createCheckList = checkListService.delete(id);
            return ResponseEntity.ok(new ApiResponse(true, "Check List Deleted Successfully", createCheckList));

        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("get-by-AssetClass")
    public ResponseEntity<ApiResponse> getCheckListByAssetClass(@RequestParam String assetClass) {
        try {
            CheckList checkLists = checkListService.getCheckListByAssetClass(assetClass);
            return ResponseEntity.ok(new ApiResponse(true, CHECKLIST_FETCHED_SUCCESSFULLY, checkLists));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("get-by-class")
    public ResponseEntity<ApiResponse> getByAssetClass(@RequestParam String assetClass, @RequestParam String companyId,
                                                       @RequestParam String plant) {
        try {
            CheckList checkLists = checkListService.getByAssetClass(assetClass, companyId, plant);
            return ResponseEntity.ok(new ApiResponse(true, CHECKLIST_FETCHED_SUCCESSFULLY, checkLists));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }
}
