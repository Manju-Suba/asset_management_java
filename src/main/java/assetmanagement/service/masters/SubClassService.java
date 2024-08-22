package assetmanagement.service.masters;

import java.util.List;

import org.springframework.stereotype.Service;

import assetmanagement.model.SubClass;
import assetmanagement.response.SubClassResponse;

@Service
public interface SubClassService {

    SubClassResponse getAllAssetClass(String assetClass, Integer page, Integer size);

    List<SubClass> getAllAssetClassBasedSubClass(String assetClass);

    SubClassResponse getAllSubClass(Integer page, Integer size);
}
