package com.wcc.platform.domain.cms.pages;

import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.SimpleLink;
import java.util.List;
import lombok.Builder;
import lombok.Data;

/** CMS Page attributes. */
@Data
@Builder
public class Page {
  String title;
  String subtitle;
  String description;
  SimpleLink link;
  List<Image> images;

  public Page(
      String title, String subtitle, String description, SimpleLink link, List<Image> images) {
    this.title = title;
    this.subtitle = subtitle;
    this.description = description;
    this.link = link;
    this.images = images;
  }

  public Page() {
    // Necessary constructor for jackson.
  }
}
