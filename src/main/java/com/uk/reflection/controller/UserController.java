package com.uk.reflection.controller;

import com.uk.reflection.anno.RequestMapping;
import com.uk.reflection.controller.dto.JoinDto;
import com.uk.reflection.controller.dto.LoginDto;
import com.uk.reflection.model.User;

public class UserController {

    @RequestMapping("/user/join")
    public String join(JoinDto dto) {  // username, password, email
        System.out.println("JoinDto: " + dto.toString());
        return "join.html";
    }

    @RequestMapping("/user/login")
    public String login(LoginDto dto) { // username, password
        System.out.println("LoginDto: " + dto.toString());
        return "login.html";
    }

    @RequestMapping("/user/list")
    public String user(User user) {
        System.out.println("User: " + user.toString());
        return "user.html";
    }

    @RequestMapping("/hello")
    public String hello() {
        return "hello.html";
    }
}
