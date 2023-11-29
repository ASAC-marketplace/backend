package market.demo.domain.config;

import market.demo.dto.CustomOAuth2User;
import market.demo.service.OAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuth2UserService oAuth2UserService;

    public SecurityConfig(OAuth2UserService oAuth2UserService) {
        this.oAuth2UserService = oAuth2UserService;
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
    public AuthenticationSuccessHandler successHandler() {
        return ((request, response, authentication) -> {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
//            DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

            String id = customOAuth2User.getAttributes().get("id").toString();
            String body = """
                    {"id":"%s"}
                    """.formatted(id);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            PrintWriter writer = response.getWriter();
            writer.println(body);
            writer.flush();
        });
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
