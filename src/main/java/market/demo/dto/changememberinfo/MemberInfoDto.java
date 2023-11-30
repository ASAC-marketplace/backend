package market.demo.dto.changememberinfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberInfoDto {
    //26-개인정보 수정
    private String loginId;
    private String memberName;
    private String email;
    private String phoneNumber;
}
