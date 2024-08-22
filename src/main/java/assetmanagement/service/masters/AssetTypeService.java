package assetmanagement.service.masters;

import java.util.List;
import org.springframework.stereotype.Service;

import assetmanagement.model.masters.AssetType;


@Service
public interface AssetTypeService {

    AssetType createAssetType(AssetType assetType);
      
    AssetType getAssetTypeById(String id);
   
    List<AssetType> getAllAssetType();
    
    AssetType updateAssetType(AssetType assetType);
   
    AssetType deleteAssetType(String id);

}
