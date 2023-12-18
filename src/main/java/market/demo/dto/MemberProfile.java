package market.demo.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class MemberProfile {
    private String name;
    private String email;
    private String gender;
    private String ageRange;
    private String birthday;
}
