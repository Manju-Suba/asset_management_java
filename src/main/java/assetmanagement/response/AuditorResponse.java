package assetmanagement.response;

import java.util.List;
import assetmanagement.model.audit.Audit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditorResponse {

    private long auditStatusCounts;   
    private List<AuditorResponseDTO> auditsData;  
    
}
