package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.platform.type.ResourceType;
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
public class ResourceContent {
  private String id;
  private String name;
  private String description;
  private String rawContent;
  private ResourceType type;
  private List<Image> images;
}
