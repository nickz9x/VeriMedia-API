package com.vestingCustodyApp.vca.dto;

import com.vestingCustodyApp.vca.enums.MediaOrigin;
import com.vestingCustodyApp.vca.enums.MediaType;

public record MediaRegisterRequestDto(MediaOrigin origin, MediaType type,String tool,String purpose) {
}
