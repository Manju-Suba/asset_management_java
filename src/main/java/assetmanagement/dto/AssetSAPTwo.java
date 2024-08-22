package assetmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetSAPTwo {
    @JsonProperty("ASSET NUMBER")
    private String assetId;
    @JsonProperty("SUBASSET NUMBER")
    private String childId;
    @JsonProperty("ASSET CLASS")
    private String assetClass;
    @JsonProperty("ASSET DESCRIPTION")
    private String description;
    @JsonProperty("ACQUIS.VAL.")
    private Long costOfAsset;
    @JsonProperty("ACCUM.DEP.")
    private Long accumDep;
    @JsonProperty("BOOK VAL.")
    private String estimatedSalvageValue;

}
