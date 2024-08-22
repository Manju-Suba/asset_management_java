package assetmanagement.service.asset;

import assetmanagement.dto.AssetTicketDTO;
import assetmanagement.model.AssetTicket;
import assetmanagement.request.AssetTicketRequest;
import assetmanagement.response.AssetTicketResponse;
import assetmanagement.response.SapAssetTicketResponse;
import assetmanagement.response.TicketNoResponse;

import java.util.List;
import java.util.Optional;

public interface AssetTicketService {

    AssetTicketResponse fetchAllTicket(String assetClass, String ticketNo, Boolean search, String value, Integer page,
                                       Integer size);

    AssetTicketResponse fetchStatusWiseTicket(String assetClass, String ticketNo, boolean search, String value, Integer page,
                                       Integer size, String ticketType);

    Optional<AssetTicket> getAssetById(String id);

    AssetTicket update(String id, AssetTicketRequest assetTicket);
    AssetTicket sapUpdate(String ticketNo, String sapStatus);

    List<AssetTicket> getTicketNo();

    List<AssetTicketDTO> assetCreationFilterData(String type);

    AssetTicket create(AssetTicketRequest assetTicket);

    Optional<AssetTicket> deleteAssetTicket(String id);

    AssetTicket statusUpdate(String id, String status);

    SapAssetTicketResponse sapRequestTicket();

    TicketNoResponse getTicketNoCount();
}
