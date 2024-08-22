package assetmanagement.model.transfer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transfer_details")
public class TransferDetail {
    
    @Id
    private String id;
    private String companyId;
    private String assetReferenceId;
    private String assetId;
    private String assetClass;
    private String childId; 
    private String fromPlant;
    private String remarks;
    private String toPlant;
    private LocalDate requestRaisedDate;
    private LocalDate actionDate;
    private String status;
    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String updatedBy;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public TransferDetail(String id){
        this.id = id;
    }
}
