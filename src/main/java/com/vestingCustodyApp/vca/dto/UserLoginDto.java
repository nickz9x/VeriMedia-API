package com.vestingCustodyApp.vca.dto;

import jakarta.validation.constraints.NotBlank;

public record UserLoginDto(@NotBlank String login, @NotBlank String password) {
}
