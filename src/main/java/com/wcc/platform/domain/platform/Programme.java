package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.pages.Page;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Programme class representing the structure of any programme section of a programme page. */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class Programme {
  private String title;
  private String description;
  private Page card;
}
