package assetmanagement.serviceImpl.masters;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import assetmanagement.enumData.ActiveInActive;
import assetmanagement.model.masters.AssetCategory;
import assetmanagement.repository.masters.AssetCategoryRepository;
import assetmanagement.service.masters.AssetCategoryService;
import assetmanagement.util.AuthUser;

@Service
public class AssetCategoryServiceImpl implements AssetCategoryService{

    @Autowired
    AssetCategoryRepository assetCategoryRepository;

    @Override
    public AssetCategory createAssetCategory(AssetCategory assetCategory) {
        if(assetCategoryRepository.existsByNameIgnoreCaseAndCompanyIdAndStatus(assetCategory.getName().trim(),AuthUser.getCompanyId(),ActiveInActive.ACTIVE.getValue())){
            throw new IllegalArgumentException("Asset Category already exist ");
        }
        assetCategory.setCompanyId(AuthUser.getCompanyId());
        return assetCategoryRepository.save(assetCategory);
    }

    @Override
    public AssetCategory getbyIdAssetCategory(String id) {
        Optional<AssetCategory> optionalUser = assetCategoryRepository.findByIdAndStatus(id,ActiveInActive.ACTIVE.getValue());
        return optionalUser.get();
    }

    @Override
    public List<AssetCategory> getallAssetCategory() {
        List<AssetCategory> assetCategorys=assetCategoryRepository.findAllByCompanyIdAndPlantAndStatus(AuthUser.getCompanyId(),AuthUser.getPlant(),ActiveInActive.ACTIVE.getValue());
        return assetCategorys;
    }

    @Override
    public AssetCategory updateAssetCategory(AssetCategory assetCategory) {
        if(assetCategoryRepository.existsByNameIgnoreCaseAndCompanyIdAndStatusAndIdNot(assetCategory.getName().trim(),AuthUser.getCompanyId(),ActiveInActive.ACTIVE.getValue(),assetCategory.getId())){
            throw new IllegalArgumentException("Asset Category Not Updated ");
        }
        AssetCategory existingAssetCategory = assetCategoryRepository.findById(assetCategory.getId())
        .orElseThrow(() -> new IllegalArgumentException("Asset Category Not Found"));
        existingAssetCategory.setName(assetCategory.getName());
        existingAssetCategory.setBusinessId(assetCategory.getBusinessId());
        existingAssetCategory.setDescription(assetCategory.getDescription());
            return assetCategoryRepository.save(existingAssetCategory);
        }
    
    @Override
    public AssetCategory deleteAssetCategory(String id) {
        Optional<AssetCategory> assetCategoryOptional=assetCategoryRepository.findByIdAndStatus(id,ActiveInActive.ACTIVE.getValue());

        if(assetCategoryOptional.isPresent()){
            AssetCategory assetCategorydelete= assetCategoryOptional.get();
            assetCategorydelete.setStatus(ActiveInActive.INACTIVE.getValue());
            return assetCategoryRepository.save(assetCategorydelete);
        }
        return null;
    }

}

