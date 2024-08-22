package assetmanagement.service.masters;

import java.util.List;
import org.springframework.stereotype.Service;

import assetmanagement.model.masters.Brand;

@Service
public interface BrandService {

    Brand create(Brand brand);
    
    List<Brand> getAllBrand();

    Brand getById(String id);
    
    Brand update(Brand brand);
    
    Brand delete(String id);

}
