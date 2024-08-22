package assetmanagement.response;

import java.util.List;

import assetmanagement.model.ScrappedDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScrapPendingResponse {

 private long PendingRequestCounts;   
 private List<ScrappedDetailsDTO> WaitingAssets;

}
