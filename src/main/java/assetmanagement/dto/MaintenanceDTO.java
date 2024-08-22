package assetmanagement.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceDTO {
    private String assetId;
    private String plant;
    private String equipmentNumber;
    // private String breakDownDateTime;
    // private String breakDownDuration;
    // private String problem;
    // private String malfunctionStartDate;
    // private String malfunctionEndDate;
        private LocalDateTime breakDownDateTime;
    private Float breakDownDuration;
    private String problem;
    private LocalDateTime malfunctionStartDate;
    private LocalDateTime malfunctionEndDate;
    private String orderNumber;
    private String productionLine;
    private String equipmentName;
    private String category;
    private String tag;
    private String priority;
    private String actionTaken;
    private String rootCause;
    private String status;
    private String maintenanceType;
    private String requestedBy;
    private String reportedBy;
}
