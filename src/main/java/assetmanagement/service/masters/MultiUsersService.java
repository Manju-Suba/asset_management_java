package assetmanagement.service.masters;

import assetmanagement.model.ScrappedDetails;
import assetmanagement.model.transfer.TransferDetail;
import assetmanagement.request.RequestWithFilter;
import assetmanagement.request.ScrapRequest;
import assetmanagement.response.AssetListResponse;
import assetmanagement.response.MultiUsersTransferPendingResponse;
import assetmanagement.response.ScrapPendingResponse;

public interface MultiUsersService {

    AssetListResponse getAllAsset(String assetClass, String assetStatus, String assetId, String childId, Boolean search, String value, Integer page, Integer size);

    ScrapPendingResponse getAllScrapped(RequestWithFilter requestWithFilter, Boolean search, String value, Integer page, Integer size);

    MultiUsersTransferPendingResponse getAllTransferred(RequestWithFilter requestWithFilter, Boolean search, String value, Integer page, Integer size);

    ScrappedDetails createScrapRequest(ScrapRequest scrapRequest);

    TransferDetail create(TransferDetail transferDetail);

    long getScrappedCount(RequestWithFilter requestWithFilter, Boolean search, String value);

    long getTransferredCount(RequestWithFilter requestWithFilter, Boolean search, String value);

    TransferDetail bulkAssetUpdateStatus(String id, String assetId, String status);

    ScrappedDetails bulkScrapUpdateStatus(String assetId, String status);


}
