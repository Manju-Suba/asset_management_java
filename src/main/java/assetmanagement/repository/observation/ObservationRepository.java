package assetmanagement.repository.observation;

import org.springframework.data.mongodb.repository.MongoRepository;

import assetmanagement.model.audit.Observation;

public interface ObservationRepository extends MongoRepository<Observation, String> {

}
