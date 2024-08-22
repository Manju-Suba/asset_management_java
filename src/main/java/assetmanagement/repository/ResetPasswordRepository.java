package assetmanagement.repository;

import assetmanagement.model.ResetPassword;
import assetmanagement.response.ResetResponse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ResetPasswordRepository extends MongoRepository<ResetPassword, String> {
    @Query("{'email': ?0}")
    List<ResetResponse> findByEmailOrderByTimeDesc(String email);

    // ResetPassword findByEmailOrderByTimeDesc(String email);
    ResetPassword findFirstByEmailOrderByCreatedAtDesc(String email);
    // Optional<EmailRecord> findFirstByEmailOrderByCreatedAtDesc(String email);

    ResetPassword findByIdAndEmail(String id, String email);
}


