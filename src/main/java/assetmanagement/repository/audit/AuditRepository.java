package assetmanagement.repository.audit;

import assetmanagement.dto.AuditDto;
import assetmanagement.model.audit.Audit;
import assetmanagement.response.AuditHistoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AuditRepository extends MongoRepository<Audit, String> {

    List<Audit> findByPlant(String plant);

    List<Audit> findByAssetId(String id);

    Long countByAssetId(String id);

    Long countByAssetIdAndStatus(String id, String status);

    List<Audit> findByAssetId(String id, Sort sortById);

    List<Audit> findByAssetIdAndStatus(String id, String status);

    List<AuditHistoryResponse> findAllByAssetIdAndStatus(String id, String status);

    List<Audit> findByAssetIdOrderByAuditDateDesc(String id, Pageable pageable);

    List<Audit> findByAssetIdOrderByNextAuditDateDesc(String id, Pageable pageable);

    List<Audit> findByStatusNot(String value);

    Audit findByStatusAndId(String value, String id);

    List<AuditDto> findByStatus(String value);

    Page<AuditDto> findByStatus(String value, Pageable pageable);

    long countByStatus(String status);

    // List<AuditDto> findByAssetIdCompanyIdAndAssetIdPlantAndStatus(String
    // companyId, String plant, String status);

    List<Audit> findByAssetIdAndStatusOrderByAuditDateDesc(String id, String status, Pageable pageable);

    List<Audit> findByAssetIdAndStatus(String assetId, String status, Sort sortById);

}
