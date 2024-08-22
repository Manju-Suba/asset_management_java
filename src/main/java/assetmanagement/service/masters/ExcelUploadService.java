package assetmanagement.service.masters;

import assetmanagement.model.Asset;
import assetmanagement.model.masters.Employee;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public interface ExcelUploadService {
    static boolean isValidExcelFile(MultipartFile file) {
        return Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    List<Asset> getAssetModelfromExcel(InputStream inputStream) throws IOException;
    List<Employee> getEmployeesFromExcel(InputStream inputStream) throws IOException;
    List<Employee> saveBulkOfEmployees(List<Employee> employeeList);
}
