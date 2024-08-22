package assetmanagement.response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;

import assetmanagement.response.masters.AssetCategoryDTO;
import assetmanagement.response.masters.AssetTypeDTO;
import assetmanagement.response.masters.BrandDTO;
import assetmanagement.response.masters.BusinessDTO;
import assetmanagement.response.masters.LocationDTO;
import assetmanagement.util.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetResponse {

    private String id;
    private String companyId;
    private String assetId;
    private String name;
    private BusinessDTO business;
    private AssetCategoryDTO assetCategory;
    private AssetTypeDTO assetType;
    private LocationDTO location;
    private BrandDTO brand;
    private String picture;

    // public String getPicture() {
    // if (this.picture != null && !this.picture.isEmpty()) {
    // return "/uploads/" + this.picture;
    // } else {
    // return null; // or return a default value if picture is null or empty
    // }
    // }
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
