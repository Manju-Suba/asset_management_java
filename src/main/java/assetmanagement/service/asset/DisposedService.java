package assetmanagement.service.asset;

import assetmanagement.model.Asset;
import assetmanagement.model.audit.Audit;
import assetmanagement.model.disposed.DisposedDetail;
import assetmanagement.request.RenewedRequest;
import assetmanagement.request.ReplacedRequest;
import assetmanagement.request.RequestWithFilter;
import assetmanagement.response.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DisposedService {
    DisposedDetail create(DisposedDetail disposedDetail) throws IOException;

    DisposedDetail getById(String id);

    List<DisposedDetail> getAll(String status);

    DisposedDetail updateStatus(String id, String status, String reason, String userId) throws IOException;

    CloseToDisposedResponse closedDisposed(RequestWithFilter requestWithFilter, boolean search, String value, Integer page, Integer size);

    PendingRequestResponse pendingRequest(RequestWithFilter requestWithFilter, Integer page, Integer size);

    Long countPendingRequests(); // To show rejected counts in dashboard

    Audit approvedRequest(String id);

    Audit rejectedRequest(String id);

    AssetDisposedResponse fetchData(RequestWithFilter requestWithFilter, Integer page, Integer size, boolean search, String value);

    Asset actionReplace(ReplacedRequest replacedRequest, MultipartFile file) throws IOException;

    Asset actionRenew(RenewedRequest renewedRequest, MultipartFile file) throws IOException;

    RenewedResponse getAllRenewed(RequestWithFilter requestWithFilter, boolean search, String value, Integer page, Integer size);

    ReplacedResponse fetchByStatus(String status, RequestWithFilter requestWithFilter, boolean search, String value, Integer page, Integer size);

    Long countRejectedRequests();
}
