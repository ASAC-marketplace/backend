package market.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.Member;
import market.demo.dto.social.CustomOAuth2User;
import market.demo.repository.MemberRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

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
        String providerId = String.valueOf(attributes.get("id")); // 고유 ID 추출
//        String name = (String) attributes.get("name"); // 이름 추출
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

        // 이메일로 기존 회원 검사
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            member.updateSocialLoginInfo(provider, providerId);

            // 기존 회원이 있는 경우에는 CustomOAuth2User 객체를 생성하지 않고 기존 회원 정보를 이용
            CustomOAuth2User customOAuth2User = new CustomOAuth2User(attributes, member);
            log.info("CustomOAuth2User 생성: 기존 회원, provider={}, providerId={}", provider, providerId);
            return customOAuth2User;
        } else {
            // 기존 회원이 없는 경우에만 새로운 CustomOAuth2User 객체 생성
            Member member = new Member(email);
            CustomOAuth2User customOAuth2User = new CustomOAuth2User(attributes, member);
            customOAuth2User.setEmail(email); // 이메일 설정
            customOAuth2User.setProvider(provider); // provider 설정
            customOAuth2User.setProviderId(providerId); // providerId 설정

            log.info("CustomOAuth2User 생성: 신규 회원,email={}, provider={}, providerId={}",email, provider, providerId);
            log.info("member 생성: member={}",member.getId());
            return customOAuth2User;
        }
    }
}
