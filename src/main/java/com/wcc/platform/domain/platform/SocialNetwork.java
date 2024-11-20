package com.wcc.platform.domain.platform;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/** Pojo of social network data of member, event or programme. */
public record SocialNetwork(
    @NotNull SocialNetworkType type,
    @Pattern(regexp = "^(https?)://[^\\s/$.?#].[^\\s]*$") @NotBlank String link) {}
