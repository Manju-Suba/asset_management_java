package assetmanagement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class SapMaintenance {
    @Id
    private String id;
    @JsonProperty("EquipmentNumber")
    private String assetId;
    @JsonProperty("Plant")
    private String plant;
    @JsonProperty("BreakdownDateTime")
    private String breakDownDate;
    @JsonProperty("BreakdownDuration")
    private String breakDownDurationTime;
    @JsonProperty("Problem")
    private String problem;
    @JsonProperty("MalfunctionStart")
    private String malfunctionStart;
    @JsonProperty("MalfunctionEnd")
    private String malfunctionEnd;
    // private String breakDownDateTime;
    // private String breakDownDuration;
    // private String malfunctionStartDate;
    // private String malfunctionEndDate;
    private LocalDateTime breakDownDateTime;
    private Float breakDownDuration;
    private LocalDateTime malfunctionStartDate;
    private LocalDateTime malfunctionEndDate;
    @JsonProperty("OrderNumber")
    private String orderNumber;
    @JsonProperty("ProductionLine")
    private String productionLine;
    @JsonProperty("EquipmentName")
    private String equipmentName;
    @JsonProperty("Category")
    private String category;
    @JsonProperty("Tag")
    private String tag;
    @JsonProperty("Priority")
    private String priority;
    @JsonProperty("ActionTaken")
    private String actionTaken;
    @JsonProperty("RootCause")
    private String rootCause;
    @JsonProperty("Status")
    private String status;
    @JsonProperty("MaintenanceType")
    private String maintenanceType;
    @JsonProperty("RequestedBy")
    private String requestedBy;
    @JsonProperty("ReportedBy")
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
