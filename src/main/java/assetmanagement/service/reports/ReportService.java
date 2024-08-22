package assetmanagement.service.reports;

import java.util.List;
import org.springframework.stereotype.Service;
import assetmanagement.response.ActivityReportDTO;
import assetmanagement.response.AssetResponse;

@Service
public interface ReportService {

    List<ActivityReportDTO> getAllActivityReport();

    List<AssetResponse> assetByLocation(String locationId);

    List<AssetResponse> assetAssetByType(String typeId);

}
