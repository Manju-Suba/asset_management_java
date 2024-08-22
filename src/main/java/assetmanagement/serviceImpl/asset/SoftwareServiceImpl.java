package assetmanagement.serviceImpl.asset;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import assetmanagement.enumData.ActiveInActive;
import assetmanagement.exception.ResourceNotFoundException;
import assetmanagement.model.Asset;
import assetmanagement.model.SoftwareHistory;
import assetmanagement.model.masters.AssetCategory;
import assetmanagement.repository.SoftwareHistoryRepository;
import assetmanagement.repository.asset.AssetRepository;
import assetmanagement.repository.masters.AssetCategoryRepository;
import assetmanagement.repository.masters.AssetTypeRepository;
import assetmanagement.service.asset.SoftwareService;
import assetmanagement.util.AuthUser;
import assetmanagement.util.Format;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SoftwareServiceImpl implements SoftwareService{

    public final AssetRepository assetRepository;
    public final AssetTypeRepository assetTypeRepository;
    public final AssetCategoryRepository assetCategoryRepository;
    public final SoftwareHistoryRepository softwareHistoryRepository;
    
    @Value("${upload.path}")
    private String fileBasePath;

    @Override
    public List<Asset> getSoftwareData(String type) {
        if(type == null || type.isEmpty()){
            throw new ResourceNotFoundException("type must not be empty");
        } 
        LocalDate currentDate = LocalDate.now();
        LocalDate afteroneMonth = currentDate.plusMonths(2);

        String name = "Software";
        Optional<AssetCategory> assetCategoryId = assetCategoryRepository.findByNameContainingIgnoreCaseAndCompanyIdAndStatus(name,AuthUser.getCompanyId(),ActiveInActive.ACTIVE.getValue());
        if(assetCategoryId.isPresent()){
            if(type.equals("all")){
                return assetRepository.findByCompanyIdAndStatusAndAssetCategory(AuthUser.getCompanyId(),ActiveInActive.ACTIVE.getValue(),assetCategoryId.get().getId());
            }else if(type.equals("close")){
                return assetRepository.findByCompanyIdAndStatusAndAssetCategoryAndExpiryDateBetween(AuthUser.getCompanyId(),ActiveInActive.ACTIVE.getValue(),assetCategoryId.get().getId(),currentDate.minusDays(1),afteroneMonth);
            }else if(type.equals("expired")){
                return assetRepository.findByCompanyIdAndStatusAndAssetCategoryAndExpiryDateBefore(AuthUser.getCompanyId(),ActiveInActive.ACTIVE.getValue(),assetCategoryId.get().getId(),currentDate);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public SoftwareHistory saveRenewal(SoftwareHistory softwareHistory, MultipartFile Image) throws IOException{
        
        
        String assetId = softwareHistory.getAssetReferenceId();
        if(assetId == null || assetId.isEmpty()){
            throw new ResourceNotFoundException("Asset Id must not be empty");
        } 
        Optional<Asset> asset = assetRepository.findByIdAndCompanyIdAndStatus(assetId,AuthUser.getCompanyId(),ActiveInActive.ACTIVE.getValue());

        if (asset.isPresent()) {
            Asset assetDetails = asset.get();
            LocalDate renewableDate = softwareHistory.getRenewableDate();
            if (renewableDate != null) {
                assetDetails.setExpiryDate(renewableDate);
                assetRepository.save(assetDetails);
            }
            if (Image != null && !Image.isEmpty()) {
                String originalName = Image.getOriginalFilename();
                String fileName = Format.formatDate() + "_" + originalName;
                Path path = Path.of(fileBasePath + fileName);
                Files.copy(Image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                softwareHistory.setPicture(fileName);
            }
            softwareHistory.setAssetReferenceId(assetId);
            return softwareHistoryRepository.save(softwareHistory);
        }
        throw new ResourceNotFoundException("asset not found");    
    }

    @Override
    public List<SoftwareHistory> getSoftwareHistoryByAssetId(String assetId) {
        
        return softwareHistoryRepository.findByAssetReferenceId(assetId);
    }

}
