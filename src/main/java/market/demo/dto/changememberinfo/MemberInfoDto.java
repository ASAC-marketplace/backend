package market.demo.dto.changememberinfo;

import lombok.Getter;
import lombok.Setter;
import market.demo.domain.member.Member;

@Getter
@Setter
public class MemberInfoDto {
    //26-개인정보 수정
    private String loginId;
    private String memberName;
    private String email;
    private String phoneNumber;

    public MemberInfoDto(Member member){
        this.loginId = member.getLoginId();
        this.memberName = member.getMemberName();
        this.email = member.getEmail();
        this.phoneNumber = member.getPhoneNumber();
    }
}
