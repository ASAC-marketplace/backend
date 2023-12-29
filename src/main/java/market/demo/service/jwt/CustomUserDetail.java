package market.demo.service.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetail extends User {
    private final Long memberId;
    private final String email;

    public CustomUserDetail(String username, String password,
                            Collection<? extends GrantedAuthority> authorities, Long memberId, String email) {
        super(username, password, authorities);
        this.memberId = memberId;
        this.email = email; // 이메일 초기화
    }
}
