package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.pages.Page;
import lombok.Builder;
import lombok.Data;

/** Programme class representing the structure of any programme section of a programme page. */
@Data
@Builder
public class Programme {

  private String title;

  private String description;

  private Page card;

  /**
   * Programme Builder.
   *
   * @param title like - heading of the programme
   * @param description details co-relating the title
   * @param card if any details to be represented in a card
   */
  public Programme(final String title, final String description, final Page card) {

    this.title = title;
    this.description = description;
    this.card = card;
  }

  public Programme() {
    // Necessary constructor for jackson.
  }
}
