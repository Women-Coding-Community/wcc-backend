package com.wcc.platform.domain.cms.pages;

import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.LabelLink;
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
  private String subtitle;
  private String description;
  private LabelLink link;
  private List<Image> images;
}
