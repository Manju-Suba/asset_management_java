package assetmanagement.service;

import assetmanagement.exception.ValidationException;
import assetmanagement.model.RefreshToken;
import assetmanagement.model.Users;
import assetmanagement.repository.RefreshTokenRepository;
import assetmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {


    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    @Value("${hepl.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }


    public RefreshToken createRefreshToken(String userId) {

        Optional<RefreshToken> existingTokenOptional = refreshTokenRepository.findByUsers(userId);

        if (existingTokenOptional.isPresent()) {
            RefreshToken existingToken = existingTokenOptional.get();
            existingToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
            return refreshTokenRepository.save(existingToken);
        } else {

            RefreshToken newRefreshToken = new RefreshToken();
            newRefreshToken.setUsers(userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found")));
            newRefreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
            newRefreshToken.setToken(UUID.randomUUID().toString());
            return refreshTokenRepository.save(newRefreshToken);
        }
    }


    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new ValidationException(token.getToken() + "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }


    public String deleteByUserId(String userId) {
        Optional<Users> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            return refreshTokenRepository.deleteByUsers(userOptional.get());
        }
        return null;
    }

}
