package assetmanagement.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetDisposedResponse {

    private long assetDisposedCount;

    private List<DisposedAsset> assetDisposed;

}
