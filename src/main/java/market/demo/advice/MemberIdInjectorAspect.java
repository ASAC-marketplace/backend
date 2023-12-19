package market.demo.advice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.member.jwt.TokenProvider;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class MemberIdInjectorAspect {
    private final TokenProvider tokenProvider;

    @Around("execution(* market.demo.controller..*.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // JWT 토큰에서 memberId 추출
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String authHeader = request.getHeader("Authorization");
        Long memberId = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            memberId = tokenProvider.getMemberIdFromToken(token);
        }

        // 메소드 파라미터 조정
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof MemberIdAwardDto) {
                MemberIdAwardDto dto = (MemberIdAwardDto) args[i];
                if (memberId != null) {
                    dto.setMemberId(memberId);
                }
                args[i] = dto;
            }
        }

        // 원래 메소드 실행
        return joinPoint.proceed(args);
    }
}
