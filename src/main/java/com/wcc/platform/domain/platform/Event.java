package com.wcc.platform.domain.platform;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.wcc.platform.domain.cms.attributes.EventResource;
import com.wcc.platform.domain.cms.attributes.EventType;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.SimpleLink;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Event class with all the relevant attributes for an event. */
@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {

  private UUID id;
  private String title;
  private String description;
  private EventType eventType;
  private String startDate; // TODO convert to Date
  private String endDate; // TODO convert to Date
  private ProgramType topics;
  private List<Image> images;

  @JsonInclude(Include.NON_NULL)
  private SimpleLink speakerProfile;

  @JsonInclude(Include.NON_NULL)
  private SimpleLink hostProfile;

  private SimpleLink eventLink;

  @JsonInclude(Include.NON_NULL)
  private List<EventResource> eventResources;
}
