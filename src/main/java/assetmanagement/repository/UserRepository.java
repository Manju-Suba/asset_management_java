package assetmanagement.repository;

import java.util.List;
import java.util.Optional;

import assetmanagement.dto.UsersDto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import assetmanagement.model.Users;

public interface UserRepository extends MongoRepository<Users, String> {

    Optional<Users> findByEmail(String email);


    boolean existsByEmail(String email);


    boolean existsByPhoneNo(String phoneno);

    @Query("{'status': 'Active', '_id': ?0}")
    Optional<Users> findByIdAndStatusActive(String userId);

    @Query("{'status': 'active'}")
    List<UsersDto> findAllActive();


	Optional<Users> findByUserId(String id);
    @Query("{'status': 'Active', 'email': ?0}")
    Optional<UsersDto> findByEmailAndStatusActive(String email);

    Optional<Users> findByEmailAndStatus(String email,String status);
    Optional<UsersDto> findByIdAndStatus(String userId, String value);

    List<Users> findByCompanyIdAndPlantAndRole(String companyId, String plant, String userRole);
}
