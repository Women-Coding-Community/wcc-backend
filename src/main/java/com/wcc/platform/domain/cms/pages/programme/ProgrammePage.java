package com.wcc.platform.domain.cms.pages.programme;

import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.cms.pages.Page;
import com.wcc.platform.domain.platform.EventSection;
import com.wcc.platform.domain.platform.Programme;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** BookClub programme details. */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class ProgrammePage {
  private Page page;
  private Contact contact;
  private List<Programme> programmeDetails;
  private List<EventSection> eventSection;
  // TODO Add resources section
}
