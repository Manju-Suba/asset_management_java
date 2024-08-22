package assetmanagement.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetTicketRequest {
    private String ticketNo;
    private String assetClass;
    private String assetName;
    private String description;
}
