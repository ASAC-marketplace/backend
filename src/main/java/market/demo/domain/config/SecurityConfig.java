package market.demo.domain.config;

import market.demo.domain.Member;
import market.demo.dto.social.CustomOAuth2User;
import market.demo.repository.MemberRepository;
import market.demo.service.OAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuth2UserService oAuth2UserService;

    private final MemberRepository memberRepository;

    public SecurityConfig(OAuth2UserService oAuth2UserService, MemberRepository memberRepository) {
        this.oAuth2UserService = oAuth2UserService;
        this.memberRepository = memberRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrfConfig) -> csrfConfig.disable())
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests.anyRequest().permitAll())
                .oauth2Login(oauth2Login -> oauth2Login
                                .loginPage("/login")
                                .successHandler(successHandler())
                                .userInfoEndpoint(userInfo -> userInfo
                                        .userService(oAuth2UserService)
                                )
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login") //로그아웃 성공 후 리다이렉트될 URL
                        .logoutSuccessHandler(logoutSuccessHandler()) //로그아웃 성공 핸들러
                        .invalidateHttpSession(true) //HTTP 세션 무효화
                        .deleteCookies("JSESSIONID") //쿠키 삭제
                        .clearAuthentication(true) // 인증 정보 클리어
                );

        return http.build();
    }

    private LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            response.setStatus(HttpStatus.OK.value());
            PrintWriter writer =response.getWriter();
            writer.println("logout");
            writer.flush();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    public AuthenticationSuccessHandler successHandler() {
        return ((request, response, authentication) -> {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            String email = customOAuth2User.getEmail();

            // 데이터베이스에서 사용자 조회
            Optional<Member> memberOptional = memberRepository.findByEmail(email);

            String body;
            if (memberOptional.isPresent()) {
                Member member = memberOptional.get();
                if (member.getProvider() != null) {
                    // 소셜 연동된 계정인 경우
                    response.sendRedirect("/main");
                } else {
                    // 이메일은 같지만 소셜 연동되지 않은 경우
                    response.sendRedirect("/login/verify");
                }
            } else {
                // 계정이 없는 경우
                response.sendRedirect("/login/add");
            }
        });
    }
}
