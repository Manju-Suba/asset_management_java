package assetmanagement.serviceImpl;

import assetmanagement.dto.UsersDto;
import assetmanagement.enumData.ActiveInActive;
import assetmanagement.exception.ValidationException;
import assetmanagement.jwt.JwtUtils;
import assetmanagement.model.RefreshToken;
import assetmanagement.model.ResetPassword;
import assetmanagement.model.Users;
import assetmanagement.repository.RefreshTokenRepository;
import assetmanagement.repository.ResetPasswordRepository;
import assetmanagement.repository.UserRepository;
import assetmanagement.request.LoginRequest;
import assetmanagement.request.ProfileRequest;
import assetmanagement.request.ResetPasswordRequest;
import assetmanagement.response.ApiResponse;
import assetmanagement.response.ResetResponse;
import assetmanagement.response.UserResponse;
import assetmanagement.security.UserDetailsImpl;
import assetmanagement.service.MailService;
import assetmanagement.service.RefreshTokenService;
import assetmanagement.service.UserService;
import assetmanagement.util.Format;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static assetmanagement.util.Constant.URL_FE;
import static java.util.Map.entry;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final MailService mailService;
    private final ResetPasswordRepository resetPasswordRepository;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final Validator validator;
    LocalDateTime currentTime = LocalDateTime.now();
    @Value("${upload.path}")
    private String fileBasePath;
    @Value("${hepl.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Override
    public ResponseEntity<ApiResponse> signUp(Users users) {
        Set<ConstraintViolation<Users>> errors = validator.validate(users);
        if (!errors.isEmpty()) {
            String errorMessage = errors.stream().map(error -> error.getPropertyPath() + ": " + error.getMessage())
                    .collect(Collectors.joining("; "));
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, errorMessage, Collections.emptyList()));

        }
        try {
            if (userRepository.existsByEmail(users.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse(false, "Email Id is Already Exist. Try a Different Email Id.",
                                Collections.emptyList()));
            }

            users.setPassword(passwordEncoder.encode(users.getPassword()));
            Date currentdate = new Date();
            users.setCreatedAt(currentdate);
            users.setUpdatedAt(currentdate);
            Users savedUser = userRepository.save(users);

            if (savedUser.getId() != null) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ApiResponse(true, "User Created Successfully", savedUser));
            } else {
                return ResponseEntity.internalServerError()
                        .body(new ApiResponse(true, "User Not Created Successfully", savedUser));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(true, "Internal Server Error", e.getMessage()));
        }

    }

    @Override
    public ResponseEntity<ApiResponse> signIn(LoginRequest users) {

        Set<ConstraintViolation<LoginRequest>> errors = validator.validate(users);

        if (!errors.isEmpty()) {
            String errorMessage = errors.stream().map(error -> error.getPropertyPath() + ": " + error.getMessage())
                    .collect(Collectors.joining("; "));
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, errorMessage, Collections.emptyList()));
        }
        try {

            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(users.getEmail(), users.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            // for refresh token with expiry time
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
            Instant expiryInstant = refreshToken.getExpiryDate();
            LocalDateTime expiryDateTime = LocalDateTime.ofInstant(expiryInstant, ZoneId.systemDefault());
            String pattern = "yyyy-MM-dd HH:mm:ss";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            String formattedExpiryTime = expiryDateTime.format(formatter);

            // token generate keys
            Map<String, Object> userDataMap = Map.ofEntries(
                    entry("id", userDetails.getId()),
                    entry("domian", userDetails.getDomain()),
                    entry("phoneNo", userDetails.getPhoneNo()),
                    entry("plant", userDetails.getPlant()),
                    entry("role", userDetails.getAuthorities()),
                    entry("userId", userDetails.getUserId()),
                    entry("username", userDetails.getFullName()),
                    entry("email", userDetails.getEmail()),
                    entry("profile", userDetails.getPictureWithPath()),
                    entry("companyId", userDetails.getCompanyId()),
                    entry("refreshToken", refreshToken.getToken()),
                    entry("refreshTokenExpiryTime", formattedExpiryTime));

            String jwt = jwtUtils.generateJwtToken(userDetails.getEmail(), userDataMap);

            Date expirationTime = jwtUtils.getExpirationDateFromJwtToken(jwt);
            List<UserResponse> userResponses = new ArrayList<>();
            UserResponse userResponse = new UserResponse();
            userResponse.setExpirationTime(expirationTime);
            userResponse.setToken(jwt);
            userResponse.setRefreshToken(refreshToken.getToken());
            userResponse.setRefreshTokenExpiryTime(formattedExpiryTime);
            userResponses.add(userResponse);

            return ResponseEntity.ok().body(new ApiResponse(true, "User Login Successfully", userResponses));
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(false, "Credentials Mismatch"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Internal Server Error", e.getMessage()));
        }
    }

    @Override
    public Users createUsers(Users users) {
        Date currentdate = new Date();
        users.setCreatedAt(currentdate);
        users.setUpdatedAt(currentdate);
        users.setStatus(ActiveInActive.ACTIVE.getValue());
        String encodedPw = passwordEncoder.encode(users.getPassword());
        users.setPassword(encodedPw);
        return userRepository.save(users);
    }

    @Override
    public UsersDto getUsersById(String userId) {
        Optional<UsersDto> optionalUsers = userRepository.findByIdAndStatus(userId, ActiveInActive.ACTIVE.getValue());
        return optionalUsers.orElseThrow(
                () -> new IllegalArgumentException("User with ID " + userId + " not found or not active."));
    }

    @Override
    public List<UsersDto> getAllUsers() {
        return userRepository.findAllActive();
    }

    @Override
    public Users updateUsers(ProfileRequest profileRequest, MultipartFile file) throws IOException {
        final List<String> allowedImageExtensions = Arrays.asList("jpg", "jpeg", "png");
        Optional<Users> existingUsersId = userRepository.findByIdAndStatusActive(profileRequest.getId());

        if (existingUsersId.isPresent()) {
            Users usersupdate = existingUsersId.get();
            usersupdate.setFullName(profileRequest.getFullName());
            Date date = new Date();
            usersupdate.setUpdatedAt(date);

            if (file != null && !file.isEmpty()) {
                String originalName = file.getOriginalFilename();
                String extension = getFileExtension(originalName);
                if (extension == null || !allowedImageExtensions.contains(extension.toLowerCase())) {
                    throw new IllegalArgumentException(
                            "Invalid image format. Only JPG, JPEG, and PNG files are allowed.");
                }
                String fileName = Format.formatDate() + "_" + originalName;
                Path path = Path.of(fileBasePath + fileName);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                usersupdate.setProfilePicture(fileName);
            }
            return userRepository.save(usersupdate);

        }
        return null;
    }

    @Override
    public Users deleteUsers(String userId) {
        Optional<Users> usersModelId = userRepository.findByIdAndStatusActive(userId);
        if (usersModelId.isPresent()) {
            Users usersModelDelete = usersModelId.get();
            usersModelDelete.setStatus("inactive");
            return userRepository.save(usersModelDelete);
        }
        return null;
    }

    @SneakyThrows
    @Override
    public UsersDto getUserByMail(String email) {
        Optional<UsersDto> optionalUser = userRepository.findByEmailAndStatusActive(email);
        if (optionalUser.isPresent()) {
            UsersDto user = optionalUser.get();
            Map<String, Object> content = new HashMap<>();
            String recipientEmail = user.getEmail();
            String resetLink = generateResetLink(user);
            String subject = "Password Reset Link";
            content.put("data", resetLink);
            content.put("UserName", user.getFullName());
            String emailSent = mailService.sendRequestMail(content, subject, recipientEmail);
            if ("success".equals(emailSent)) {
                saveResetPasswordData(user, resetLink);
            } else throw new RuntimeException("Failed to send email to " + recipientEmail);
            return user;
        } else {
            throw new UsernameNotFoundException("User with email " + email + " not found or not active.");
        }
    }

    @Override
    public ResetResponse getCheckByMail(String email) {
        List<ResetResponse> checks = resetPasswordRepository.findByEmailOrderByTimeDesc(email);
        if (!checks.isEmpty()) {
            ResetResponse resetDto = checks.get(0);
            Duration duration = Duration.between(resetDto.getTime(), LocalDateTime.now());
            long hoursDifference = duration.toHours();
            if (hoursDifference >= 24) {
                resetDto.setUrlStatus(ActiveInActive.EXPIRED.getValue());
                resetDto.setActiveStatus(ActiveInActive.EXPIRED.getValue());
                updatedResetPasswordData(email, ActiveInActive.EXPIRED.getValue(), resetDto.getId());
            }
            resetDto.setExpiredHours(hoursDifference);
            return resetDto;
        } else {
            return null;
        }
    }

    public void updatedResetPasswordData(String email, String status, String id) {
        ResetPassword resetPassword = resetPasswordRepository.findByIdAndEmail(id, email);
        resetPassword.setActiveStatus(status);
        resetPassword.setUrlStatus(status);
        Date currentdate = new Date();
        resetPassword.setUpdatedAt(currentdate);
        resetPasswordRepository.save(resetPassword);
    }

    public void saveResetPasswordData(UsersDto user, String link) {
        ResetPassword resetPassword = new ResetPassword();
        resetPassword.setTime(LocalDateTime.now());
        resetPassword.setUrl(link);
        resetPassword.setEmail(user.getEmail());
        resetPassword.setActiveStatus(ActiveInActive.ACTIVE.getValue());
        Date currentdate = new Date();
        resetPassword.setCreatedAt(currentdate);
        resetPassword.setUpdatedAt(currentdate);
        resetPasswordRepository.save(resetPassword);
    }

    @Override
    public ResponseEntity<ApiResponse> refreshToken(String token) {
        String requestRefreshToken = token.replace("\"", "");

        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByToken(requestRefreshToken);
        RefreshToken refreshToken = refreshTokenOptional
                .orElseThrow(() -> new ValidationException("Refresh token not found or expired"));
        Users user = refreshToken.getUsers(); // Assuming Users is the entity associated with the user
        // Extract user information

        Instant expiryInstant = refreshToken.getExpiryDate();
        LocalDateTime expiryDateTime = LocalDateTime.ofInstant(expiryInstant, ZoneId.systemDefault());
        String pattern = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        String formattedExpiryTime = expiryDateTime.format(formatter);

        Map<String, Object> responseData = Map.ofEntries(
                entry("id", user.getId()),
                entry("domian", user.getDomain()),
                entry("phoneNo", user.getPhoneNo()),
                entry("role", user.getRole()),
                entry("userId", user.getUserId()),
                entry("username", user.getFullName()),
                entry("email", user.getEmail()),
                entry("companyId", user.getCompanyId()),
                entry("profile", user.getPictureWithPath()),
                entry("refreshToken", requestRefreshToken),
                entry("refreshTokenExpiryTime", formattedExpiryTime));

        String jwt = jwtUtils.generateRefreshJwtToken(user.getEmail(), responseData);

        Date expirationTime = jwtUtils.getExpirationDateFromJwtToken(jwt);
        List<UserResponse> userResponses = new ArrayList<>();
        UserResponse userResponse = new UserResponse();
        userResponse.setExpirationTime(expirationTime);
        userResponse.setToken(jwt);
        userResponse.setRefreshToken(refreshToken.getToken());
        userResponse.setRefreshTokenExpiryTime(formattedExpiryTime);
        userResponses.add(userResponse);
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUsers)
                .map(users -> ResponseEntity.ok(new ApiResponse(true, "Token Generated", userResponses)))
                .orElseThrow(() -> new ValidationException("Refresh token not found or expired"));

    }

    private String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
    }

    private String generateResetLink(UsersDto user) {
        String encodedEncryptedBytes = Base64.getEncoder().encodeToString(user.getEmail().getBytes());
        String strongEncodedEncryptedBytes = Base64.getEncoder().encodeToString(encodedEncryptedBytes.getBytes());
        return URL_FE + "/#/reset-password/" + strongEncodedEncryptedBytes;
    }

    @Override
    public Users forgotPassword(ResetPasswordRequest resetPasswordRequest) {
        Date currentdate = new Date();

        Optional<Users> existingUsersId = userRepository.findByEmailAndStatus(resetPasswordRequest.getEmail(),
                ActiveInActive.ACTIVE.getValue());
        if (existingUsersId.isPresent()) {

            ResetPassword resetPassword = resetPasswordRepository
                    .findFirstByEmailOrderByCreatedAtDesc(resetPasswordRequest.getEmail());
            resetPassword.setActiveStatus(ActiveInActive.UPDATED.getValue());
            resetPassword.setUrlStatus(ActiveInActive.UPDATED.getValue());
            resetPassword.setUpdatedAt(currentdate);
            resetPasswordRepository.save(resetPassword);

            Users usersupdate = existingUsersId.get();
            usersupdate.setUpdatedAt(currentdate);
            usersupdate.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
            return userRepository.save(usersupdate);
        }
        return null;
    }

}
