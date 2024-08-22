package assetmanagement.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotNull(message = "is mandatory")
    @NotEmpty(message = "must not be null")
    private String email;
    @NotNull(message = "is mandatory")
    @NotEmpty(message = "must not be null")
    private String password;
}
