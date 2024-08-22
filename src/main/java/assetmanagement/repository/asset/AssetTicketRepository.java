package assetmanagement.repository.asset;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import assetmanagement.model.AssetTicket;

public interface AssetTicketRepository extends MongoRepository<AssetTicket, String>  {

    Optional<AssetTicket> findByIdAndPlant(String id, String plant);

    Optional<AssetTicket> findByTicketNo(String ticketNo);
    
}
