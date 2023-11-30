package market.demo.dto.social;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.member.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
@Slf4j
public class CustomOAuth2User implements OAuth2User {
    private final Map<String, Object> attributes;
    private final Member member;

    private String provider;
    private String providerId;
    private String email;

    public CustomOAuth2User(Map<String, Object> attributes, Member member) {
        this.attributes = attributes;
        this.member = member;
        this.provider = member.getProvider(); // provider 추가
        this.providerId = member.getProviderId(); // providerId 추가
        this.email = member.getEmail(); // email 추
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList("ROLE_USER");
    }

    @Override
    public String getName() {
        if (member == null) {
            return null;
        }
        return member.getEmail();
    }
}
