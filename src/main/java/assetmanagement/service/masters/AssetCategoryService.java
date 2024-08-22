package assetmanagement.service.masters;


import java.util.List;

import org.springframework.stereotype.Service;

import assetmanagement.model.masters.AssetCategory;

@Service
public interface AssetCategoryService {

    AssetCategory createAssetCategory(AssetCategory assetCategory);
    AssetCategory getbyIdAssetCategory(String id);
    List<AssetCategory> getallAssetCategory();
    AssetCategory updateAssetCategory(AssetCategory assetCategory);
    AssetCategory deleteAssetCategory(String id);

}
