package assetmanagement.serviceImpl.masters;

import assetmanagement.enumData.ActiveInActive;
import assetmanagement.model.masters.Business;
import assetmanagement.repository.masters.BusinessRepository;
import assetmanagement.service.masters.BusinessService;
import assetmanagement.util.AuthUser;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessServiceImpl implements BusinessService {

    private final BusinessRepository businessRepository;

    // @Autowired
    // private AuditorAware<String> auditorAware;
    @Override
    public Business create(Business business) {
        if(businessRepository.existsByNameIgnoreCaseAndCompanyIdAndStatus(business.getName().trim(),AuthUser.getCompanyId(),ActiveInActive.ACTIVE.getValue())){
            throw new IllegalArgumentException("Business already exist ");
        }
        business.setCompanyId(AuthUser.getCompanyId());
        return businessRepository.save(business);
    }

    @Override
    public Business getBusinessById(String id) {
        
        return businessRepository.findByIdAndStatus(id,ActiveInActive.ACTIVE.getValue())
            .orElse(null);
    }

    @Override
    public List<Business> getAll() {
        List<Business> business = businessRepository.findByCompanyIdAndStatus(AuthUser.getCompanyId(),ActiveInActive.ACTIVE.getValue());
        return business;
    }

    @Override
    public Business update(Business business) {
        if(businessRepository.existsByNameIgnoreCaseAndCompanyIdAndStatusAndIdNot(business.getName().trim(),AuthUser.getCompanyId(),ActiveInActive.ACTIVE.getValue(),business.getId())){
            throw new IllegalArgumentException("Business already exists ");
        }
        Business existingBusiness = businessRepository.findById(business.getId())
        .orElseThrow(() -> new IllegalArgumentException("Business not found"));
        existingBusiness.setName(business.getName());
        existingBusiness.setDescription(business.getDescription());
            return businessRepository.save(existingBusiness);
        }

    @Override
    public Business delete(String id) {
        Optional<Business> businessId=businessRepository.findByIdAndStatus(id,ActiveInActive.ACTIVE.getValue());
        if(businessId.isPresent()){
            Business businessdelete= businessId.get();
            businessdelete.setStatus(ActiveInActive.INACTIVE.getValue());
            return businessRepository.save(businessdelete);
        }
        return null;
    }

}
