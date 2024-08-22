package assetmanagement.service.masters;

import java.util.List;
import org.springframework.stereotype.Service;

import assetmanagement.model.masters.Location;

@Service
public interface LocationService {

    Location create(Location location);
       
    Location getLocationbyId(String locationId);
   
    List<Location> getAll();
   
    Location update(Location location);
   
    Location delete(String locationId);

}
