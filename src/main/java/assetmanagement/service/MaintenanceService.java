package assetmanagement.service;

import assetmanagement.dto.MaintenanceDTO;
import assetmanagement.model.Maintenance;
import assetmanagement.response.MaintenanceResponse;
import assetmanagement.response.SapResponse;

import java.util.List;

public interface MaintenanceService {
    SapResponse addApiData();

    List<Maintenance> getAllMaintenances();

    MaintenanceResponse getByAssetId(String assetId, String fromDate, String toDate, Integer page, Integer size);

    Maintenance create(MaintenanceDTO maintenanceDTO);
}
