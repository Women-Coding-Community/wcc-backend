package com.wcc.platform.domain.cms.pages.events;

import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.pages.Page;
import com.wcc.platform.domain.cms.pages.PageData;
import com.wcc.platform.domain.cms.pages.PageMetadata;
import com.wcc.platform.domain.platform.Event;
import jakarta.validation.constraints.NotNull;

/** Events page details. */
public record EventsPage(
    @NotNull String id,
    @NotNull PageMetadata metadata,
    @NotNull HeroSection heroSection,
    @NotNull Page page,
    @NotNull Contact contact,
    @NotNull PageData<Event> data) {}
