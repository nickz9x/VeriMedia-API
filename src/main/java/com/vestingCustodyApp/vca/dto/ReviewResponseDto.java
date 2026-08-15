package com.vestingCustodyApp.vca.dto;

import com.vestingCustodyApp.vca.enums.Status;

import java.time.Instant;

public record ReviewResponseDto(Long reviewId, Long media, Long reviewer, String observation, Status status, Instant reviewedAt) {
}
