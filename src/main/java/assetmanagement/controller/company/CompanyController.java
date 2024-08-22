package assetmanagement.controller.company;

import org.springframework.web.bind.annotation.RestController;

import assetmanagement.model.Company;
import assetmanagement.response.ApiResponse;
import assetmanagement.service.company.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;




@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("/save")
    public ResponseEntity<ApiResponse> save(@Valid Company company) {
        try {
            Company savedRecord = companyService.save(company);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true,"Created Successfully",savedRecord));
        } catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, e.getMessage(), Collections.emptyList()));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Internal Server Error", e.getMessage()));
        } 
    }
      
}
