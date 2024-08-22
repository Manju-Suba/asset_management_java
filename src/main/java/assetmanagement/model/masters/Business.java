package assetmanagement.model.masters;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "business")
public class Business {
    
    @Id
    private String id;
    @NotNull(message = "field is mandatory")
    @NotEmpty(message = "must not be empty")
    private String name;
    private String companyId;
    @NotNull(message = "field is mandatory")
    @NotEmpty(message = "must not be empty")
    private String description;
    private String status = "Active";
    @CreatedBy
    @Field("created_by")
    private String createdBy;
    @LastModifiedBy
    private String updatedBy;
    @Field("created_at")
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;

    public Business(String id) {
        this.id = id;
    }
}
