package market.demo.dto.recoverypassword;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PasswordChangeDto {

    @NotEmpty
    private String loginId;
    private String newPassword;
    private String confirmPassword;
}
