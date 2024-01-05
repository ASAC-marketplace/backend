//package market.demo.advice;
//
//import lombok.extern.slf4j.Slf4j;
//import market.demo.dto.social.CustomOAuth2User;
//import market.demo.dto.social.MemberRegistrationDto;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//
//@Aspect
//@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
//@Slf4j
//public class CustomOAuth2UserAspect {
//
//    @Around("execution(* market.demo.controller..*.*(..))")
//    public Object aroundRegisterMember(ProceedingJoinPoint joinPoint) throws Throwable {
//        // 시큐리티 컨텍스트에서 CustomOAuth2User 정보 가져오기
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        CustomOAuth2User customOAuth2User = null;
//        if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User) {
//            customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
//        }
//
//        Object[] args = joinPoint.getArgs();
//        if (args.length > 0 && args[0] instanceof MemberRegistrationDto) {
//            MemberRegistrationDto request = (MemberRegistrationDto) args[0];
//            if (customOAuth2User != null) {
//                // DTO 필드 수정
//                request.setProviderEmail(customOAuth2User.getEmail());
//                System.out.println("선림 = {}");
//                log.info("선림 = {}", customOAuth2User.getMemberId());
//                request.setProvider(customOAuth2User.getProvider());
//                request.setProviderId(customOAuth2User.getProviderId());
//            }
//        }
//
//        // 원래의 메소드 실행
//        return joinPoint.proceed(args);
//    }
//}
