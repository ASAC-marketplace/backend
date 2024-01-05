package market.demo.dto.social;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class MemberRegistrationDto {
    private String memberName;
    private String loginId;
    private String password;
    private String phoneNumber;
    private String providerEmail;
    private String provider;
    private String providerId;

}
