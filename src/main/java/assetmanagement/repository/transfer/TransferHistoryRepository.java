package assetmanagement.repository.transfer;

import assetmanagement.model.transfer.TransferHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransferHistoryRepository extends MongoRepository<TransferHistory,String> {
}
