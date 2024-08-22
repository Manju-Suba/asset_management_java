package assetmanagement.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "sap_details")
public class SapDetails {
    @Id
    private String id;
    private String assetId;
    private String costClassWise;
    private String latitudeAndLongitude;
    private String geo;
    private String assetClass;
    private String plant;
    private String serialNumber;
    private String assetStatus;
    private String assetAgeing;
    private String assetLifetime;
    private String requiresAttention;
    private String upOrDowntime;
    private String warrantyStatus;
    private String noOfRoutinesExecuted;
    private String costBasedMajorAsset;
    private String costBasedMinorAsset;
    private String costOfAsset;
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
    // private String standardDep;
}
