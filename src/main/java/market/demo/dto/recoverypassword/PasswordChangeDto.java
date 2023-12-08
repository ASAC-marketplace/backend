package market.demo.dto.recoverypassword;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PasswordChangeDto {
    @NotNull
    @NotEmpty
    private String loginId; //제거
    private String newPassword;
    private String confirmPassword;
}
