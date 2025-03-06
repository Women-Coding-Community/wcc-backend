package com.wcc.platform.domain.cms.pages.aboutus;

import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.attributes.ListSection;
import com.wcc.platform.domain.platform.Partner;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** CMS Partners page. */
public record PartnersPage(
    @NotBlank String id,
    @NotNull HeroSection heroSection,
    @NotNull ListSection<String> introSection,
    @NotNull Contact contact,
    ListSection<Partner> partners) {}
