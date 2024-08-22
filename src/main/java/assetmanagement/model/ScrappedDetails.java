package assetmanagement.model;

import java.time.LocalDateTime;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ScrappedDetails{

@Id
private String id;
@Indexed(sparse = true)
private String assetId;
@Indexed(sparse = true)
private ObjectId objectId;
private String assetClass;
private String childId;
@Indexed(sparse = true)
private String status; 
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
