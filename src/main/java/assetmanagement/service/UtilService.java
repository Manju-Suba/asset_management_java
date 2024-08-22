package assetmanagement.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Service
public class UtilService {
    @Value("${upload.path}")
    private String fileBasePath;

    public byte[] getFile(String filename) {
        try {
            Path filePath = Paths.get("uploads").resolve(filename).normalize();
            return Files.readAllBytes(filePath);
        } catch (IOException ex) {
            ex.printStackTrace();
            return new byte[0];
        }
    }

    public byte[] pictureAsBytes(String filename) {
        if (filename != null && !filename.isEmpty()) {

            Path filePath = Paths.get(fileBasePath).resolve(filename).normalize();

            if (filePath != null) {
                try {
                    return Files.readAllBytes(filePath);
                } catch (IOException e) {
                    e.printStackTrace(); // You may want to handle this more gracefully
                }
            }
        }
        return new byte[0];
    }

    public String getPictureWithPath(String fileName) {
        byte[] pictureBytes = pictureAsBytes(fileName);
        if (pictureBytes.length > 0) {
            String encodedImage = Base64.getEncoder().encodeToString(pictureBytes);
            return "data:image/jpg;base64," + encodedImage;
        }
        return null;
    }
}
