package assetmanagement.repository.transfer;

import assetmanagement.model.transfer.TransferDetail;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface TransferDetailRepository extends MongoRepository<TransferDetail,String> {
    List<TransferDetail> findAllByStatus(String status);

    List<TransferDetail> findByAssetIdAndStatus(String assetId,String status);

    List<TransferDetail> findByAssetId(String assetId);

    
    //Get all data based on status with filter
    // List<TransferDetail> findByCompanyIdAndFromPlantAndStatusAndAssetClassAndAssetTypeAndAssetId(String companyId,String plant,String status,String assetClass,String assetType,String assetId,Sort sortByDescId);
    // List<TransferDetail> findByCompanyIdAndFromPlantAndStatusAndAssetClassAndAssetType(String companyId,String plant,String status,String assetClass,String assetType,Sort sortByDescId);
    // List<TransferDetail> findByCompanyIdAndFromPlantAndStatusAndAssetClass(String companyId,String plant,String status,String assetClass,Sort sortByDescId);
    // List<TransferDetail> findByCompanyIdAndFromPlantAndStatusAndAssetIdAndAssetType(String companyId,String plant,String status,String assetId,String assetType,Sort sortByDescId);
    // List<TransferDetail> findByCompanyIdAndFromPlantAndStatusAndAssetType(String companyId,String plant,String status,String assetType,Sort sortByDescId);
    // List<TransferDetail> findByCompanyIdAndFromPlantAndStatusAndAssetId(String companyId,String plant,String status,String assetId,Sort sortByDescId);
    List<TransferDetail> findByCompanyIdAndFromPlantAndStatus(String companyId,String plant,String status,Sort sortByDescId);
}
