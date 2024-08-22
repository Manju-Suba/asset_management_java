package assetmanagement.controller.auth;

import assetmanagement.controller.asset.AssetTicketController;
import assetmanagement.dto.UsersDto;
import assetmanagement.exception.ResourceNotFoundException;
import assetmanagement.jwt.AuthTokenFilter;
import assetmanagement.model.Asset;
import assetmanagement.model.CheckList;
import assetmanagement.model.Users;
import assetmanagement.request.LoginRequest;
import assetmanagement.request.ResetPasswordRequest;
import assetmanagement.response.ApiResponse;
import assetmanagement.response.ChildIdDTO;
import assetmanagement.response.ResetResponse;
import assetmanagement.security.UserDetailsImpl;
import assetmanagement.service.RefreshTokenService;
import assetmanagement.service.TokenBlacklist;
import assetmanagement.service.UserService;
import assetmanagement.service.asset.AssetService;
import assetmanagement.service.masters.CheckListService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    public final UserService userService;
    public final RefreshTokenService refreshTokenService;
    private final AuthTokenFilter authTokenFilter;
    private final TokenBlacklist tokenBlacklist;
    private final AssetService assetService;
    private final CheckListService checkListService;
    private final AssetTicketController assetTicketController;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody Users user) {
        return userService.signUp(user);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest user) {
        return userService.signIn(user);
    }

    @PostMapping("/signout")
    public ResponseEntity<ApiResponse> logoutUser(HttpServletRequest request) {
        try {
            // Check if there is an authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
                String userId = userDetails.getId();
                refreshTokenService.deleteByUserId(userId);
                SecurityContextHolder.clearContext();

                String token = authTokenFilter.parseJwt(request);
                tokenBlacklist.blacklistToken(token);

                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, "Logout Successfully", Collections.emptyList()));

            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, Collections.emptyList()));
            }

        } catch (Exception e) {
            // Handle any exceptions gracefully
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error occurred during logout: ", e.getMessage()));
        }
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@RequestBody String token) {
        return userService.refreshToken(token);

    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> updateUsers(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        try {
            String rp = resetPasswordRequest.getPassword();
            String cp = resetPasswordRequest.getConfirmPassword();
            if (rp.equals(cp)) {
                Users updatedUsers = userService.forgotPassword(resetPasswordRequest);
                if (updatedUsers != null) {
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(new ApiResponse(true, "Password Reset Successfully", Collections.emptyList()));
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse(false, "Confirm password doesn't match to the new password",
                                Collections.emptyList()));
            }

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/fetch-by-mail")
    public ResponseEntity<UsersDto> getUserByMail(@RequestParam String email) {
        try {
            UsersDto users = userService.getUserByMail(email);
            if (users != null) {
                return ResponseEntity.ok(users);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/check-by-mail")
    public ResponseEntity<ResetResponse> checkByMail(@RequestParam String email) {
        try {
            ResetResponse resetResponse = userService.getCheckByMail(email);
            if (resetResponse != null) {
                return ResponseEntity.ok(resetResponse);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // qr code
    @GetMapping("/get-by-id")
    public ResponseEntity<ApiResponse> getAssetById(@RequestParam(required = false) String id,
                                                    @RequestParam(required = false) String company, @RequestParam(required = false) String plant) {
        if (StringUtils.isEmpty(id)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Id must not be null or empty", Collections.emptyList()));
        }
        try {
            Optional<Asset> getAsset = assetService.getAssetById(id, company, plant);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, "Data Fetched Successfully", getAsset));
        } catch (ResourceNotFoundException resourceError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, resourceError.getMessage(), Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("get-by-AssetClass")
    public ResponseEntity<ApiResponse> getCheckListByAssetClass(@RequestParam String assetClass,
                                                                @RequestParam String plant, @RequestParam String companyId) {
        try {
            CheckList checkLists = checkListService.getCheckListToAudit(assetClass, plant, companyId);
            return ResponseEntity.ok(new ApiResponse(true, "Check List Fetched Successfully", checkLists));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("get-childId-by-AssetId")
    public ResponseEntity<ApiResponse> getChildIdByAssetId(@RequestParam String assetId, @RequestParam String plant,
                                                           @RequestParam String companyId) {
        try {
            List<ChildIdDTO> checkLists = assetService.getChildIdByAssetId(assetId, plant, companyId);
            return ResponseEntity.ok(new ApiResponse(true, "Check List Fetched Successfully", checkLists));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/sap-ticket-request")
    public ResponseEntity<?> sapTicketRequest(@RequestHeader HttpHeaders headers) {
        try {
            final String authorization = headers.getFirst(HttpHeaders.AUTHORIZATION);
            if (authorization != null && authorization.toLowerCase().startsWith("basic ")) {
                return handleBasicAuth(authorization);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Authorization header missing or invalid",
                                Collections.emptyList()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    private ResponseEntity<?> handleBasicAuth(String authorization) {
        try {
            String base64Credentials = authorization.substring("Basic ".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            String[] values = credentials.split(":", 2);

            if (values.length == 2) {
                return authenticateAndProcessRequest(values[0], values[1]);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(false, "Invalid Authorization header format",
                                Collections.emptyList()));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Invalid Base64 encoding in Authorization header",
                            Collections.emptyList()));
        }
    }

    private ResponseEntity<?> authenticateAndProcessRequest(String username, String password) {
        try {
            userService.signIn(new LoginRequest(username, password));
            return assetTicketController.sapRequestTicket();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid login credentials", e.getMessage()));
        }
    }


}