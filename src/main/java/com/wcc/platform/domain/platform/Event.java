package com.wcc.platform.domain.platform;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wcc.platform.domain.cms.attributes.EventResource;
import com.wcc.platform.domain.cms.attributes.EventType;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.LabelLink;
import java.time.Instant;
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
  //private String startDate; // TODO convert to Date
  private Instant startDate;
  //private String endDate; // TODO convert to Date
  private Instant endDate;
  private ProgramType topics;
  private List<Image> images;
  private LabelLink speakerProfile;
  private LabelLink hostProfile;
  private LabelLink eventLink;
  private List<EventResource> eventResources;
  public setEndDate(String dateFormat) {
    this.endDate = DateTimeFormatter.ISO_INSTANT.parse(dateFormat.replace(".", ":"));
  }

  public setStartDate(String dateFormat) {
    this.startDate = DateTimeFormatter.ISO_INSTANT.parse(dateFormat.replace(".", ":"));
  }

}
