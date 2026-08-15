package com.vestingCustodyApp.vca.dto;

import java.time.Instant;

public record RequestReviewMediaDto(  Long id, Instant requestedReviewDate,  Long mediaId,  String reason) {
}
