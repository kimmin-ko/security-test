package com.sp.security.basic;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class HomeController {

    @RequestMapping("/")
    public String home() {
        return "index";
    }

    @RequestMapping("/login")
    public String login(@RequestParam(defaultValue = "false") Boolean error,
                        Model model) {

        if(error)
            model.addAttribute("errorMessage", "아이디나 패스워드가 올바르지 않습니다.");

        return "login";
    }

//    @GetMapping("/hello")
//    public String hello() {
//        return "hello min";
//    }

}
