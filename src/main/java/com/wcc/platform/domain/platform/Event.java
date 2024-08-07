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
  private SimpleLink speakerProfile;
  private String host;
  private SimpleLink hostProfile;
  private SimpleLink eventLink;
  private List<EventResource> eventResources;

  public Event(
      UUID id,
      String title,
      String description,
      EventType eventType,
      String startDate,
      String endDate,
      ProgramType topics,
      List<Image> images,
      String speaker,
      SimpleLink speakerProfile,
      String host,
      SimpleLink hostProfile,
      SimpleLink eventLink,
      List<EventResource> eventResources) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.eventType = eventType;
    this.startDate = startDate;
    this.endDate = endDate;
    this.topics = topics;
    this.images = images;
    this.speaker = speaker;
    this.speakerProfile = speakerProfile;
    this.host = host;
    this.hostProfile = hostProfile;
    this.eventLink = eventLink;
    this.eventResources = eventResources;
  }

  public Event() {
    // Necessary constructor for jackson.
  }
}
