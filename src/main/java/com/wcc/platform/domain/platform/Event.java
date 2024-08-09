package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.attributes.EventResource;
import com.wcc.platform.domain.cms.attributes.EventType;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.SimpleLink;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/** Event class with all the relevant attributes for an event. */
@Getter
@EqualsAndHashCode
@ToString
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
  private SimpleLink speakerProfile;
  private SimpleLink hostProfile;
  private SimpleLink eventLink;
  private List<EventResource> eventResources;

  /** Event builder. */
  @SuppressWarnings("PMD.ExcessiveParameterList")
  public Event(
      final UUID id,
      final String title,
      final String description,
      final EventType eventType,
      final String startDate,
      final String endDate,
      final ProgramType topics,
      final List<Image> images,
      final SimpleLink speakerProfile,
      final SimpleLink hostProfile,
      final SimpleLink eventLink,
      final List<EventResource> eventResources) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.eventType = eventType;
    this.startDate = startDate;
    this.endDate = endDate;
    this.topics = topics;
    this.images = images;
    this.speakerProfile = speakerProfile;
    this.hostProfile = hostProfile;
    this.eventLink = eventLink;
    this.eventResources = eventResources;
  }

  public Event() {
    // Necessary constructor for jackson.
  }
}
