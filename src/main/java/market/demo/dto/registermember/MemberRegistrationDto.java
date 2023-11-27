package market.demo.dto.registermember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRegistrationDto {
    private String loginId;
    private String password;
    private String memberName;
    private String email;
    private String phoneNumber;
}
