package market.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/login")
public class LoginTestController {
    @GetMapping
    public String login() {
        return "login";
    }
    @GetMapping("/verify")
    public String verify() {
        return "verifyPass";
    }
    @GetMapping("/add")
    public String add() {
        return "addform";
    }
}
