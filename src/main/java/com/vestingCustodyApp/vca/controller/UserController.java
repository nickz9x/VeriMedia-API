package com.vestingCustodyApp.vca.controller;

import com.vestingCustodyApp.vca.dto.UserRequestDto;
import com.vestingCustodyApp.vca.entity.User;
import com.vestingCustodyApp.vca.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class UserController {
    private UserService service;


    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserRequestDto data){
        return ResponseEntity.ok(service.save(data));
    }

}
