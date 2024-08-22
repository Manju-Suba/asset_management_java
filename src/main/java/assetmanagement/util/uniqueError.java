package assetmanagement.util;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import assetmanagement.response.ApiResponse;

public class uniqueError {
    
    public static ResponseEntity<ApiResponse> handleUnique(DataIntegrityViolationException e){
        String errorMessage = e.getMessage();
        String pattern = "index: (\\w+) dup key:";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(errorMessage);

        if (matcher.find()) {
            String duplicateField = matcher.group(1);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(false, "Employee with " + duplicateField + " already exists", Collections.emptyList() ));
        } else {
            // Handle the case when the pattern is not matched
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(false, "Employee Already Exist", e.getMessage()));
        }
    }
}
