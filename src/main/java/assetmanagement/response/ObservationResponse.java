package assetmanagement.response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import assetmanagement.model.Asset;
import assetmanagement.util.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObservationResponse {

    private String assetId;
    private String assetClass;
    private String plant;
    private LocalDate observationDate;
    private String previousImage;
    private String remarks;
    private String previousImageWithPath;

    // public String getPreviewImageWithPath() {
    // if (this.previousImage != null && !this.previousImage.isEmpty()) {
    // return "/uploads/" + this.previousImage;
    // } else {
    // return null; // or return a default value if picture is null or empty
    // }
    // }

    public byte[] pictureAsBytes() {
        if (this.previousImage != null && !this.previousImage.isEmpty()) {

            Path filePath = Paths.get(Constant.FILE_PATH).resolve(this.previousImage).normalize();

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
        byte[] pictureBytes = pictureAsBytes();
        if (pictureBytes.length > 0) {
            String encodedImage = Base64.getEncoder().encodeToString(pictureBytes);
            return "data:image/jpg;base64," + encodedImage;
        }
        return null;
    }
}
