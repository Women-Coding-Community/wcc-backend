package com.wcc.platform.domain.resource;

import com.wcc.platform.domain.cms.attributes.LabelLink;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Resource for mentorship, mentors, events and, etc. */
@Setter(AccessLevel.NONE)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MentorResource {
  private String id;
  private List<String> books;
  private List<LabelLink> links;
  private List<Resource> resources;
}
