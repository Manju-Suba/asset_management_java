package assetmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Sort;
import assetmanagement.model.ScrappedDetails;
import org.bson.types.ObjectId;


public interface ScrappedDetailsRepository extends MongoRepository<ScrappedDetails,String>{

    List<ScrappedDetails> findByAssetId(String assetiId, Sort sortById);

    ScrappedDetails findFirstByAssetIdOrderByCreatedAtDesc(String assetId);

    List<ScrappedDetails>  findByAssetIdInAndStatus(List<String> assetId, String status);

    List<ScrappedDetails> findByObjectId(ObjectId objectId);

    Optional<ScrappedDetails> findById(ObjectId objectId);

    List<ScrappedDetails> findByAssetIdInAndStatusOrderByCreatedAtDesc(List<String> assetId, String status);



}
