package assetmanagement.serviceImpl.asset;

import org.springframework.stereotype.Service;
import assetmanagement.model.SubClass;
import assetmanagement.repository.SubClassRepository;
import assetmanagement.response.SubClassResponse;
import assetmanagement.service.masters.SubClassService;
import assetmanagement.util.AuthUser;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class SubClassServiceImpl implements SubClassService {

    public final SubClassRepository subClassRepository;

    @Override
    public SubClassResponse getAllAssetClass(String assetClass, Integer page, Integer size) {

        if (page == null && size == null || page == null || size == null) {
            page = 0;
            size = 10;
        }
        Pageable pageable = PageRequest.of(page, size);
        SubClassResponse response = new SubClassResponse();
        if (assetClass != null) {
            Page<SubClass> pagedSubClasses = subClassRepository.findByCompanyIdAndPlantAndAssetClass(
                    AuthUser.getCompanyId(), AuthUser.getPlant(), assetClass, pageable);
            response.setSubClass(pagedSubClasses.getContent());
            response.setSubClassCounts(pagedSubClasses.getTotalElements());
            return response;
        } else {
            throw new RuntimeException("assetClass is null");
        }
    }

    @Override
    // @Cacheable(value = "subClassCache", key = "#assetClass")
    public List<SubClass> getAllAssetClassBasedSubClass(String assetClass) {
        if (assetClass != null) {
            return subClassRepository.findByCompanyIdAndPlantAndAssetClass(AuthUser.getCompanyId(), AuthUser.getPlant(),
                    assetClass);
        } else {
            throw new RuntimeException("assetClass is null");
        }
    }

    @Override
    public SubClassResponse getAllSubClass(Integer page, Integer size) {
        if (page == null && size == null || page == null || size == null) {
            page = 0;
            size = 10;
        }
        long counts = subClassRepository.countByCompanyIdAndPlant(AuthUser.getCompanyId(), AuthUser.getPlant());
        Pageable pageable = PageRequest.of(page, size);
        List<SubClass> allSubClasses = subClassRepository.findByCompanyIdAndPlant(AuthUser.getCompanyId(),
                AuthUser.getPlant(), pageable);
        SubClassResponse response = new SubClassResponse();
        response.setSubClass(allSubClasses);
        response.setSubClassCounts(counts);
        return response;
    }

}
