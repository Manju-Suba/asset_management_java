package assetmanagement.serviceImpl.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import assetmanagement.enumData.ActiveInActive;
import assetmanagement.model.AssetHistory;
import org.springframework.data.domain.Sort;
import assetmanagement.repository.asset.AssetHistoryRepository;
import assetmanagement.repository.asset.AssetRepository;
import assetmanagement.response.ActivityReportDTO;
import assetmanagement.response.AssetResponse;
import assetmanagement.service.reports.ReportService;
import assetmanagement.util.AuthUser;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{

    public final AssetHistoryRepository assetHistoryRepository;
    public final AssetRepository assetRepository;

    // @Override
    // public List<Map<String, Object>> getAllActivityReport() {
    //     Sort sortById = Sort.by(Sort.Direction.DESC, "id");
    //     List<AssetHistory> historyList =  assetHistoryRepository.findByCompanyIdAndStatus(AuthUser.getCompanyId(),ActiveInActive.ACTIVE.getValue(),sortById);
    //     Set<String> uniqueAssetIds = new HashSet<>();
    //     for (AssetHistory assetHistory : historyList) {
    //         uniqueAssetIds.add(assetHistory.getAssetReferenceId().getId());
    //     }
    //     List<Map<String, Object>> finalResult = new ArrayList<>();
    //     for (String assetReferenceId : uniqueAssetIds) {
    //         Map<String, Object> result = new HashMap<>();
    //         List<AssetHistory> historyByAsset = assetHistoryRepository.findByAssetReferenceId(assetReferenceId);
    //         for (AssetHistory data : historyByAsset) {
    //             result.put("AssetId",data.getAssetReferenceId().getId());
    //             if(data.getType().equals("Allocate")){
    //                 result.put("AllocateDate",data.getAllocatedDate());
    //             }
    //             if(data.getType().equals("Get Back")){
    //                 result.put("GetBackDate",data.getGetBackDate());
    //             }
    //             if(data.getType().equals("Retrial")){
    //                 result.put("retrialDate",data.getRetiralDate());
    //                 result.put("reason",data.getReason());
    //             }
    //             result.put("name",data.getAssetReferenceId().getName());
    //             if("Yes".equals(data.getAssetReferenceId().getAssetAllocate())){
    //                 result.put("employeeName",data.getAssetReferenceId().getEmployee().getFullName());
    //                 result.put("employeeId",data.getAssetReferenceId().getEmployee().getEmpId());
    //             }
    //         }
    //         finalResult.add(result);
    //     }
    //     return finalResult;
    // }

    @Override
    public List<ActivityReportDTO> getAllActivityReport() {
        // Sort sortById = Sort.by(Sort.Direction.DESC, "id");
        List<AssetHistory> historyList = assetHistoryRepository.findByCompanyIdAndStatus(AuthUser.getCompanyId(), ActiveInActive.ACTIVE.getValue());
        Map<String, ActivityReportDTO> assetMap = new HashMap<>();

        for (AssetHistory assetHistory : historyList) {
            String assetReferenceId = assetHistory.getAssetId().getId();
            ActivityReportDTO result = assetMap.getOrDefault(assetReferenceId, new ActivityReportDTO());
            result.setAssetId(assetReferenceId);

            switch (assetHistory.getType()) {
                case "Allocate":
                    result.setAllocateDate(assetHistory.getAllocatedDate());
                    break;
                case "Get Back":
                    result.setGetBackDate(assetHistory.getGetBackDate());
                    break;
                case "Retrial":
                    result.setRetrialDate(assetHistory.getRetiralDate());
                    result.setReason(assetHistory.getReason());
                    break;
            }
            result.setName(assetHistory.getAssetId().getName());
            if ("Yes".equals(assetHistory.getAssetId().getAssetAllocate())) {
                result.setEmployeeName(assetHistory.getAssetId().getEmployee().getFullName());
                result.setEmployeeId(assetHistory.getAssetId().getEmployee().getEmpId());
            }

            assetMap.put(assetReferenceId, result);
        }
        return new ArrayList<>(assetMap.values());
    }
    
    @Override
    public List<AssetResponse> assetByLocation(String locationId) {
        if(locationId !=null && !locationId.isEmpty()){
            Sort sortById = Sort.by(Sort.Direction.DESC, "id");
            return assetRepository.findByLocationIdAndCompanyIdAndStatus(locationId,AuthUser.getCompanyId(), ActiveInActive.ACTIVE.getValue(), sortById);
        
        }else{
            Sort sortById = Sort.by(Sort.Direction.DESC, "id");
            return assetRepository.findAllByCompanyIdAndStatus(AuthUser.getCompanyId(), ActiveInActive.ACTIVE.getValue(), sortById);
        }
    }

    @Override
    public List<AssetResponse> assetAssetByType(String typeId) {
        if(typeId !=null && !typeId.isEmpty()){
            Sort sortById = Sort.by(Sort.Direction.DESC, "id");
            return assetRepository.findByAssetTypeIdAndCompanyIdAndStatus(typeId,AuthUser.getCompanyId(), ActiveInActive.ACTIVE.getValue(), sortById);
        
        }else{
            Sort sortById = Sort.by(Sort.Direction.DESC, "id");
            return assetRepository.findAllByCompanyIdAndStatus(AuthUser.getCompanyId(), ActiveInActive.ACTIVE.getValue(), sortById);
        }
    }

}
