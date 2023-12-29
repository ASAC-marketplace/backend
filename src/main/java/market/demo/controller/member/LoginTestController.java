//package market.demo.controller.member;
//
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.IOException;
//
//@Controller
//@RequestMapping("/login")
//public class LoginTestController {
//    @GetMapping
//    public void login(HttpServletResponse response) throws IOException {
//        response.sendRedirect("/oauth2/authorization/kakao");
//    }
//    @GetMapping("/verify")
//    public String verify() {
//        return "verifyPass";
//    }
//    @GetMapping("/add")
//    public String add() {
//        return "addform";
//    }
//}
