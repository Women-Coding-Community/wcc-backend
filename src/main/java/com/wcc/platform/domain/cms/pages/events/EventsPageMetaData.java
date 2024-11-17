package com.wcc.platform.domain.cms.pages.events;

import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.pages.Pagination;

/**
 * Events page metaData details.
 *
 * @param hero
 * @param contact
 */
public record EventsPageMetaData(Pagination pagination, HeroSection hero, Contact contact) {}
