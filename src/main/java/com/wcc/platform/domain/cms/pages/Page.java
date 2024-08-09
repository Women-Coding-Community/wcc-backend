package com.wcc.platform.domain.cms.pages;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.SimpleLink;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** CMS Page attributes. */
@SuppressWarnings("PMD.ShortClassName")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class Page {
  private String title;

  @JsonInclude(Include.NON_NULL)
  private String subtitle;

  private String description;

  @JsonInclude(Include.NON_NULL)
  private SimpleLink link;

  @JsonInclude(Include.NON_NULL)
  private List<Image> images;
}
