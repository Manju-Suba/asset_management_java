package assetmanagement.repository.reports;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import assetmanagement.model.AssetHistory;

public interface ReportRepository extends MongoRepository<AssetHistory,String>{
    List<AssetHistory> findByCompanyIdAndStatus(String companyId,String status,Sort sortByDescId);

}
