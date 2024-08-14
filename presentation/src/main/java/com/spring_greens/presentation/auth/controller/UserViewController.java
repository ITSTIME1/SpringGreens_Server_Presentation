
package com.spring_greens.presentation.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring_greens.presentation.auth.dto.RetailSignupDTO;
import com.spring_greens.presentation.auth.dto.UserDTO;
import com.spring_greens.presentation.auth.dto.WholesaleSignupDTO;
import com.spring_greens.presentation.auth.service.UserService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@AllArgsConstructor
@Controller
public class UserViewController {
    private final UserService userService;

    @GetMapping("/")
    public String main() { return "main"; }

    @PostMapping("/login")
    public String login(@RequestBody UserDTO userDTO) {
        return "Login";
    }

    @PostMapping("/signup/retail")
    public String retailRegister(@RequestBody RetailSignupDTO retailSignupDTO) {
        //소매 회원가입
        userService.retailRegister(retailSignupDTO);
        return "retailRegister";
    }

    @PostMapping("/signup/wholesale")
    public String postMethodName(@RequestBody WholesaleSignupDTO wholesaleSignupDTO) {
        //도매 회원가입
        userService.wholesaleRegister(wholesaleSignupDTO);
        return"wholeRegister";
    }
}
