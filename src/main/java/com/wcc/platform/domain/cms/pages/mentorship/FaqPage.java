package com.wcc.platform.domain.cms.pages.mentorship;

import com.wcc.platform.domain.cms.attributes.FaqSection;
import com.wcc.platform.domain.cms.pages.Page;
import java.util.List;

/**
 * Faq page object.
 *
 * @param page
 * @param faqSection
 */
public record FaqPage(Page page, List<FaqSection> faqSection) {}
