package assetmanagement.service.masters;

import java.util.List;

import assetmanagement.model.masters.Business;


public interface BusinessService {

    Business create(Business business);

    Business getBusinessById(String id);

    List<Business> getAll();

    Business update(Business business);

    Business delete(String id);

}
