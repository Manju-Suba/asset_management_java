package assetmanagement.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RejectAsstCount {
    private long assetCounts;
    private List<RejectedAssets> assetrecord;

}
