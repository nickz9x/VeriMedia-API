package com.vestingCustodyApp.vca.dto;

import com.vestingCustodyApp.vca.entity.Media;
import com.vestingCustodyApp.vca.entity.User;
import com.vestingCustodyApp.vca.enums.Status;

public record ReviewRequestDto(Long mediaId, String observation, Status status) {
}
