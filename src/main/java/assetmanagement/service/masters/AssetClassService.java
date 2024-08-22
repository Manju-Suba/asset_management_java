package assetmanagement.service.masters;

import java.util.List;

import assetmanagement.model.masters.AssetClass;
import assetmanagement.response.AssetClassResponse;
import assetmanagement.response.PlantResponse;

public interface AssetClassService {

    AssetClassResponse getAllAssetClass(Integer page, Integer size);

    List<AssetClass> getAllAssetClass();

    PlantResponse getAllPlant(Integer page, Integer size);

    List<AssetClass> getCheckListAssetClass();
}
