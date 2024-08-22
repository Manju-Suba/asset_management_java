package assetmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RejectedAssets {
    private String assetId;
    private String assetClass;
    private String childId;
    private Long rejectedCount;

}
