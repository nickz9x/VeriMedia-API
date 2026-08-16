package com.vestingCustodyApp.vca.service;

import com.vestingCustodyApp.vca.dto.UserRequestDto;
import com.vestingCustodyApp.vca.dto.UserResponseDto;
import com.vestingCustodyApp.vca.entity.User;
import com.vestingCustodyApp.vca.enums.Role;
import com.vestingCustodyApp.vca.mapper.UserMapper;
import com.vestingCustodyApp.vca.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class UserService {
    private UserRepository repository;
    private PasswordEncoder encoder;

    public UserResponseDto save(UserRequestDto data){
        if (repository.findByLogin(data.login()).isPresent() || repository.findByEmail(data.email()).isPresent())
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"this user already exists");
        }
        User user = new User();
        user.setLogin(data.login());
        user.setPassword(encoder.encode(data.password()));
        user.setEmail(data.email());
        user.setRole(Role.CREATOR);

        return UserMapper.toResponseDto(repository.save(user));
    }

    public User findByLogin(String login){
        return repository.findByLogin(login).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
    }

    public UserDetails findByLoginDetails(String login){
        User user = repository.findByLogin(login).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getLogin())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
