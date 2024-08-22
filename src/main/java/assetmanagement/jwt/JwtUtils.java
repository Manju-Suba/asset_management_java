package assetmanagement.jwt;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
//import javax.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${hepl.app.jwtSecret}")
    private String jwtSecret;

    @Value("${hepl.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(String email, Map<String, Object> additionalData) throws UnknownHostException {
        // Retrieve server IP address from the request
        String serverIp = InetAddress.getLocalHost().getHostAddress();

        return Jwts.builder()
                .setSubject(email)
                .addClaims(additionalData)
                .claim("serverIp", serverIp)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshJwtToken(String email, Map<String, Object> additionalData) {
        // Retrieve server IP address from the request
        String serverIp = getServerIpAddress();

        return Jwts.builder()
                .setSubject(email)
                .addClaims(additionalData)
                .claim("serverIp", serverIp)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private String getServerIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            // Log or handle the exception appropriately
            e.printStackTrace(); // Example: Printing the stack trace
            return null; // Or return a default IP address or handle it in another way
        }
    }

    public String generateJwtTokenMobileno(String email) {

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public String getServerIpFromJwtToken(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Extract the server IP address from the token's claims
        return (String) claims.get("serverIp");
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            // Token is malformed
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            // Token is expired
            logger.error("JWT token is expired: {}", e.getMessage());
            // Optionally, you may want to perform additional actions here, such as deleting
            // the token from local storage
            return false;
        } catch (UnsupportedJwtException e) {
            // Token is unsupported
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            // JWT claims string is empty
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions
            logger.error("Unexpected error during JWT validation: {}", e.getMessage());
        }

        return false;
    }

    private void deleteJwtTokenFromLocalStorage() {
        // Example assuming the key for your JWT token in local storage is "jwtToken"
        // Replace "jwtToken" with the actual key you used for storing the JWT token

        try {
            // Access local storage
            java.util.prefs.Preferences.userRoot().remove("jwtToken");
        } catch (Exception e) {
            // Handle the exception if accessing local storage fails
            e.printStackTrace();
        }
    }

    public String validateJwtToken1(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return "Token is valid";
        } catch (MalformedJwtException e) {
            // logger.error("Invalid JWT token: {}", e.getMessage());
            return "Invalid JWT token: " + e.getMessage();
        } catch (ExpiredJwtException e) {
            // logger.error("JWT token is expired: {}", e.getMessage());
            return "JWT token is expired: " + e.getMessage();
        } catch (UnsupportedJwtException e) {
            // logger.error("JWT token is unsupported: {}", e.getMessage());
            return "JWT token is unsupported: " + e.getMessage();
        } catch (IllegalArgumentException e) {
            // logger.error("JWT claims string is empty: {}", e.getMessage());
            return "JWT claims string is empty: " + e.getMessage();
        }
    }

    public Date getExpirationDateFromJwtToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
        return claims.getExpiration();
    }

}
