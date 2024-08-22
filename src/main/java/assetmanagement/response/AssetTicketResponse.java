package assetmanagement.response;

import java.util.List;

import assetmanagement.model.AssetTicket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetTicketResponse {
    private long assetTicketCount;
    private List<AssetTicket> assetTicket;
}
