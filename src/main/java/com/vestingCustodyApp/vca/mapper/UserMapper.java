package com.vestingCustodyApp.vca.mapper;

import com.vestingCustodyApp.vca.dto.UserResponseDto;
import com.vestingCustodyApp.vca.entity.User;

public class UserMapper {
    public static UserResponseDto toResponseDto(User user){
        return new UserResponseDto(user.getId(),user.getLogin(), user.getEmail(), user.getRole());
    }
}
