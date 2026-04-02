package com.wcc.platform.domain.cms.pages.mentorship;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.domain.platform.mentorship.SkillsFilter;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Filters for available mentors. */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MentorFilterSection {
  private String keyword;
  private List<MentorshipType> types;
  private SkillsFilter skills;
}
