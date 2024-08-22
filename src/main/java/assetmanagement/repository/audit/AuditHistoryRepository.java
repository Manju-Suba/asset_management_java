package assetmanagement.repository.audit;

import assetmanagement.model.audit.AuditHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditHistoryRepository extends MongoRepository<AuditHistory,String> {
}
