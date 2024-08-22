package assetmanagement.service.asset;

import assetmanagement.dto.AssetIdDTO;
import assetmanagement.model.Asset;
import assetmanagement.model.masters.AssetType;
import assetmanagement.response.AssetAllocationResponse;
import assetmanagement.response.AssetListResponse;
import assetmanagement.response.ChildIdDTO;
import assetmanagement.response.SapResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface AssetService {

    Asset create(Asset asset);

    SapResponse fetchDataAndInsert();

    AssetListResponse getAllAsset(String availableStatus, String assetClass, String assetStatus, String assetId,
                                  String childId, Boolean search, String value, Integer page, Integer size);

    List<Asset> getAllAsset(String availableStatus, String assetClass, String assetStatus, String childId);

    List<Asset> getAllByAssetStatus(String assetStatus);

    Asset getByAssetByAssetId(String assetId);

    Optional<Asset> getAssetById(String id, String company, String plant);

    Asset update(Asset asset, MultipartFile imageUpload, MultipartFile documentUpload) throws IOException;

    Asset delete(String id);

    List<AssetIdDTO> getNotReplacedAssetID();

    List<AssetType> getAssetBasedOnCategory(String id);

    AssetAllocationResponse getAssetAllocation(String assetCategoryId, String assetStatus, String availableStatus,
                                               String allocateType, String subClass, Integer page, Integer size);

    List<Asset> getByClass(String assetClass, String assetStatus, String assetId);

    Optional<Asset> getAssetByAssetIdToAudit(String assetId, String company, String plant, String assetClass);

    List<ChildIdDTO> getChildIdByAssetId(String assetId, String plant, String companyId);

    SapResponse fetchDataAndUpdate();
}
