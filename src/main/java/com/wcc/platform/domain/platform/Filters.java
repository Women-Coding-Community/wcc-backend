package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.attributes.EventDays;
import com.wcc.platform.domain.cms.attributes.EventType;
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
  List<EventType> type;
  List<ProgramType> topics;
  List<EventDays> date;
  List<String> region;
}
