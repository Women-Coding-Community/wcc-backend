package com.wcc.platform.domain.platform.mentorship;

import jakarta.validation.constraints.Size;

/**
 * Request DTO for mentee withdrawing an application.
 */
public record ApplicationWithdrawRequest(
    @Size(max = 500, message = "Withdrawal reason cannot exceed 500 characters")
    String reason
) {
}
