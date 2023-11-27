package market.demo.dto.recoverypassword;

import lombok.Data;

@Data
public class RecoveryPasswordRequestDto {
    private String loginId;
    private String email;
}
