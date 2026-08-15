package com.vestingCustodyApp.vca.dto;

import com.vestingCustodyApp.vca.entity.User;
import com.vestingCustodyApp.vca.enums.MediaOrigin;
import com.vestingCustodyApp.vca.enums.MediaType;
import com.vestingCustodyApp.vca.enums.Status;
import lombok.NoArgsConstructor;

public record MediaResponseDto(Long id, Long user, String mediaName, MediaType mediaType, MediaOrigin origin, String tool, String purpose,
                               Status status,String hash) {
}
