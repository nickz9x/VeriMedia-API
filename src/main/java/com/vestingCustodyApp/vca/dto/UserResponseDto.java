package com.vestingCustodyApp.vca.dto;

import com.vestingCustodyApp.vca.enums.Role;

public record UserResponseDto(Long id, String login, String email, Role role) {
}
