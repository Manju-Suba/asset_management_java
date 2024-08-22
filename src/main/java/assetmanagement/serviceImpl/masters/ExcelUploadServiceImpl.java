package assetmanagement.serviceImpl.masters;

import assetmanagement.enumData.ActiveInActive;
import assetmanagement.exception.ResourceNotFoundException;
import assetmanagement.model.Asset;
import assetmanagement.model.Company;
import assetmanagement.model.masters.*;
import assetmanagement.repository.asset.AssetRepository;
import assetmanagement.repository.company.CompanyRepository;
import assetmanagement.repository.masters.*;
import assetmanagement.service.masters.ExcelUploadService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExcelUploadServiceImpl implements ExcelUploadService {
    private final AssetRepository assetRepository;
    
    private final BusinessRepository businessModelRepository;

    private final EmployeeRepository employeesModelRepository;

    private final DepartmentRepository departmentModelRepository;

    private final CompanyRepository companyRepository;

    @Override
    public List<Asset> getAssetModelfromExcel(InputStream inputStream) throws IOException {
        List<Asset> models = new ArrayList<>();
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheet("allinone");
        String fullName = null;
        String empId = null;
        List<String> columnNamesList = new ArrayList<>();
        String[] columnNames = null;
        for (Row row : sheet) {
            int rowIndex = row.getRowNum();
            if (rowIndex == 0) {
                for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    if (cell == null) {
                        continue;
                    }
                    String columnName = cell.toString();
                    columnNamesList.add(columnName);
                }
                columnNames = columnNamesList.toArray(new String[0]);
                continue;
            }
            Asset assetsModel = new Asset();

            for (int i = 0; i < (columnNames != null ? columnNames.length : 0); i++) {
                Cell cell = row.getCell(i);
                if (cell == null) {
                    break;
                }

                switch (columnNames[i]) {
                   
                    case "AssetID":
                        assetsModel.setAssetId(getStringValueFromCell(cell));
                        break;
                
                   
                    default:
                        break;
                }
            }

            models.add(assetsModel);
        }

        return assetRepository.saveAll(models);
    }

    @Override
    public List<Employee> getEmployeesFromExcel(InputStream inputStream) throws IOException {
        List<Employee> employeeList = new ArrayList<>();
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheet("Employees");
        List<String> columnNamesList = new ArrayList<>();
        String[] columnNames = null;
        for (Row row : sheet) {
            int rowIndex = row.getRowNum();
            if (rowIndex == 0) {
                for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    if (cell == null) {
                        continue;
                    }
                    String columnName = cell.toString();
                    columnNamesList.add(columnName);
                }
                columnNames = columnNamesList.toArray(new String[0]);
                continue;
            }

            Employee employess = new Employee();
            for (int i = 0; i < (columnNames != null ? columnNames.length : 0); i++) {
                Cell cell = row.getCell(i);
                if (cell == null) {
                    break;
                }

                switch (columnNames[i]) {
                    case "EmployeeID":
                        employess.setEmpId(cell.getStringCellValue());
                        break;
                    case "Business":
                        String getBusinessName = cell.getStringCellValue();
                        Optional<Business> businessOptional = businessModelRepository.findByNameAndStatus(getBusinessName, ActiveInActive.ACTIVE.getValue());
                        String bId = businessOptional.map(Business::getId).orElse(null);
                        employess.setBusinessId(bId);
                        break;
                    case "Company":
                        String getCompanyName = cell.getStringCellValue();
                        Optional<Company> companyOptional = companyRepository.findByName(getCompanyName);
                        String companyId = companyOptional.map(Company::getId).orElse(null);
                        employess.setCompanyId(companyId);
                        break;
                    case "Department":
                        String getDepartmentName = cell.getStringCellValue();
                        Optional<Department> departmentData = departmentModelRepository.findByNameAndStatus(getDepartmentName, ActiveInActive.ACTIVE.getValue());
                        Department department = departmentData.orElseThrow(() -> new ResourceNotFoundException("Data not found for department"));
                        employess.setDepartment(department);
                        break;
                    case "FullName":
                        employess.setFullName(cell.getStringCellValue());

                        break;
                    case "Email":
                        String email = cell.getStringCellValue();
                        Optional<Employee> mailCheck = employeesModelRepository.findByEmailAndStatus(email, ActiveInActive.ACTIVE.getValue());
                        if (mailCheck.isPresent()) {
                            throw new IllegalArgumentException("Email Already Exists");
                        }
                        employess.setEmail(email);
                        break;
                    case "Designation":
                        employess.setJobRole(cell.getStringCellValue());
                        break;
                    case "City":
                        employess.setCity(cell.getStringCellValue());
                        break;
                    case "Country":
                        employess.setCountry(cell.getStringCellValue());
                        break;
                    case "DateOfJoining":
                        Date dateFormat = cell.getDateCellValue();
                        LocalDate Date_of_joining = dateFormat.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        employess.setDateOfJoining(Date_of_joining);
                        break;
                    case "Address":
                        employess.setAddress(cell.getStringCellValue());
                        break;
                    default:
                        break;
                }
            }

            employeeList.add(employess);

        }
        return employeeList;
    }


    @Override
    public List<Employee> saveBulkOfEmployees(List<Employee> employeeList) {

        return employeesModelRepository.saveAll(employeeList);
    }

    private String getStringValueFromCell(Cell cell) {
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        } else if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else {
            return null;
        }
    }
}
