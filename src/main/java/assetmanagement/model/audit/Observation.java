package assetmanagement.model.audit;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "observation")
public class Observation {

    @Id
    private String id;
    @NotBlank(message = "Asset Id must not be blank")
    private String assetId;
    @NotNull(message = "observation date must not be null") 
    private LocalDate observationDate;
    @NotBlank(message = "Remarks must not be blank")
    @Size(min = 1, max = 1000, message = "Remarks must be between 1 and 1000 characters")
    private String remarks;
    private String companyId;
    private String plant;
    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String updatedBy;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

}
