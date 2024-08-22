package assetmanagement.model.disposed;

import assetmanagement.model.Asset;
import assetmanagement.model.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "disposed_details")
public class DisposedDetail {
    @Id
    private String id;
    @DBRef
    private Asset assetId;
    @DBRef
    private Users requestRaisedBy;
    @DBRef
    private Users actionBy;
    private String reason;
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

    public DisposedDetail(String id){
        this.id=id;
    }

}
