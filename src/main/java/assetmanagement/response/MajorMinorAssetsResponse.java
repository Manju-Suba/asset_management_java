package assetmanagement.response;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MajorMinorAssetsResponse {
    private long majorMinorAssetsCounts;   
    private List<MajorMinorAssetsDTO> majorMinorList; 
}
