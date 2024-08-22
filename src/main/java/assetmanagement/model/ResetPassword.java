package assetmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "reset_password")
public class ResetPassword {
    @Id
    private String id;
    private String email;
    private LocalDateTime time;
    private String url;
    private String activeStatus;
    private String urlStatus;
    private Date createdAt;
    private Date updatedAt;
}
