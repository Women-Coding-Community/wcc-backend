package com.wcc.platform.domain.cms.pages;

import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.platform.Event;
import java.util.List;

/**
 * Events page details
 *
 * @param events list of events
 * @param hero hero section to show banner
 * @param contact contact information to get more information about events
 */
public record EventsPage(List<Event> events, HeroSection hero, Contact contact) {}
