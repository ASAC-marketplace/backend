package market.demo.dto.changememberinfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyMemberInfoDto {
    private String loginId;
    private String password;
    private String newPassword;
    private String newPassword_check;
    private String memberName;
    private String email;
    private String phoneNumber;

}
