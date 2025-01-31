package com.wcc.platform.domain.cms.pages.events;

import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.pages.Page;
import com.wcc.platform.domain.cms.pages.PageData;
import com.wcc.platform.domain.cms.pages.PageMetadata;
import com.wcc.platform.domain.platform.Event;

/** Events page details. */
public record EventsPage(
    PageMetadata metadata,
    HeroSection heroSection,
    Page page,
    Contact contact,
    PageData<Event> data) {}
