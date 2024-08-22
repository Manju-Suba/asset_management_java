package assetmanagement.serviceImpl.company;

import org.springframework.stereotype.Service;
import assetmanagement.model.Company;
import assetmanagement.repository.company.CompanyRepository;
import assetmanagement.service.company.CompanyService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    
    @Override
    public Company save(Company company) {
       if(companyRepository.existsByName(company.getName())){
        throw new IllegalArgumentException("Company name is already exist.");
       }
       return companyRepository.save(company);
       
    }

}
