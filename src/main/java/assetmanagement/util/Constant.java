package assetmanagement.util;

import org.springframework.beans.factory.annotation.Value;

public class Constant {
    // @Value("${upload.path}")
    // private static String fileBasePath;
    public static final String DATA_NOT_FOUND = "Data Not Found";
    public static final String AUDIT_DATE = "auditDate";
    public static final String NEXT_AUDIT_DATE = "nextAuditDate";
    public static final String URL_FE = "http://localhost:3000";
    public static final String KEY = "HEPLASSET";
    public static final String USER_ROLE = "User";
    // Local
    public static final String FILE_PATH = "src/main/resources/static/uploads/";

    // testing demo
    // public static final String FILE_PATH =
    // "/var/www/html/api/asset_management_java/uploads/";

    // Magical Portal
    // public static final String FILE_PATH =
    // "/var/www/ckassets/ckassets_be/uploads/";

    // QA
    // public static final String FILE_PATH =
    // "/var/www/html/qa/asset_management/asset_management_be/uploads/";

}
