package com.vestingCustodyApp.vca.dto;

import com.vestingCustodyApp.vca.enums.MediaOrigin;
import com.vestingCustodyApp.vca.enums.MediaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MediaRegisterRequestDto(@NotNull MediaOrigin origin,@NotNull MediaType type, String tool, @NotBlank String purpose) {
}
