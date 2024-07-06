package com.wcc.platform.domain.cms.pages;

import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.cms.attributes.MemberByType;

/** CMS Community Core Team Page grouped by members types. */
public record TeamPage(Page page, Contact contact, MemberByType membersByType) {}
