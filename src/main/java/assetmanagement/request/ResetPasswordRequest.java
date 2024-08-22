package assetmanagement.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    @NotNull(message = "is mandatory")
    @NotEmpty(message = "must not be null")
    private String password;
    @NotNull(message = "is mandatory")
    @NotEmpty(message = "must not be null")
    private String confirmPassword;
    private String email;
}
