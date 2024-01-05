package market.demo.controller.member;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.member.jwt.JwtFilter;
import market.demo.domain.member.jwt.TokenProvider;
import market.demo.dto.social.CustomOAuth2User;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@Slf4j
public class Oauth2Controller {
    private final TokenProvider tokenProvider;

//    @GetMapping("/oauth2/redirect/no")
//    public ResponseEntity<?> handleOAuth2Redirect(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
//        // 인증 토큰 생성
//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(
//                        customOAuth2User, null, customOAuth2User.getAuthorities());
//
//        String token = tokenProvider.createToken(authenticationToken);
//
//        // HTTP 헤더 설정
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + token);
//
//        // ResponseEntity를 통해 JWT 토큰을 클라이언트에 반환
//        return new ResponseEntity<>(httpHeaders, HttpStatus.OK);
//    }

    @GetMapping("/oauth2/redirect/no")
    public void handleOAuth2Redirect(HttpServletResponse response,
                                     @AuthenticationPrincipal CustomOAuth2User customOAuth2User) throws IOException {
        Cookie emailCookie = new Cookie("providerEmail", customOAuth2User.getEmail());
        Cookie providerCookie = new Cookie("provider", customOAuth2User.getProvider());
        Cookie providerIdCookie = new Cookie("providerId", customOAuth2User.getProviderId());
//
//        emailCookie.setHttpOnly(true);
//        emailCookie.setSecure(true); // HTTPS를 통해서만 쿠키 전송
//        emailCookie.setDomain("localhost");
        emailCookie.setPath("/");
////        emailCookie.setSameSite("None"); // 크로스 사이트 요청에서도 쿠키 전송
//
//        providerCookie.setHttpOnly(true);
//        providerCookie.setSecure(true);
//        providerCookie.setDomain("localhost");
        providerCookie.setPath("/");
////        providerCookie.setSameSite("None");
//
//        providerIdCookie.setHttpOnly(true);
//        providerIdCookie.setSecure(true);
//        providerIdCookie.setDomain("localhost");
        providerIdCookie.setPath("/");
////        providerIdCookie.setSameSite("None");
//

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        customOAuth2User, null, customOAuth2User.getAuthorities());

        String token = tokenProvider.createToken(authenticationToken);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + token);

        response.addCookie(emailCookie);
        response.addCookie(providerCookie);
        response.addCookie(providerIdCookie);

        String redirectUrl = "http://localhost:3000/login/social-register?" +
                "providerEmail=" + URLEncoder.encode(customOAuth2User.getEmail(), StandardCharsets.UTF_8.name()) +
                "&provider=" + URLEncoder.encode(customOAuth2User.getProvider(), StandardCharsets.UTF_8.name()) +
                "&providerId=" + URLEncoder.encode(customOAuth2User.getProviderId(), StandardCharsets.UTF_8.name()) +
                "&jwt=" + URLEncoder.encode(token);

        response.sendRedirect(redirectUrl);
//        response.sendRedirect("http://localhost:3000/login/social-register");
    }

    @GetMapping("/oauth2/redirect/yes")
    public void handleOAuth2RedirectYes(HttpServletResponse response,
                                     @AuthenticationPrincipal CustomOAuth2User customOAuth2User) throws IOException {
        Cookie emailCookie = new Cookie("providerEmail", customOAuth2User.getEmail());
        Cookie providerCookie = new Cookie("provider", customOAuth2User.getProvider());
        Cookie providerIdCookie = new Cookie("providerId", customOAuth2User.getProviderId());
//
//        // 쿠키 옵션 설정 (예: 유효 시간, 경로 등)
////        emailCookie.setHttpOnly(true); // 자바스크립트 접근 방지
////        emailCookie.setSecure(true);
        emailCookie.setPath("/");
        providerCookie.setPath("/");
        providerIdCookie.setPath("/");

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        customOAuth2User, null, customOAuth2User.getAuthorities());

        String token = tokenProvider.createToken(authenticationToken);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + token);
//
        response.addCookie(emailCookie);
        response.addCookie(providerCookie);
        response.addCookie(providerIdCookie);
//        response.sendRedirect("http://localhost:3000/login/connect");
        String redirectUrl = "http://localhost:3000/login/connect?" +
                "providerEmail=" + URLEncoder.encode(customOAuth2User.getEmail(), StandardCharsets.UTF_8.name()) +
                "&provider=" + URLEncoder.encode(customOAuth2User.getProvider(), StandardCharsets.UTF_8.name()) +
                "&providerId=" + URLEncoder.encode(customOAuth2User.getProviderId(), StandardCharsets.UTF_8.name()) +
                "&jwt=" + URLEncoder.encode(token);

        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/oauth2/redirect/full")
    public void handleOAuth2RedirectFull(HttpServletResponse response,
                                        @AuthenticationPrincipal CustomOAuth2User customOAuth2User) throws IOException {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        customOAuth2User, null, customOAuth2User.getAuthorities());

        String token = tokenProvider.createToken(authenticationToken);
        String redirectUrl = "http://localhost:3000/?" +
                "authToken=" + URLEncoder.encode(token, StandardCharsets.UTF_8.name());

        response.sendRedirect(redirectUrl);
    }
}
