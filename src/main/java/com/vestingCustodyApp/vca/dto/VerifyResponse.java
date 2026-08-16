package com.vestingCustodyApp.vca.dto;

import com.vestingCustodyApp.vca.enums.Status;

public record VerifyResponse(boolean matches, String mediaName, Status status) {
}
