package com.wcc.platform.domain.cms.pages;

import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.platform.Event;
import java.util.List;

/**
 * Events page details
 *
 * @param events
 * @param hero
 * @param contact
 */
public record EventsPage(List<Event> events, HeroSection hero, Contact contact) {}
