package assetmanagement.repository;


import java.util.Optional;
import assetmanagement.model.RefreshToken;
import assetmanagement.model.Users;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUsers(String userId);

	String deleteByUsers(Users user);
}
