package com.wcc.platform.domain.cms.pages.events;

import com.wcc.platform.domain.cms.attributes.EventDays;
import com.wcc.platform.domain.cms.attributes.EventType;
import com.wcc.platform.domain.platform.type.ProgramType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Filters model. */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
public class Filters {
  private List<EventType> type;
  private List<ProgramType> topics;
  private List<EventDays> date;
  private List<String> location;
}
