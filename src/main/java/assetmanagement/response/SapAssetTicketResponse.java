package assetmanagement.response;

import java.util.List;

import assetmanagement.model.AssetTicket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SapAssetTicketResponse {
    private long assetTicketCount;
    private List<SapTicketResponse> sapAssetTicket;
}
