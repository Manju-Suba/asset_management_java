package assetmanagement.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor 
@Document(collection = "sub_class")
public class SubClass {
  @Id
  private String id;
  private String assetClass;
  private String assetId;
  private String childId; 
  private String companyId;
  private String plant;
}
