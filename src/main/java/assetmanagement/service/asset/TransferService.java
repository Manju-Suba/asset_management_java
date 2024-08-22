package assetmanagement.service.asset;

import assetmanagement.model.transfer.TransferDetail;
import assetmanagement.response.TransferPendingResponse;

import java.util.List;

public interface TransferService {
    TransferDetail create(TransferDetail transferDetail);

    List<TransferDetail> getAll(String status);

    List<TransferDetail> getAllTransferDataByAssetid(String assetId);

    TransferDetail updateStatus(String status, String id);

    TransferDetail bulkAssetUpdateStatus(String status, List<String> ids);

    TransferDetail getById(String id);

    TransferPendingResponse getDataWithFilter(String assetClass, String assetType, String assetId, String status,
            Integer page, Integer size, String subClass,String childId);

    // List<TransferDetailDto> getDataForFilter(String assetClass, String assetType,
    // String assetId, String status);

}
