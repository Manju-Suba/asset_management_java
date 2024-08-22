package assetmanagement.response;

import java.util.List;
import assetmanagement.model.SubClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubClassResponse {
    private long subClassCounts;   
    private List<SubClass> subClass;
}
