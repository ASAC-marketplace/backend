package market.demo.domain.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoSocialDto1 {
    private String email;
    private String provider;
    private String providerId;
}
