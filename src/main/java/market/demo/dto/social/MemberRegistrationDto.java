package market.demo.dto.social;

import lombok.Data;

@Data
public class MemberRegistrationDto {
    private String loginId;
    private String password;
    private String phoneNumber;
}
