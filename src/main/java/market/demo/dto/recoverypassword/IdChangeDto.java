package market.demo.dto.recoverypassword;

import lombok.Data;

@Data
public class IdChangeDto {
    private String loginId;
    private String newId;
    private String confirmId;
}
