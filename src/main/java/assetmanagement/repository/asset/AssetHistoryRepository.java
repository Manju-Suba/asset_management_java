package assetmanagement.repository.asset;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import assetmanagement.model.AssetHistory;

public interface AssetHistoryRepository extends MongoRepository<AssetHistory, String>{

    List<AssetHistory> findByCompanyIdAndStatus(String companyId,String status);

    List<AssetHistory> findByAssetId(String assetId);
   
    @Query(value = "{}", fields = "{ '_id': 0, 'replaceAssetId' : 1}")
    List<String> findAllReplaceAssetId();

}
