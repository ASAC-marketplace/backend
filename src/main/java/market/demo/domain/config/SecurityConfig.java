package market.demo.domain.config;

import lombok.RequiredArgsConstructor;
import market.demo.domain.member.Member;
import market.demo.domain.member.jwt.JwtAccessDeniedHandler;
import market.demo.domain.member.jwt.JwtAuthenticationEntryPoint;
import market.demo.domain.member.jwt.JwtSecurityConfig;
import market.demo.domain.member.jwt.TokenProvider;
import market.demo.dto.social.CustomOAuth2User;
import market.demo.repository.MemberRepository;
import market.demo.service.OAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.filter.CorsFilter;

import java.io.PrintWriter;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2UserService oAuth2UserService;
    private final TokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final MemberRepository memberRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .authorizeHttpRequests(authorizeRequests ->
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
                )
                .apply(new JwtSecurityConfig(tokenProvider));

        return http.build();
    }

    private LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            response.setStatus(HttpStatus.OK.value());
            PrintWriter writer = response.getWriter();
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
            if (memberOptional.isEmpty()) {
                response.sendRedirect("/login/add"); // 사용자가 없으면 회원가입 페이지로 리다이렉트
            } else {
                Member member = memberOptional.get();
                if (member.getProvider() != null) {
                    // 소셜 연동된 계정인 경우
                    response.sendRedirect("/main");
                } else {
                    // 연동 안된 경우
                    response.sendRedirect("/login/verify");
                }
            }
        });
    }
}
