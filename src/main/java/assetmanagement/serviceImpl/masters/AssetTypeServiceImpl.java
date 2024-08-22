package assetmanagement.serviceImpl.masters;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import assetmanagement.enumData.ActiveInActive;
import assetmanagement.model.masters.AssetType;
import assetmanagement.repository.masters.AssetTypeRepository;
import assetmanagement.service.masters.AssetTypeService;
import assetmanagement.util.AuthUser;

@Service
public class AssetTypeServiceImpl implements AssetTypeService{

    @Autowired
    private AssetTypeRepository assetTypeRepository;

    @Override
    public AssetType createAssetType(AssetType assetType) {
        if(assetTypeRepository.existsByNameIgnoreCaseAndCompanyIdAndStatus(assetType.getName().trim(),AuthUser.getCompanyId(),ActiveInActive.ACTIVE.getValue())){
            throw new IllegalArgumentException("Asset Type already exist ");
        }
        assetType.setCompanyId(AuthUser.getCompanyId());
        return assetTypeRepository.save(assetType);
    }

    
    @Override
    public List<AssetType> getAllAssetType() {
        List<AssetType> assettype = assetTypeRepository.findAllByCompanyIdAndPlantAndStatus(AuthUser.getCompanyId(),AuthUser.getPlant(),ActiveInActive.ACTIVE.getValue());
        return assettype;
    }

    @Override
    public AssetType getAssetTypeById(String id) {
       Optional<AssetType> optionaluser = assetTypeRepository.findByIdAndStatus(id,ActiveInActive.ACTIVE.getValue());
       return optionaluser.get();
    }

    @Override
    public AssetType updateAssetType(AssetType assetType) {
        if(assetTypeRepository.existsByNameIgnoreCaseAndCompanyIdAndStatusAndIdNot(assetType.getName().trim(),AuthUser.getCompanyId(),ActiveInActive.ACTIVE.getValue(),assetType.getId())){
            throw new IllegalArgumentException("Asset type already exists ");
        }
        AssetType existingAssetType = assetTypeRepository.findById(assetType.getId())
        .orElseThrow(() -> new IllegalArgumentException("Asset type not found"));
        existingAssetType.setName(assetType.getName());
        existingAssetType.setAssetCategory(assetType.getAssetCategory());
        existingAssetType.setFieldId(assetType.getFieldId());
        existingAssetType.setDescription(assetType.getDescription());
            return assetTypeRepository.save(existingAssetType);
 
        }

    @Override
    public AssetType deleteAssetType(String id) {
        Optional<AssetType> AssetTypeid = assetTypeRepository.findByIdAndStatus(id,ActiveInActive.ACTIVE.getValue());
        if(AssetTypeid.isPresent()){
            AssetType AssetTypedelete= AssetTypeid.get();
            AssetTypedelete.setStatus(ActiveInActive.INACTIVE.getValue());
            return assetTypeRepository.save(AssetTypedelete);
    
        }
        return null;
    }

}
