package assetmanagement.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestWithFilter {
    private String id;
    private String assetClass;
    private String assetId;
    private String assetStatus;
    //private String assetCategoryId; // remove this field in upcoming days
    private String assetTypeId;
    private String assetNo;
    private String subClass;
    private String childId;
    private String status;
    private Boolean search;
    private String value;
    private Integer page;
    private Integer size; 
    private String env;
}
