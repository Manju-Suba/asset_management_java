package assetmanagement.dto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;

import assetmanagement.util.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultiUsersTransferDTO {

    private String id;
    private String companyId;
    private String picture;
    private String assetId;
    private String fromPlant;
    private String toPlant;
    private String remarks;
    private String assetClass;
    private String status;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String pictureWithPath;

    // public String getPictureWithPath() {
    // if (this.picture != null && !this.picture.isEmpty()) {
    // return "/uploads/" + this.picture;
    // } else {
    // return null;
    // }
    // }
    public byte[] pictureAsBytes() {
        if (this.picture != null && !this.picture.isEmpty()) {

            Path filePath = Paths.get(Constant.FILE_PATH).resolve(this.picture).normalize();

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
