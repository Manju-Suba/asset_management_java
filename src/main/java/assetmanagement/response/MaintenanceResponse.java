package assetmanagement.response;

import java.util.List;

import assetmanagement.model.Maintenance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaintenanceResponse {
    private Long maintenanceAssetCount;
    private List<Maintenance> maintenanceList;
}
