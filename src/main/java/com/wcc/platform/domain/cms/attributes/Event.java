package com.wcc.platform.domain.cms.attributes;

import java.util.List;

/**
 * Record for a single event
 */
public record Event(String topic, Category category, String startDate, String endDate, String title,
                    String speaker,
                    String description, Image image, SimpleLink link,
                    List<EventResource> eventResources) {

}
