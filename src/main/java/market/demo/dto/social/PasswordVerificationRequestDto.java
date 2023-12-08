package market.demo.dto.social;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PasswordVerificationRequestDto {

    @NotNull
    @NotEmpty
    private String password;
}
