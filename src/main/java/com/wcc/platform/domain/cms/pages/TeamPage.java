package com.wcc.platform.domain.cms.pages;

import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.cms.attributes.MemberByType;

public record TeamPage(Page page, Contact contact, MemberByType membersByType) {

}