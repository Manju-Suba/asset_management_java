package assetmanagement.response;

import java.util.List;

import assetmanagement.model.Asset;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetAllocationResponse {

 private long assetsCount;   
 private List<Asset> assets;

}
