package market.demo.dto.recoverypassword;

import lombok.Data;

@Data
public class PasswordChangeDto {
    private String loginId;
    private String newPassword;
    private String confirmPassword;
}
