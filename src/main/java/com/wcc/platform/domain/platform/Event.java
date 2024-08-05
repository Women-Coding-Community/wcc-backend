package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.attributes.EventResource;
import com.wcc.platform.domain.cms.attributes.EventType;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.ProgramType;
import com.wcc.platform.domain.cms.attributes.SimpleLink;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Event {
  private UUID id;
  private String title;
  private String description;
  private EventType eventType;
  private String startDate;
  private String endDate;
  private ProgramType topics;
  private List<Image> images;
  private String speaker;
  private SimpleLink speakerProfileLink;
  private String host;
  private SimpleLink hostProfileLink;
  private SimpleLink eventLink;
  private List<EventResource> eventResources;

  public Event(
      final UUID id,
      final String title,
      final String description,
      final EventType eventType,
      final String startDate,
      final String endDate,
      final ProgramType topics,
      final List<Image> images,
      final String speaker,
      final SimpleLink speakerProfileLink,
      final String host,
      final SimpleLink hostProfileLink,
      final SimpleLink eventLink,
      final List<EventResource> eventResources) {
    this.title = title;
    this.description = description;
    this.eventType = eventType;
    this.startDate = startDate;
    this.endDate = endDate;
    this.topics = topics;
    this.images = images;
    this.speaker = speaker;
    this.speakerProfileLink = speakerProfileLink;
    this.host = host;
    this.hostProfileLink = hostProfileLink;
    this.eventLink = eventLink;
    this.eventResources = eventResources;
  }

  public Event() {}
}
