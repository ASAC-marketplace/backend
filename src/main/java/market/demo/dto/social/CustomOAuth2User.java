package market.demo.dto.social;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.Member;
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
    @Getter
    @Setter
    private final Member existingMember;


    private String provider;
    private String providerId;
    private String email;

    public CustomOAuth2User(Map<String, Object> attributes, Member existingMember) {
        this.attributes = attributes;
        this.existingMember = existingMember;
        // existingMember의 null 여부 체크 및 로깅
        if (existingMember == null) {
            this.provider = existingMember.getProvider(); // provider 추가
            this.providerId = existingMember.getProviderId(); // providerId 추가
            this.email = existingMember.getEmail(); // email 추
            log.error("CustomOAuth2User 생성 실패: existingMember가 null입니다.");
        } else {
            this.provider = existingMember.getProvider(); // provider 추가
            this.providerId = existingMember.getProviderId(); // providerId 추가
            this.email = existingMember.getEmail(); // email 추성
            log.info("CustomOAuth2User 생성 성공: Member Info: {}", existingMember);


            // provider와 providerId 로깅
            log.info("CustomOAuth2User 생성: provider={}, providerId={}", provider, providerId);
            log.info("CustomOAuth2User 생성 성공: Member Info: {}", existingMember);
        }
    }


    @Override
    public Map<String, Object> getAttributes() {
        log.info("OAuth2User Attributes 호출: {}", attributes);
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        log.info("OAuth2User Authorities 호출");

        return AuthorityUtils.createAuthorityList("ROLE_USER");
    }

    @Override
    public String getName() {
        if (existingMember == null) {
            log.error("OAuth2User Name 호출 실패: existingMember가 null입니다.");
            return null;
        } else {
            log.info("OAuth2User Name 호출: {}", existingMember.getEmail());
            return existingMember.getEmail(); // 또는 다른 식별자
        }
    }
}
