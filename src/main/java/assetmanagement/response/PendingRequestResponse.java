package assetmanagement.response;

import java.util.List;
import assetmanagement.model.audit.Audit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingRequestResponse {

 private long PendingRequestCounts;   
 private List<Audit> assets;
}
