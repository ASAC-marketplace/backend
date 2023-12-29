package market.demo.controller.member;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthyCheckController {
    @GetMapping("/")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("");
    }
}
