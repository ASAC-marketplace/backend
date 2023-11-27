package market.demo.dto.registermember;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailAvailabilityDto {
    @NotNull
    @NotEmpty
    @Email
    private String email;
}
