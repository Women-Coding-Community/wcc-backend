package com.wcc.platform.domain.cms.attributes;

import com.wcc.platform.domain.cms.pages.programme.ProgrammeCardItem;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** CMS ProgrammesSection to be included in the Programmes page. */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class ProgrammesSection {
  @NotNull private List<ProgrammeCardItem> items;
}
