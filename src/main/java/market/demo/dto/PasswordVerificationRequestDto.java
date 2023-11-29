package market.demo.dto;

import lombok.Data;

@Data
public class PasswordVerificationRequestDto {
    private String email;
    private String password;
}
