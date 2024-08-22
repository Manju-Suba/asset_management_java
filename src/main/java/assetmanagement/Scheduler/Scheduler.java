package assetmanagement.Scheduler;

import assetmanagement.response.SapResponse;
import assetmanagement.service.MaintenanceService;
import assetmanagement.service.asset.AssetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class Scheduler {

    private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);
    private final AssetService assetService;
    private final MaintenanceService maintenanceService;

    @Autowired
    public Scheduler(AssetService assetService, MaintenanceService maintenanceService) {
        this.assetService = assetService;
        this.maintenanceService = maintenanceService;
    }

    @Scheduled(cron = "0 43 12 11 * ?") // Executes at 6:20 PM on the 24th day of every month
    public void cronJobSch() {
        try {
            logger.info("Data Fetch Start at {}", getCurrentTime());
            SapResponse assetList = assetService.fetchDataAndInsert();
            logger.info("Data Fetched Successfully at {}", getCurrentTime());
            logger.info("Data Updated Start at {}", getCurrentTime());
            SapResponse assetListUpdate = assetService.fetchDataAndUpdate();
            logger.info("Data Updated Successfully at {}", getCurrentTime());
        } catch (Exception e) {
            logger.error("Error fetching data: {}", e.getMessage());
        }
    }

    @Scheduled(cron = "0 16 16 ? * FRI") // Executes at 6:20 PM on the 24th day of every month
    public void cronJobSchMaintence() {
        try {
            logger.info("Data Fetch Start at {}", getCurrentTime());
            maintenanceService.addApiData();
            logger.info("Data Updated Successfully at {}", getCurrentTime());
        } catch (Exception e) {
            logger.error("Error fetching data: {}", e.getMessage());
        }
    }


    private String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
}
