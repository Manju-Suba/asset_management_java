package assetmanagement.serviceImpl.asset;

import assetmanagement.enumData.ActiveInActive;
import assetmanagement.enumData.AvailableStatus;
import assetmanagement.enumData.YesOrNo;
import assetmanagement.model.Asset;
import assetmanagement.model.AssetHistory;
import assetmanagement.repository.asset.AssetHistoryRepository;
import assetmanagement.repository.asset.AssetRepository;
import assetmanagement.service.asset.AssetHistoryService;
import assetmanagement.util.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssetHistoryImpl implements AssetHistoryService {

    private final AssetHistoryRepository assetHistoryRepository;
    private final AssetRepository assetRepository;


    @Override
    public AssetHistory create(AssetHistory assetHistory) {
        AssetHistory savedRecord = assetHistoryRepository.save(assetHistory);
        updateAssetBasedOnHistory(assetHistory);
        return savedRecord;
    }

    private void updateAssetBasedOnHistory(AssetHistory assetHistory) {
        Optional<Asset> updatedAsset = assetRepository.findByIdAndCompanyIdAndStatus(
                assetHistory.getAssetId().getId(), AuthUser.getCompanyId(), ActiveInActive.ACTIVE.getValue()
        );

        if (updatedAsset.isPresent()) {
            Asset assetToUpdate = updatedAsset.get();
            String type = assetHistory.getType();
            switch (type) {
                case "Allocate":
                    updateAssetStatus(assetToUpdate, AvailableStatus.Allocate.getValue());
                    break;
                case "Get Back":
                    updateAssetStatus(assetToUpdate, AvailableStatus.Stock.getValue());
                    break;
                case "Retrial":
                    handleRetrial(assetHistory, assetToUpdate);
                    break;
                default:
                    handleUnknownType(type);
                    break;
            }
            assetRepository.save(assetToUpdate);
        }
    }

    private void updateAssetStatus(Asset asset, String status) {
        asset.setAvailableStatus(status);
    }

    private void handleRetrial(AssetHistory assetHistory, Asset assetToUpdate) {
        if (assetHistory.getRetrialType().equals("Replacement")) {
            Optional<Asset> replacementAsset = assetRepository.findByIdAndCompanyIdAndStatus(
                    assetHistory.getReplaceAssetId(), AuthUser.getCompanyId(), ActiveInActive.ACTIVE.getValue()
            );

            if (replacementAsset.isPresent()) {
                Asset asset = replacementAsset.get();
                if (asset.getAssetAllocate().equals(YesOrNo.Yes.getValue())) {
                    asset.setAssetAllocate(YesOrNo.Yes.getValue());
                    asset.setEmployee(assetToUpdate.getEmployee());
                    asset.setAvailableStatus(AvailableStatus.Allocate.getValue());
                    assetRepository.save(asset);
                }
            }
            updateAssetStatus(assetToUpdate, AvailableStatus.Retrial.getValue());
        }
    }

    private void handleUnknownType(String type) {
        throw new IllegalArgumentException("Unknown asset history type: " + type);
    }
}
