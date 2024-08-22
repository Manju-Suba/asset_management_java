package assetmanagement.service;

import assetmanagement.dto.UsersDto;
import assetmanagement.model.Users;
import assetmanagement.request.LoginRequest;
import assetmanagement.request.ProfileRequest;
import assetmanagement.request.ResetPasswordRequest;
import assetmanagement.response.ApiResponse;
import assetmanagement.response.ResetResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface UserService {

    ResponseEntity<?> signIn(LoginRequest users);

    ResponseEntity<ApiResponse> signUp(Users users);

    ResponseEntity<?> refreshToken(String token);

    Users createUsers(Users users);

    UsersDto getUsersById(String userId);

    Users forgotPassword(ResetPasswordRequest resetPasswordRequest) throws IOException;

    List<UsersDto> getAllUsers();

    Users updateUsers(ProfileRequest profileRequest, MultipartFile file) throws IOException;

    Users deleteUsers(String userId);

    UsersDto getUserByMail(String email);

    ResetResponse getCheckByMail(String email);
}
