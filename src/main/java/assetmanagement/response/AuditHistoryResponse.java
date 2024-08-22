package assetmanagement.response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Base64;

import assetmanagement.model.Users;
import assetmanagement.util.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditHistoryResponse {

    private String id;
    private AssetResponse assetId;
    private Users auditBy;
    private LocalDate auditFixedDate;
    private LocalDate auditDate;
    private String previewImage;
    private String currentImage;
    private String previewImageWithPath;
    private String currentImageWithPath;
    private String remark;
    private String status;

    // public String getPreviewImageWithPath() {
    // if (this.previewImage != null && !this.previewImage.isEmpty()) {
    // return "/uploads/" + this.previewImage;
    // } else {
    // return null;
    // }
    // }

    // public String getCurrentImageWithPath() {
    // if (this.currentImage != null && !this.currentImage.isEmpty()) {
    // return "/uploads/" + this.currentImage;
    // } else {
    // return null;
    // }
    // }
    public byte[] pictureAsBytes(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {

            Path filePath = Paths.get(Constant.FILE_PATH).resolve(fileName).normalize();

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

    public String getPreviewImageWithPath() {
        byte[] pictureBytes = pictureAsBytes(this.previewImage);
        if (pictureBytes.length > 0) {
            String encodedImage = Base64.getEncoder().encodeToString(pictureBytes);
            return "data:image/jpg;base64," + encodedImage;
        }
        return null;
    }

    public String getCurrentImageWithPath() {
        byte[] pictureBytes = pictureAsBytes(this.currentImage);
        if (pictureBytes.length > 0) {
            String encodedImage = Base64.getEncoder().encodeToString(pictureBytes);
            return "data:image/jpg;base64," + encodedImage;
        }
        return null;
    }
}
