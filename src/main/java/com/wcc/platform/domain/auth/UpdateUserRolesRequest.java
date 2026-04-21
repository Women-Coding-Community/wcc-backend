package com.wcc.platform.domain.auth;

import com.wcc.platform.domain.platform.type.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** Request DTO for updating roles on an existing user account. */
public record UpdateUserRolesRequest(
    @NotEmpty
        @Schema(
            description = "List of roles to assign. Must match enum names exactly.",
            example = "[\"CONTRIBUTOR\"]",
            allowableValues = {
              "ADMIN",
              "MENTORSHIP_ADMIN",
              "LEADER",
              "MENTEE",
              "MENTOR",
              "CONTRIBUTOR",
              "VIEWER"
            })
        List<RoleType> roles) {}
