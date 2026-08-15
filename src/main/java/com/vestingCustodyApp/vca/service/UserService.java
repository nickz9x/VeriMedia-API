package com.vestingCustodyApp.vca.service;

import com.vestingCustodyApp.vca.dto.UserRequestDto;
import com.vestingCustodyApp.vca.entity.User;
import com.vestingCustodyApp.vca.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private UserRepository repository;
    private PasswordEncoder encoder;

    public User save(UserRequestDto data){
        if (repository.findByLogin(data.login()).isPresent() || repository.findByEmail(data.email()).isPresent())
        {

        }
        User user = new User();
        user.setLogin(data.login());
        user.setPassword(encoder.encode(data.password()));
        user.setEmail(data.email());
        user.setRole(data.role());
        return repository.save(user);
    }
}
