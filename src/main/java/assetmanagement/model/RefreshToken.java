package assetmanagement.model;

import java.time.Instant;
import java.util.Date;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "refresh_token")
public class RefreshToken {
    @Id
    private String id;

    @DBRef
    private Users users;
    @NotNull(message = "field is mandatory")
    private String token;
    private Instant expiryDate;
    private Date updatedAt;

}
