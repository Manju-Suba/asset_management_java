package assetmanagement.service.asset;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import assetmanagement.model.Asset;
import assetmanagement.model.SoftwareHistory;

public interface SoftwareService {

    List<Asset> getSoftwareData(String type);

    SoftwareHistory saveRenewal(SoftwareHistory softwareHistory,MultipartFile picture) throws IOException;

    List<SoftwareHistory> getSoftwareHistoryByAssetId(String assetId);
}
  