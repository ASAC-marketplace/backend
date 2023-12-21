package market.demo.dto.changememberinfo;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ModifyMemberInfoDto {

    private String loginId;
    private String password;
    private String newPassword;
    private String newPasswordCheck;
    private String memberName;
    private String email;
    private String phoneNumber;

    public void checkNewPassword(){
        if(!this.newPassword.equals(this.newPasswordCheck)) throw new IllegalArgumentException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
    }
}
