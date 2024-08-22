package assetmanagement.response;

import java.util.List;

import assetmanagement.model.Asset;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetListResponse {

 private long assetsCount;   
 private List<Asset> assets;


}
