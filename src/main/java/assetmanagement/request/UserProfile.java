package assetmanagement.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    private String id;
    private String fullName;
    private String email;
    private String passWord;
    private String city;
    private String phoneNo;
    private String role;
    private String domain;
}
