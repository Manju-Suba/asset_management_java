package assetmanagement.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendingAuditAssetResponse {
    private long pendingAuditAssetCounts;
    private List<AuditResponse> auditResponses;
}
