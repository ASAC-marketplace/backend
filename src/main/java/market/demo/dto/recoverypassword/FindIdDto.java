package market.demo.dto.recoverypassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class FindIdDto {
    @NotEmpty
    private String memberName;

    @NotEmpty
    @Email
    private String email;
}
