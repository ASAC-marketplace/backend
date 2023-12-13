package market.demo.domain.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.member.Member;
import market.demo.domain.member.jwt.JwtAccessDeniedHandler;
import market.demo.domain.member.jwt.JwtAuthenticationEntryPoint;
import market.demo.domain.member.jwt.JwtSecurityConfig;
import market.demo.domain.member.jwt.TokenProvider;
import market.demo.dto.social.CustomOAuth2User;
import market.demo.repository.MemberRepository;
import market.demo.service.oauth2.OAuth2UserService;
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
@Slf4j
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
            log.info("현재 인증 정보: {}", authentication);
            String email = customOAuth2User.getEmail();
            String provider = customOAuth2User.getProvider();
            String providerId = customOAuth2User.getProviderId();

            MemberInfoSocialDto1 memberInfoSocialDto1 = new MemberInfoSocialDto1();
            memberInfoSocialDto1.setEmail(email);
            memberInfoSocialDto1.setProvider(provider);
            memberInfoSocialDto1.setProviderId(providerId);

//            String jwt = tokenProvider.createToken(authentication);
//            response.addHeader("Authorization", "Bearer " + jwt);

            // 데이터베이스에서 사용자 조회
            Optional<Member> memberOptional = memberRepository.findByEmail(email);
            if (memberOptional.isEmpty()) {
                String jsonMemberInfo1 = new ObjectMapper().writeValueAsString(memberInfoSocialDto1);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(jsonMemberInfo1);
//                response.sendRedirect("/login/add"); // 사용자가 없으면 회원가입 페이지로 리다이렉트
            } else {
                Member member = memberOptional.get();
                if (member.getProvider() != null) {
                    // 소셜 연동된 계정인 경우
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    String jsonMemberInfo2 = new ObjectMapper().writeValueAsString(memberInfoSocialDto1);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(jsonMemberInfo2);
                    // 연동 안된 경우
//                    response.sendRedirect("/login/verify");
                }
            }
        });
    }
//    public AuthenticationSuccessHandler successHandler() {
//        return (request, response, authentication) -> {
//            CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
//            String email = customOAuth2User.getEmail();
//
//            // 데이터베이스에서 사용자 조회
//            Optional<Member> memberOptional = memberRepository.findByEmail(email);
//            Member member = memberOptional.orElseGet(() -> new Member(email)); // 사용자가 없는 경우 기본값 설정
//
//            // Authentication 객체 생성
//            Authentication newAuth = new UsernamePasswordAuthenticationToken(
//                    member.getLoginId(), member.getPassword(), getAuthorities(member)
//            );
//            SecurityContextHolder.getContext().setAuthentication(newAuth);
//
//            // JWT 토큰 생성
//            String jwt = tokenProvider.createToken(newAuth);
//
//            // 클라이언트에게 반환할 상태 설정
//            String status;
//            if (memberOptional.isEmpty()) {
//                status = "NEW_USER";
//            } else if (member.getProvider() != null) {
//                status = "LINKED_WITH_SOCIAL";
//            } else {
//                status = "NOT_LINKED_WITH_SOCIAL";
//            }
//
//            // 클라이언트에 상태와 토큰 반환
//            response.setContentType("application/json;charset=UTF-8");
//            PrintWriter writer = response.getWriter();
//            writer.print("{\"status\":\"" + status + "\", \"token\":\"" + jwt + "\"}");
//            writer.flush();
//        };
//    }
//
//    private Collection<GrantedAuthority> getAuthorities(Member member) {
//        // 사용자의 권한에 따라 변경할 수 있습니다.
//        // 예를 들어, member 객체에 권한 목록이 있다면 해당 목록을 사용하여 GrantedAuthority 목록을 만듭니다.
//        List<GrantedAuthority> authorities = new ArrayList<>();
//        // 예시로 "ROLE_USER" 권한을 추가합니다. 실제 구현에서는 member의 권한에 따라 다를 수 있습니다.
//        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
//        return authorities;
//    }
}
