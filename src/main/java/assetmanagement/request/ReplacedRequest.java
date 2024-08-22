package assetmanagement.request;

import java.time.LocalDate;
import assetmanagement.model.Asset;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplacedRequest {

    private Asset assetId;
    private String status;
    private String assetNo;
    private String replaceAssetId; 
    private LocalDate expiryDate;
    private String cost;

}
