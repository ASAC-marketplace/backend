package market.demo.controller.member;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import market.demo.domain.member.jwt.TokenProvider;
import market.demo.dto.social.CustomOAuth2User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class Oauth2Controller {
    private final TokenProvider tokenProvider;

    @GetMapping("/oauth2/redirect/no")
    public void handleOAuth2Redirect(HttpServletResponse response,
                                     @AuthenticationPrincipal CustomOAuth2User customOAuth2User) throws IOException {
//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(
//                        customOAuth2User, null, customOAuth2User.getAuthorities());
//        // 토큰 생성
//        String token = tokenProvider.createToken(authenticationToken);
//        Cookie cookie = new Cookie("AUTH_TOKEN", token);
//        cookie.setHttpOnly(true); // 자바스크립트 접근 방지
//        cookie.setSecure(true);   // HTTPS 통신에서만 전송
//        cookie.setPath("/");      // 모든 경로에서 쿠키 사용
//        response.addCookie(cookie);
//
//
//        // 프론트엔드 페이지로 리다이렉트 (토큰을 쿼리 파라미터 또는 쿠키로 전달)
        response.sendRedirect("http://localhost:3000/login/social-register");
    }

    @GetMapping("/oauth2/redirect/yes")
    public void handleOAuth2RedirectYes(HttpServletResponse response,
                                     @AuthenticationPrincipal CustomOAuth2User customOAuth2User) throws IOException {
        response.sendRedirect("http://localhost:3000/login/connect");
    }

    @GetMapping("/oauth2/redirect/full")
    public void handleOAuth2RedirectFull(HttpServletResponse response,
                                        @AuthenticationPrincipal CustomOAuth2User customOAuth2User) throws IOException {
        response.sendRedirect("http://localhost:3000/recommendations");
    }
}
