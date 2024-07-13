package com.wcc.platform.domain.cms.pages;

import com.wcc.platform.domain.cms.attributes.Event;
import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.attributes.PageSection;
import java.util.List;

public record EventsPage (List<Event> events, HeroSection hero, PageSection pageSection){

}
