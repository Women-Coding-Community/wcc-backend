package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.attributes.SimpleLink;
import java.util.List;
import lombok.Builder;
import lombok.Data;

/** EventSection representing list of events {@link Event}. */
@Data
@Builder
public class EventSection {
  private String title;
  private SimpleLink link;
  private List<Event> events;

  /**
   * Builder for the EventSection.
   *
   * @param title like - "Upcoming Events" or "Past Events"
   * @param link link to the Events page
   * @param events list of events to show
   */
  public EventSection(final String title, final SimpleLink link, final List<Event> events) {
    this.title = title;
    this.link = link;
    this.events = events;
  }

  public EventSection() {
    // Necessary constructor for jackson.
  }
}
