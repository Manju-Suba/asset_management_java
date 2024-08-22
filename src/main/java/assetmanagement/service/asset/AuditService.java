package assetmanagement.service.asset;

import assetmanagement.dto.AuditDto;
import assetmanagement.model.Asset;
import assetmanagement.model.audit.Audit;
import assetmanagement.request.AuditRequest;
import assetmanagement.request.RequestWithFilter;
import assetmanagement.response.AuditCompletedResponse;
import assetmanagement.response.AuditHistoryResponse;
import assetmanagement.response.AuditResponse;
import assetmanagement.response.AuditorResponse;
import assetmanagement.response.PendingAuditAssetResponse;
import assetmanagement.response.RequestAuditAssetResponse;
import freemarker.template.TemplateException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface AuditService {

    List<AuditDto> getAll(String status);

    Audit getById(String id);

    Audit updateStatus(String status, String id, String userId);

    List<Audit> getFilter(String assetClass, String assetType, String status);

    PendingAuditAssetResponse auditorFetch(RequestWithFilter requestWithFilter, Integer page, Integer size);

    List<AuditResponse> auditorFetchById(String id);

    Asset uploadPreImage(MultipartFile file, String id) throws IOException;

    Audit createAudit(MultipartFile file, AuditRequest auditRequest) throws IOException, TemplateException;

    AuditorResponse assetAuditByStatus(RequestWithFilter requestWithFilter);

    PendingAuditAssetResponse getPendingAuditAsset(RequestWithFilter requestWithFilter,int page, int size, boolean search, String value);

    List<Asset> saveAuditDate(String assetId, LocalDate auditDate);

    RequestAuditAssetResponse getRequestAuditAsset(Integer page, Integer size, String assetClass,boolean search, String value);

    List<AuditResponse> auditorFetchByAssetId(String assetId);

    List<Audit> allAuditDataByAssetId(String assetId);

    public long getApprovedRejectCount(RequestWithFilter requestWithFilter, boolean search, String value); 

    RequestAuditAssetResponse getAuditCompletedAsset(RequestWithFilter requestWithFilter,int page, int size, boolean search, String value);

    List<AuditHistoryResponse> getAuditCompletedHistory(String assetId);

    AuditCompletedResponse getCompletedAudit(RequestWithFilter requestWithFilter);
 
}
