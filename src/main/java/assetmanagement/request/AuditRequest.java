package assetmanagement.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditRequest {
    private String id;
    private String remark;
    private String status;
    private Boolean withCondition;
//    private double latPre;
//    private double longPre;
//    private double latCur;
//    private double longCur;
}
