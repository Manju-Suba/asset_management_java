package assetmanagement.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import assetmanagement.enumData.ActiveInActive;
import assetmanagement.enumData.AvailableStatus;
import assetmanagement.model.masters.AssetCategory;
import assetmanagement.model.masters.AssetType;
import assetmanagement.model.masters.Brand;
import assetmanagement.model.masters.Business;
import assetmanagement.model.masters.Employee;
import assetmanagement.model.masters.Location;
import assetmanagement.util.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "asset")
public class AssetSAP {
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
    @JsonProperty("COST CENTER")
    private String costCenter;
    private String mainAssetNumber;
    private String subAssetNumber;
    @JsonProperty("PLANNED USEFUL LIFE IN YEARS")
    private String usefulLife;
    // private int plannedUsefulLifeInYears;
    @JsonProperty("FISCAL YEAR")
    private int fiscalYear;
    @JsonProperty("TRANSACTIONS FOR THE YEAR AFFECTING ASSET VALUES")
    private int transactionsForTheYearAffectingAssetValues;
    @JsonProperty("CAPITALIZATION DATE")
    private String capitalization;
    private LocalDate capitalizationDate;
    @JsonProperty("ASSSET RETIREMENT DATE")
    private String assetRetirement;
    private LocalDate assetRetirementDate;
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
    @JsonProperty("ASSET DESCRIPTION")
    private String description;
    private String version;
    private LocalDate expiryDate;
    private String Quantity;
    private String ipAddress;
    private String picture;
    private String document;
    private LocalDate damagedAt;
    private LocalDate purchasedDate;
    private String temporary;
    private LocalDate auditDate;
    private String status = ActiveInActive.ACTIVE.getValue();
    private String renewStatus;
    private String pictureWithPath;
    private String documentWithPath;

    @JsonProperty("MAIN ASSET NUMBER")
    private String assetId;
    private String costClassWise;
    private String latitudeAndLongitude;
    @JsonProperty("ASSET CLASS")
    private String assetClass;
    private String subClass;
    @JsonProperty("SUB ASSET NUMBER")
    private String childId;
        @JsonProperty("BUSINESS AREA")
    private String plant;
    @JsonProperty("REAL DEPRECIATION AREA")
    private String depreciation;
    private String serialNumber;
    private String assetStatus;
    private String availableStatus = AvailableStatus.Stock.getValue();
    @JsonProperty("DEP START DATE ON")
    private String depStart;

    private LocalDate assetAgeingFrom;
    private LocalDate assetAgeingTo;
    private String assetLifetime;
    private String requiresAttention;
    private String upOrDowntime;
    private String warrantyStatus;
    private String noOfRoutinesExecuted;
    private String costBasedMajorAsset;
    private String costBasedMinorAsset;
    @JsonProperty("ACQUIS.VAL.")
    private Long costOfAsset;
    @JsonProperty("BOOK VAL.")
    private String estimatedSalvageValue;
    private String netProfitOrBenefit;
    private String costOfInvestment;
    private String totalBenefit;
    private String totalCost;
    private String revenueGenerated;
    private Long beginningTotalAsset;
    private Long endingTotalAsset;
    private String netIncome;
    private String returnOfInvestment;
    private String netBenefit;
    private String assetUtilization;
    private String averageTotalAsset;
    private String returnOfAsset;
    // private String standardDep;
    private LocalDate sapDate;
    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String updatedBy;
    @CreatedDate
    private LocalDate createdAt;
    @LastModifiedDate
    private LocalDate updatedAt;

    public AssetSAP(String id) {
        this.id = id;
    }

    // public String getPictureWithPath() {
    // if (this.picture != null && !this.picture.isEmpty()) {
    // return "/uploads/" + this.picture;
    // } else {
    // return null;
    // }
    // }

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
