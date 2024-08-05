package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.attributes.SimpleLink;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventSection {
  String title;
  SimpleLink link;
  List<Event> events;

  public EventSection(final String title, final SimpleLink link, final List<Event> events) {
    this.title = title;
    this.link = link;
    this.events = events;
  }

  public EventSection() {}
}
