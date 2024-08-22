package assetmanagement.serviceImpl.masters;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import assetmanagement.service.masters.BrandService;
import assetmanagement.util.AuthUser;
import assetmanagement.enumData.ActiveInActive;
import assetmanagement.model.masters.Brand;
import assetmanagement.repository.masters.BrandRepository;

@Service
public class BrandServiceImpl implements BrandService {

    
    @Autowired    
    private BrandRepository brandRepository;

    //create brand
    @Override
    public Brand create(Brand brand) {
        if(brandRepository.existsByNameIgnoreCaseAndCompanyIdAndStatus(brand.getName().trim(),AuthUser.getCompanyId(),ActiveInActive.ACTIVE.getValue())){
            throw new IllegalArgumentException("Brand already exist ");
        }
       brand.setCompanyId(AuthUser.getCompanyId());
       return brandRepository.save(brand);
    }

    //getall brand 
    @Override
    public List<Brand> getAllBrand() {
        List<Brand> brand = brandRepository.findAllByCompanyIdAndStatus(AuthUser.getCompanyId(),ActiveInActive.ACTIVE.getValue());
        return brand;
    }

    //getbrand by primary key
    @Override
    public Brand getById(String id) {
       Optional<Brand> optionaluser = brandRepository.findByIdAndStatus(id,ActiveInActive.ACTIVE.getValue());
       return optionaluser.get();
    }
  
    //update brand by id
    @Override
    public Brand update(Brand brand) {
        if(brandRepository.existsByNameIgnoreCaseAndCompanyIdAndStatusAndIdNot(brand.getName().trim(),AuthUser.getCompanyId(),ActiveInActive.ACTIVE.getValue(),brand.getId())){
            throw new IllegalArgumentException("Brand already exists ");
        }
        Brand existingBrand = brandRepository.findById(brand.getId())
        .orElseThrow(() -> new IllegalArgumentException("Brand not found"));
        existingBrand.setName(brand.getName());
        existingBrand.setDescription(brand.getDescription());
            return brandRepository.save(existingBrand);
        }
  
    //delete brand based on brand id
    @Override
    public Brand delete(String id) {
        Optional<Brand> brandid=brandRepository.findByIdAndStatus(id,ActiveInActive.ACTIVE.getValue());
        if(brandid.isPresent()){
            Brand branddelete= brandid.get();
            branddelete.setStatus(ActiveInActive.INACTIVE.getValue());
            return brandRepository.save(branddelete);
    
        }
        return null;
    }

}
