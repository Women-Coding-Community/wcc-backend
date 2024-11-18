package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.attributes.EventResource;
import com.wcc.platform.domain.cms.attributes.EventType;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.LabelLink;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

  @NotBlank private String title;
  @NotBlank private String description;
  @NotNull private EventType eventType;
  @NotNull private String startDate; // TODO convert to Date
  @NotNull private String endDate; // TODO convert to Date
  @NotNull private ProgramType topics;
  @NotEmpty private List<Image> images;
  private LabelLink speakerProfile;
  private LabelLink hostProfile;
  @NotNull private LabelLink eventLink;
  private List<EventResource> eventResources;
}
