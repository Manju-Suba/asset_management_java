package assetmanagement.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import assetmanagement.model.Asset;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RenewedRequest {

    private Asset assetId;
    private String assetNo;
    private String status;
    private LocalDate nextRenewedDate;
    private LocalDate expiryDate;
    private String cost;
}
