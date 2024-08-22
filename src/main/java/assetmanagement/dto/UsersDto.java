package assetmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;

import assetmanagement.util.Constant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersDto {
    private String id;
    private String userId;
    private String companyId;
    private String fullName;
    private String email;
    private String status;
    private String city;
    private String phoneNo;
    private String plant;
    private String role;
    private String domain;
    private String profilePicture;
    private String pictureWithPath;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    // public String getPictureWithPath() {
    // if (this.profilePicture != null && !this.profilePicture.isEmpty()) {
    // return "/uploads/" + this.profilePicture;
    // } else {
    // return null;
    // }
    // }

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
