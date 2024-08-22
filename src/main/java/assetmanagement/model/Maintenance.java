package assetmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "maintenance")
public class Maintenance {
    @Id
    private String id;
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
    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String updatedBy;
    @CreatedDate
    private LocalDate createdAt;
    @LastModifiedDate
    private LocalDate updatedAt;

}
