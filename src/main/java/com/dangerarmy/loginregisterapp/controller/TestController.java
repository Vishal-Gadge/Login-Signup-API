package com.dangerarmy.loginregisterapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.util.HexFormat;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("")
    public String test(){
        System.out.println();
        System.out.println("test controller active on path /test ");

        byte[] bytes = new byte[8];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}
