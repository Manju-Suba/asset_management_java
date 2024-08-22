package assetmanagement.dto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import assetmanagement.util.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferDetailDto {

    private String id;
    private String assetId;
    private String companyId;
    private String fromPlant;
    private String picture;
    private String toPlant;
    private String assetClass;
    private String subClass;
    private String childId;
    private String assetStatus;
    private String assetAgeing;
    private String assetLifetime;
    private String costOfAsset;
    private LocalDate requestRaisedDate;
    private LocalDate actionDate;
    private String status;
    private String pictureWithPath;
    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String updatedBy;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    // public TransferDetailDto(String id){
    // this.id=id;
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
