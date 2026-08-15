package com.vestingCustodyApp.vca.dto;

import com.vestingCustodyApp.vca.enums.Role;

public record UserRequestDto(String login, String password, String email, Role role) {
}
