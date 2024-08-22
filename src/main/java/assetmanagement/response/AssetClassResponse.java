package assetmanagement.response;

import java.util.List;
import assetmanagement.model.masters.AssetClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetClassResponse {

private long assetClassCount;
private List<AssetClass> assetClasses;


}
