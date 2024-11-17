package com.wcc.platform.domain.cms.pages.events;

import com.wcc.platform.domain.cms.pages.PageData;
import com.wcc.platform.domain.platform.Event;

/**
 * Events page details.
 *
 * @param metadata metadata for the page
 * @param data events page data
 */
public record EventsPage(EventsPageMetaData metadata, PageData<Event> data) {}
