package assetmanagement.controller;

import assetmanagement.dto.UsersDto;
import assetmanagement.model.Users;
import assetmanagement.request.ProfileRequest;
import assetmanagement.response.ApiResponse;
import assetmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<String> postUsers(@RequestBody Users users) {
        try {
            userService.createUsers(users);
            return ResponseEntity.ok("User_Created");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_SERVER_ERROR_MESSAGE);
        }
    }

    @GetMapping("/fetchsingleuser/{id}")
    public ResponseEntity<ApiResponse> getuserbById(@PathVariable("id") String userId) {
        try {
            UsersDto usersDto = userService.getUsersById(userId);
            if (usersDto != null) {
                return ResponseEntity.ok().body(new ApiResponse(true, "Data Fetched Successfully", usersDto));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(true, "Data Not Found", Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/fetchallusers")
    public ResponseEntity<List<UsersDto>> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/updatedusers")
    public ResponseEntity<ApiResponse> updateUsers(ProfileRequest profileRequest, MultipartFile file) {
        try {
            Users updatedUsers = userService.updateUsers(profileRequest, file);
            if (updatedUsers != null) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ApiResponse(true, "Data Updated Successfully", Collections.emptyList()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(true, "Data Not Found", Collections.emptyList()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @DeleteMapping("/deleteduser/{id}")
    public ResponseEntity<Object> delUsers(@PathVariable("id") String id) {
        try {
            Users delete = userService.deleteUsers(id);
            if (delete != null) {
                return ResponseEntity.ok("Sucessfully Deleted");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Users not found for id: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete Users");

        }
    }

}
