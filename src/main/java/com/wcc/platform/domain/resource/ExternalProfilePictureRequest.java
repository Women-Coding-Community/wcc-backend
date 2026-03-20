package com.wcc.platform.domain.resource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

/**
 * Request DTO for setting a member's profile picture from an external URL.
 *
 * @param memberId the unique identifier of the member
 * @param externalUrl the publicly accessible URL of the profile picture
 */
public record ExternalProfilePictureRequest(
    @NotNull Long memberId, @NotBlank @URL String externalUrl) {}
