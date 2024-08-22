package assetmanagement.serviceImpl.masters;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import assetmanagement.enumData.ActiveInActive;
import assetmanagement.model.masters.Location;
import assetmanagement.repository.masters.LocationRepository;
import assetmanagement.service.masters.LocationService;
import assetmanagement.util.AuthUser;

@Service
public class LocationServiceImpl implements LocationService{

    @Autowired
    private LocationRepository locationRepository;
   
    //save the location
    @Override
    public Location create(Location location) {
        if(locationRepository.existsByNameIgnoreCaseAndCompanyIdAndStatus(location.getName().trim(),AuthUser.getCompanyId(),ActiveInActive.ACTIVE.getValue())){
            throw new IllegalArgumentException("Location already exist ");
        }
        location.setCompanyId(AuthUser.getCompanyId());
        return locationRepository.save(location);
    }
     
    //get location based on id
    @Override
    public Location getLocationbyId(String locationId) {
        Optional<Location> optionallocation = locationRepository.findByIdAndStatus(locationId,ActiveInActive.ACTIVE.getValue());
        return optionallocation.get();
    }
    
    // get all location
    @Override
    public List<Location> getAll() {
        List<Location> location = locationRepository.findAllByCompanyIdAndStatus(AuthUser.getCompanyId(),ActiveInActive.ACTIVE.getValue());
        return location;
    }
    
    // update location based on id
    @Override
    public Location update(Location location) {
        if(locationRepository.existsByNameIgnoreCaseAndCompanyIdAndStatusAndIdNot(location.getName().trim(),AuthUser.getCompanyId(),ActiveInActive.ACTIVE.getValue(),location.getId())){
            throw new IllegalArgumentException("Location Not  Updated ");
        }
        Location existingLocation = locationRepository.findById(location.getId())
        .orElseThrow(() -> new IllegalArgumentException("Location not found"));
        existingLocation.setName(location.getName());
        existingLocation.setDescription(location.getDescription());
            return locationRepository.save(existingLocation);
        }
    //delete location based on id
    @Override
    public Location delete(String locationId) {
        Optional<Location> locationid = locationRepository.findByIdAndStatus(locationId,ActiveInActive.ACTIVE.getValue());
        if(locationid.isPresent()){
            Location locationdelete= locationid.get();
            locationdelete.setStatus(ActiveInActive.INACTIVE.getValue());
            return locationRepository.save(locationdelete);
        }
        return null;
    }

}
