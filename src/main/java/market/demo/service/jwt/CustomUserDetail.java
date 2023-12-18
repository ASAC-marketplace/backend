package market.demo.service.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetail extends User {
    private final Long memberId;
    public CustomUserDetail(String username, String password, Collection<? extends GrantedAuthority> authorities, Long memberId) {
        super(username, password, authorities);
        this.memberId = memberId;
    }

    public Long getMemberId() {
        return memberId;
    };
}
