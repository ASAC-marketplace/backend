package market.demo.advice;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class ResponseEntityToJsonAspect {

    @Around("execution(* market.demo.controller..*.*(..)) && @within(org.springframework.web.bind.annotation.RestController)")
    public Object convertToJson(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        if (result instanceof ResponseEntity) {
            ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
            Object body = responseEntity.getBody();

            if (body instanceof String) {
                // 단순 문자열을 JSON 형식으로 변환
                Map<String, String> jsonBody = new HashMap<>();
                jsonBody.put("msg", (String) body);
                return ResponseEntity.status(responseEntity.getStatusCode()).body(jsonBody);
            } else if (!(body instanceof Map)) {
                // 기타 객체들을 JSON 형식으로 변환
                Map<String, Object> jsonBody = new HashMap<>();
                jsonBody.put("data", body);
                return ResponseEntity.status(responseEntity.getStatusCode()).body(jsonBody);
            }
        }
        return result;
    }
}
