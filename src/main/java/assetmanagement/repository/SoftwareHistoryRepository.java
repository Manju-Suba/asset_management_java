package assetmanagement.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import assetmanagement.model.SoftwareHistory;

public interface SoftwareHistoryRepository extends MongoRepository<SoftwareHistory, String>{

	List<SoftwareHistory> findByAssetReferenceId(String assetId);
    
}
