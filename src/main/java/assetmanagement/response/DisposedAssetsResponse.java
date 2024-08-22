package assetmanagement.response;

import java.util.List;

import assetmanagement.dto.AuditDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisposedAssetsResponse {

 private long DisposedCount;   
 private List<AuditDto> DisposedAssets;

}
