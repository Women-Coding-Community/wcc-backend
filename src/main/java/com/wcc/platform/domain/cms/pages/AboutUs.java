package com.wcc.platform.domain.cms.pages;

import com.wcc.platform.domain.cms.attributes.Contact;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

// TODO: Get photo for About Us hero section.
// TODO: Use Page or HeroSection record?
// TODO: Add id to the below record declaration?
// TODO: Add description after title to Contact record (to be able to add the description from the
// Figma About Us contact example)?
public record AboutUs(
    @NotNull Page heroSection, @NotEmpty List<Section<String>> items, @NotEmpty Contact contact) {}
