package assetmanagement.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityReportDTO {

    private String assetId;
    private LocalDate allocateDate;
    private LocalDate getBackDate;
    private LocalDate retrialDate;
    private String reason;
    private String name;
    private String employeeName;
    private String employeeId;
    private String location;
}
