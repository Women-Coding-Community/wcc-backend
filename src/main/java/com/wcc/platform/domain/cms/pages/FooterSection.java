package com.wcc.platform.domain.cms.pages;

import com.wcc.platform.domain.cms.attributes.LabelLink;
import com.wcc.platform.domain.platform.SocialNetwork;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** CMS Footer section details. */
public record FooterSection(
    @NotBlank String id,
    @NotBlank String title,
    @NotBlank String subtitle,
    @NotBlank String description,
    @NotEmpty List<SocialNetwork> network,
    @NotNull LabelLink link) {}
