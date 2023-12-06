package market.demo.dto.changememberinfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyMemberInfoDto {

    private String loginId;
    private String password;
    private String newPassword;
    private String newPasswordCheck;
    private String memberName;
    private String email;
    private String phoneNumber;

    public void setNewPassword(String newPassword){
        if(newPassword == null || newPassword.isEmpty()){
            this.newPassword = "";
        }else this.newPassword = newPassword;
    }

    public void setNewPasswordCheck(String newPasswordCheck){
        if(newPasswordCheck == null || newPasswordCheck.isEmpty()){
            this.newPasswordCheck = "";
        }else this.newPasswordCheck = newPasswordCheck;
    }
}
