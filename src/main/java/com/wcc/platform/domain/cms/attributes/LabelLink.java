package com.wcc.platform.domain.cms.attributes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/** Web link attributes to be shown in the frontend. */
public record LabelLink(@JsonInclude(Include.NON_NULL) String title, String label, String uri) {}
