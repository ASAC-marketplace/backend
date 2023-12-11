package market.demo.service.oauth2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.member.Member;
import market.demo.dto.social.CustomOAuth2User;
import market.demo.exception.InvalidEmailException;
import market.demo.repository.MemberRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 사용자 정보 추출
        Map<String, Object> attributes = oAuth2User.getAttributes();
        // 객체
        String providerId = String.valueOf(attributes.get("id")); // 고유 ID 추출
        String provider = userRequest.getClientRegistration().getRegistrationId();
        log.info("OAuth2User attributes: {}", attributes);

        // 이메일 추출
        String email = extractEmail(attributes);

        // 데이터베이스에서 사용자 조회 또는 새 사용자 생성
        Member member = memberRepository.findByEmail(email)
                .orElse(new Member(email));

        // 소셜 로그인 정보 업데이트 (기존 회원이든 신규 회원이든 동일하게 적용)
        member.updateSocialLoginInfo(provider, providerId);

        // CustomOAuth2User 객체 생성 및 반환
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(attributes, member);
        customOAuth2User.setEmail(email);
        customOAuth2User.setProvider(provider);
        customOAuth2User.setProviderId(providerId);

        log.info("CustomOAuth2User 생성: email={}, provider={}, providerId={}", email, provider, providerId);
        return customOAuth2User;
    }

    private String extractEmail(Map<String, Object> attributes) {
        if (attributes.containsKey("kakao_account")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            String email = (String) kakaoAccount.get("email");
            if (StringUtils.hasLength(email)) {
                return email;
            }
        }
        throw new InvalidEmailException("유효한 이메일 주소를 찾을 수 없습니다.");
    }
}
