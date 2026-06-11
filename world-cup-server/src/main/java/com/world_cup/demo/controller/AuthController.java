//package com.world_cup.demo.controller;
//
//import com.world_cup.demo.security.JwtUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/auth")
//public class AuthController {
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @PostMapping("/login")
//    public String login(@RequestParam Long userId) {
//        return jwtUtil.generateToken(userId);
//    }
//
////    Long userId = (Long) SecurityContextHolder
////            .getContext()
////            .getAuthentication()
////            .getPrincipal();
//}
