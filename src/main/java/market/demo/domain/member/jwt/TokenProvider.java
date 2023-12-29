package market.demo.domain.member.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import market.demo.dto.social.CustomOAuth2User;
import market.demo.service.jwt.CustomUserDetail;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TokenProvider implements InitializingBean {

    private static final String AUTHORITIES_KEY = "auth";
    private final String secret;
    private final long tokenValidityInMilliseconds;
    private Key key;

    // 생성자를 통해 JWT 비밀키와 토큰 유효시간을 주입받음
    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds) {
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
    }

    // 빈 초기화 시 비밀키로부터 Key 인스턴스를 생성
    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 인증 정보를 기반으로 JWT 생성
    public String createToken(Authentication authentication) {
        // 인증된 사용자의 권한 정보를 문자열로 변환
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Long memberId = customOAuth2User.getMemberId();
        String loginId = customOAuth2User.getLoginId(); // loginId 추가

        log.info("memberId = {}", memberId);

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        // JWT 토큰 생성 및 반환
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("memberId", memberId)
                .claim("loginId", loginId)
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    // 인증 정보를 기반으로 JWT 생성
    public String createTokenNormal(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        CustomUserDetail customUserDetail = (CustomUserDetail)  authentication.getPrincipal();
        String email = customUserDetail.getEmail(); // 이메일 가져오기

        Long memberId = customUserDetail.getMemberId();
        String loginId = customUserDetail.getUsername();

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        // JWT 토큰 생성 및 반환
        return Jwts.builder()
                .setSubject(email)
                .claim("memberId", memberId)
                .claim("loginId", loginId)
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    // JWT로부터 인증 정보를 추출
    public Authentication getAuthentication(String token) {
        // 토큰 검증 및 클레임 추출
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // 클레임으로부터 권한 정보 추출
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // 사용자 인증 정보 생성 및 반환
        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // 토큰의 유효성 검증
    public boolean validateToken(String token) {
        try {
            // 토큰 파싱 및 유효성 검증
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    public Long getMemberIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        Long memberId = claims.get("memberId", Long.class);
        log.info("Extracted 선림 from token: {}", memberId);

        return claims.get("memberId", Long.class);
    }

    public String getLoginIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("loginId", String.class); // loginId 반환
    }

    public String getLoginIdFromCurrentRequest() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                return getLoginIdFromToken(token);
            }
        }
        return null;
    }

    public Long getMemberIdFromCurrentRequest() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                return getMemberIdFromToken(token);
            }
        }
        return null;
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }
}
