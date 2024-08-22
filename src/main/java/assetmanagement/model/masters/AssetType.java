package assetmanagement.model.masters;

import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "asset_type")
public class AssetType {

    @Id
    private String id;
    private String fieldId;
    private String companyId;
    private String plant;
    @NotNull(message = "field is mandatory")
    // @NotEmpty(message = "must not be empty")
    @DBRef
    private AssetCategory assetCategory;
    @NotNull(message = "field is mandatory")
    @NotEmpty(message = "must not be empty")
    private String name;
    @Field
    private String description;
    private  String status ="Active";
    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String updatedBy;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
