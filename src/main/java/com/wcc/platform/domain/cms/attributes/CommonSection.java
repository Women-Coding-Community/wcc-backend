package com.wcc.platform.domain.cms.attributes;

import com.wcc.platform.domain.cms.attributes.style.CustomStyle;
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
public class CommonSection {
  private String title;
  private String subtitle;
  private String description;
  private LabelLink link;
  private List<Image> images;
  private CustomStyle customStyle;
}
