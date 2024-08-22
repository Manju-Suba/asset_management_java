package assetmanagement.repository.audit;

import assetmanagement.model.Maintenance;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MaintenanceRepository extends MongoRepository<Maintenance, String> {

    // Maintenance findByEquipmentNumberAndMalfunctionStart(String equipmentNumber,
    // String malfunctionStart);
}
