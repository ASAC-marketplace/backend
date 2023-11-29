package market.demo.dto.social;

import lombok.Data;

@Data
public class PasswordVerificationRequestDto {
    private String email;
    private String password;
}
