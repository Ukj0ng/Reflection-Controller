package com.uk.reflection.controller;

import com.uk.reflection.anno.RequestMapping;

public class UserController {

    @RequestMapping("/join")
    public String join() {
        return "join.html";
    }

    @RequestMapping("/login")
    public String login() {
        return "login.html";
    }

    @RequestMapping("/user")
    public String user() {
        return "user.html";
    }

    @RequestMapping("/hello")
    public String hello() {
        return "hello.html";
    }
}
