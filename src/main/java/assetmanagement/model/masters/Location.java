package assetmanagement.model.masters;

import java.time.LocalDateTime;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "locations")
public class Location {
 
    @Id
    private String id;
    @NotNull(message = "field is mandatory")
    @NotEmpty(message = "must not be empty")
    private String name;
    private String companyId;
    @NotNull(message = "field is mandatory")
    private String description; 
    private  String status = "Active";
    @CreatedBy
    private String createdby;
    @LastModifiedBy
    private String updatedBy;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Location(String id) {
        this.id = id;
    }
}
