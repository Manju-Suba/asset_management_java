package assetmanagement.repository.disposed;

import assetmanagement.model.disposed.DisposedHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DisposedHistoryRepository extends MongoRepository<DisposedHistory,String> {
}
