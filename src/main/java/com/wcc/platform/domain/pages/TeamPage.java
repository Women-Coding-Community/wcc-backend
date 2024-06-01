package com.wcc.platform.domain.pages;

import com.wcc.platform.domain.pages.attributes.Contact;
import com.wcc.platform.domain.pages.attributes.MemberByType;

public record TeamPage(Page page, Contact contact, MemberByType membersByType) {
}