package com.wcc.platform.domain.cms.pages.programme;

import com.wcc.platform.domain.cms.attributes.SimpleDetails;
import java.util.List;

/**
 * Common programme information.
 *
 * @param details for the programme
 */
public record ProgrammeInfo(List<SimpleDetails> details) {}
