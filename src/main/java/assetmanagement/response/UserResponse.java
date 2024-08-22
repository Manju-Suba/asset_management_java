package assetmanagement.response;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    // private String id;
    // private String userId;
    // private String fullName;
    // private String email;
    // private String city;
    // private String phone;
    // private String role;
    // private String domain;
    private Date expirationTime;
    private String token;
    private String refreshToken;
    private String refreshTokenExpiryTime;


}