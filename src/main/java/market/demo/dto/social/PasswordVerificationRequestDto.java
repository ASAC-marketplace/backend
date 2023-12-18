package market.demo.dto.social;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PasswordVerificationRequestDto {

    @NotEmpty
    private String password;
    private String email;
    private String provider;
    private String providerId;
}
