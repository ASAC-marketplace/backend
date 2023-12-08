package market.demo.dto.changememberinfo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckMemberInfoDto {
    //26-개인정보 수정
    @NotNull
    @NotEmpty
    private String loginId;
    private String password;
}
