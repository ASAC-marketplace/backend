package market.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.Member;
import market.demo.dto.CustomOAuth2User;
import market.demo.repository.MemberRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        String providerId = String.valueOf(attributes.get("id")); // 고유 ID 추출
        String name = (String) attributes.get("name"); // 이름 추출
        String provider = userRequest.getClientRegistration().getRegistrationId();
        log.info("OAuth2User attributes: {}", attributes);

        // 이메일 추출
        String email;
        if (attributes.containsKey("kakao_account")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            email = (String) kakaoAccount.get("email");
        } else {
            // 이메일이 없는 경우의 처리, 예를 들어 기본값 설정 또는 예외 발생
            email = "default_email@example.com"; // 또는 적절한 예외 처리
        }

        // 이메일로 기존 회원 검사 또는 새로 등록
        Member member = memberRepository.findByEmail(email)
                .orElseGet(() -> {
                    // 사용자가 존재하지 않으면 새로 생성
                    Member newMember = new Member(email, name, providerId, provider);
                    memberRepository.save(newMember);
                    return newMember;
                });

        // 기존 회원일 경우, 소셜 로그인 정보 업데이트
        if (memberRepository.findByEmail(email).isPresent()) {
            member.updateSocialLoginInfo(provider, providerId);
            memberRepository.save(member);
        }

        // CustomOAuth2User 객체 반환
        return new CustomOAuth2User(attributes, member);
    }
}
