package com.mycom.myapp.naviya.global.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/login")
    public String loginP() {

        return "login";
    }

    @GetMapping("/signup")
    public String signupP() {

        return "signup";
    }

    @GetMapping("/usermypage")
    public String userP() {

        return "userpage";
    }






}
