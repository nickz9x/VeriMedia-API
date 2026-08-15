package com.vestingCustodyApp.vca.dto;

import com.vestingCustodyApp.vca.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequestDto(@NotBlank String login, @NotBlank String password, @Email @NotBlank String email, @NotNull Role role) {
}
