package assetmanagement.model;

import assetmanagement.enumData.ActiveInActive;
import assetmanagement.enumData.AvailableStatus;
import assetmanagement.model.masters.*;
import assetmanagement.util.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Base64;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Asset {

    @Id
    private String id;
    private String companyId;
    @DBRef
    private Business business;
    @DBRef
    private AssetCategory assetCategory;
    @DBRef
    private AssetType assetType;
    private String name;
    private String portNo;
    private String qrcode;
    private String costCenter;
    private String mainAssetNumber;
    private String subAssetNumber;
    private int realDepreciationArea;
    private int plannedUsefulLifeInYears;
    private int fiscalYear;
    private int transactionsForTheYearAffectingAssetValues;
    private String businessArea;
    @DBRef
    private Location location;
    @DBRef
    private Brand brand;
    private String cost;
    private LocalDate date;
    private String assetDomain;
    @DBRef
    private Employee spoc;
    private String assetAllocate;
    @DBRef
    private Employee employee;
    private String description;
    private String version;
    private LocalDate expiryDate;
    private String quantity;
    private String ipAddress;
    private String picture;
    private String document;
    private LocalDate damagedAt;
    private LocalDate purchasedDate;
    private String temporary;
    private LocalDate auditDate;
    private LocalDate nextAuditDate;
    private String status = ActiveInActive.ACTIVE.getValue();
    private String renewStatus;
    private String pictureWithPath;
    private String documentWithPath;
    private String assetId;
    private String costClassWise;
    private String latitudeAndLongitude;
    private String assetClass;
    private String subClass;
    private String childId;
    private String plant;
    private String serialNumber;
    private String assetStatus;
    private String availableStatus = AvailableStatus.Stock.getValue();
    private LocalDate assetAgeingFrom;
    private LocalDate capitalizationDate;
    private LocalDate assetRetirementDate;
    private LocalDate assetAgeingTo;
    private String assetLifetime;
    private String requiresAttention;
    private String upOrDowntime;
    private String warrantyStatus;
    private String noOfRoutinesExecuted;
    private String costBasedMajorAsset;
    private String costBasedMinorAsset;
    private Long costOfAsset;
    private String estimatedSalvageValue;
    private String usefulLife;
    private String netProfitOrBenefit;
    private String costOfInvestment;
    private String totalBenefit;
    private String totalCost;
    private String revenueGenerated;
    private String beginningTotalAsset;
    private String endingTotalAsset;
    private String netIncome;
    private String depreciation;
    private String returnOfInvestment;
    private String netBenefit;
    private String assetUtilization;
    private String averageTotalAsset;
    private String returnOfAsset;
    private LocalDate sapDate;
    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String updatedBy;
    @CreatedDate
    private LocalDate createdAt;
    @LastModifiedDate
    private LocalDate updatedAt;

    public Asset(String id) {
        this.id = id;
    }

    public String getDocumentWithPath() {
        if (this.document != null && !this.document.isEmpty()) {
            return "/uploads/" + this.document;
        } else {
            return null;
        }
    }


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
