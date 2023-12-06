package market.demo.dto.registermember;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginIdAvailabilityDto {
    @NotEmpty
    @NotNull
    private String loginId;
}
