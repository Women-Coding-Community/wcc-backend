package com.wcc.platform.domain.cms.pages;

import com.wcc.platform.domain.cms.attributes.Contact;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record AboutUsPage(
    @NotNull Page heroSection, @NotEmpty List<Section<String>> items, @NotEmpty Contact contact) {}
