package com.vestingCustodyApp.vca.controller;

import com.vestingCustodyApp.vca.dto.LoginResponseDto;
import com.vestingCustodyApp.vca.dto.UserLoginDto;
import com.vestingCustodyApp.vca.dto.UserRequestDto;
import com.vestingCustodyApp.vca.entity.User;
import com.vestingCustodyApp.vca.service.TokenService;
import com.vestingCustodyApp.vca.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class UserController {
    private UserService service;
    private TokenService tokenService;
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserRequestDto data){
        return ResponseEntity.ok(service.save(data));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody UserLoginDto data){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(data.Login(),data.password());
        Authentication auth = authenticationManager.authenticate(authenticationToken);
        User byLogin = service.findByLogin(auth.getName());
        String token = tokenService.generateToken(byLogin);
        return ResponseEntity.ok(new LoginResponseDto(token));
    }

}
