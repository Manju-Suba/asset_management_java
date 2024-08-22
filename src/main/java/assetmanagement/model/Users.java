package assetmanagement.model;

import assetmanagement.enumData.ActiveInActive;
import assetmanagement.util.Constant;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class Users {

    @Id
    private String id;
    @NotNull(message = "field is mandatory")
    private String userId;
    @NotNull(message = "field is mandatory")
    private String companyId;
    @NotNull(message = "field is mandatory")
    private String fullName;
    @NotNull(message = "field is mandatory")
    private String email;
    private String password;
    private String status = ActiveInActive.ACTIVE.getValue();
    private String city;
    private String phoneNo;
    @NotNull(message = "field is mandatory")
    private String role;
    private String plant;
    @NotNull(message = "field is mandatory")
    private String domain;
    private String profilePicture;
    private String pictureWithPath;
    private Date createdAt;
    private Date updatedAt;

    public Users(String id) {
        this.id = id;
    }

    public byte[] pictureAsBytes() {
        if (this.profilePicture != null && !this.profilePicture.isEmpty()) {

            Path filePath = Paths.get(Constant.FILE_PATH).resolve(this.profilePicture).normalize();

            if (filePath != null) {
                try {
                    return Files.readAllBytes(filePath);
                } catch (IOException e) {
                    e.printStackTrace(); // You may want to handle this more gracefully
                }
            }
        }
        return new byte[0];
    }

    public String getPictureWithPath() {
        byte[] pictureBytes = pictureAsBytes();
        if (pictureBytes.length > 0) {
            String encodedImage = Base64.getEncoder().encodeToString(pictureBytes);
            return "data:image/jpg;base64," + encodedImage;
        }
        return null;
    }

}