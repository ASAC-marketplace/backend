package market.demo.dto.recoverypassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecoveryPasswordRequestDto {
    @NotNull
    @NotEmpty
    private String loginId;

    @NotNull
    @NotEmpty
    @Email
    private String email;
}
