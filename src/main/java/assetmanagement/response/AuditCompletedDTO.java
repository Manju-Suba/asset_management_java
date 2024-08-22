package assetmanagement.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditCompletedDTO {
    private String id;
    private String assetId;
    private String assetClass;
    private String plant;
    private LocalDate auditDate;
    private String auditDateFormat;
    private LocalDate nextAuditDate;
    private Long statusCount;
    private String status;
    private String remark;

}
