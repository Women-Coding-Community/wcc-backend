package com.wcc.platform.domain.cms.pages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wcc.platform.domain.cms.attributes.LabelLink;
import com.wcc.platform.domain.cms.attributes.Network;
import java.util.List;

/** CMS Footer section details. */
public record FooterPage(
    @JsonIgnore String id,
    String title,
    String subtitle,
    String description,
    List<Network> network,
    LabelLink link) {}
