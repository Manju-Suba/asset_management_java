package assetmanagement.response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import assetmanagement.util.Constant;
import org.springframework.beans.factory.annotation.Value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisposedAsset {
    private String id;
    private String companyId;
    private String assetId;
    private String assetClass;
    private String childId;
    private String plant;
    private String availableStatus;
    private String picture;
    private String pictureWithPath;
    private String remarks;

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
