package assetmanagement.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import assetmanagement.security.UserDetailsImpl;

public class AuthUser {
    public static String getUserId() {
        String userId;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (authentication != null && authentication.isAuthenticated()) {
            userId = userDetails.getId();
        } else {
            userId = "null";
        }
        return userId;
    }
    
    public static String getCompanyId() {
        String companyId;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (authentication != null && authentication.isAuthenticated()) {
            companyId = userDetails.getCompanyId();
        } else {
            companyId = "null";
        }
        return companyId;
    }

    public static String getPlant() {
        String plant;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (authentication != null && authentication.isAuthenticated()) {
            plant = userDetails.getPlant();
        } else {
            plant = "null";
        }
        return plant;
    }
}
