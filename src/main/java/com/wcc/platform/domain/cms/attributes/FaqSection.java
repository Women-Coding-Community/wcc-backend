package com.wcc.platform.domain.cms.attributes;

import java.util.List;

/**
 * Faq section on faq page.
 *
 * @param title
 * @param details
 */
public record FaqSection(String title, List<Faq> details) {}
