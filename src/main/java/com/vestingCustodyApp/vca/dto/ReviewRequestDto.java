package com.vestingCustodyApp.vca.dto;

import com.vestingCustodyApp.vca.enums.Status;
import jakarta.validation.constraints.NotNull;

public record ReviewRequestDto(@NotNull Long mediaId, String observation, @NotNull Status status) {
}
