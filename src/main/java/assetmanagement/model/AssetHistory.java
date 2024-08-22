package assetmanagement.model;

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
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import assetmanagement.enumData.ActiveInActive;
import assetmanagement.model.masters.Employee;
import assetmanagement.model.masters.Location;
import assetmanagement.util.AuthUser;
import assetmanagement.util.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "asset_history")
public class AssetHistory {

    @Id
    private String id;
    private String companyId = AuthUser.getCompanyId();
    @DBRef
    private Asset assetId;
    private String assetNo;
    private String replaceAssetId;
    @DBRef
    private Employee employee;
    @DBRef
    private Location location;
    private LocalDate allocatedDate;
    private String assetBrand;
    private String charger;
    private String bag;
    private LocalDate getBackDate;
    private LocalDate retiralDate;
    private String type;
    private String reason;
    private String remark;
    private String retrialType;
    private LocalDate expiryDate;
    private String cost;
    private String picture;
    private LocalDate nextRenewedDate;
    private String status = ActiveInActive.ACTIVE.getValue();
    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String updatedBy;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    private String pictureWithPath;

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
