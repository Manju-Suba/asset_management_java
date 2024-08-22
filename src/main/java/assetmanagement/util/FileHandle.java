package assetmanagement.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

public class FileHandle {
    
    @Value("${upload.path}")
    private static String fileBasePath;
    
    public static String saveAssetPicture(MultipartFile file)  {
        String originalName = file.getOriginalFilename();
        String fileName = Format.formatDate() + "_" + originalName;
        Path path = Path.of(fileBasePath + fileName);
        try {
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }
}
