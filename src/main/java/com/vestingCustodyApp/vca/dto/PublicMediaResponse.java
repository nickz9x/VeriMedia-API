package com.vestingCustodyApp.vca.dto;

import com.vestingCustodyApp.vca.enums.MediaOrigin;
import com.vestingCustodyApp.vca.enums.MediaType;
import com.vestingCustodyApp.vca.enums.Status;

public record PublicMediaResponse(String fileName, MediaType mediaType, MediaOrigin mediaOrigin, Status status, String publicToken, Integer version, String hash) {
}
