package assetmanagement.jwt;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import assetmanagement.response.ApiResponse;
import assetmanagement.security.UserDetailsServiceImpl;
import assetmanagement.service.TokenBlacklist;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.util.Collections;

public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
	private TokenBlacklist tokenBlacklist;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            String validationResult = jwtUtils.validateJwtToken1(jwt); // Call validateJwtToken method

            if (tokenBlacklist.isTokenBlacklisted(jwt)) {
                // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is blacklisted. Please log in again.");
                ApiResponse customErrorResponse = new ApiResponse(false, "Token is blacklisted. Please log in again ",Collections.EMPTY_LIST);
                String errorResponseJson = new ObjectMapper().writeValueAsString(customErrorResponse);

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write(errorResponseJson);
                return;
            }

            if (validationResult.equals("Token is valid")) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                // Retrieve server IP address from the token
                String tokenServerIp = jwtUtils.getServerIpFromJwtToken(jwt);
                // Retrieve server IP address from the request
                String requestServerIp = InetAddress.getLocalHost().getHostAddress();

                // Compare server IP addresses
                if (!tokenServerIp.equals(requestServerIp)) {
                    // Server IP address in the token doesn't match the current server's IP address
                    ApiResponse customErrorResponse = new ApiResponse(false, "Invalid Token");
                    String errorResponseJson = new ObjectMapper().writeValueAsString(customErrorResponse);

                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write(errorResponseJson);
                    return; // Exit the method to prevent further processing
                }

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if (jwt != null) {
                // Token is invalid, handle accordingly
                ApiResponse customErrorResponse = new ApiResponse(false, validationResult);
                String errorResponseJson = new ObjectMapper().writeValueAsString(customErrorResponse);

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write(errorResponseJson);
                return; // Exit the method to prevent further processing
            }
        } catch (Exception e) {

            // logger.error("An error occurred while processing the token: {}",
            // e.getMessage());
            ApiResponse customErrorResponse = new ApiResponse(false, "You have an Invalid token: " + e.getMessage());
            String errorResponseJson = new ObjectMapper().writeValueAsString(customErrorResponse);

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(errorResponseJson);
            return;
        }

        filterChain.doFilter(request, response);
    }

    public String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
    
}
