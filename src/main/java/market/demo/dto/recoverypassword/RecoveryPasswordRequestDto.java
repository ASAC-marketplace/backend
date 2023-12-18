package market.demo.dto.recoverypassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RecoveryPasswordRequestDto {
    @NotEmpty
    private String loginId;

    @NotEmpty
    @Email
    private String email;
}
