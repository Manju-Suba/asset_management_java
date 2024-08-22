package assetmanagement.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetResponse {
    private String id;
    private String email;
    private LocalDateTime time;
    private String url;
    private String activeStatus;
    private String urlStatus;
    private Date createdAt;
    private Date updatedAt;
    private Long expiredHours;

}
