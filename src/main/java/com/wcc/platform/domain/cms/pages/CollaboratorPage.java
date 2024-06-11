package com.wcc.platform.domain.cms.pages;

import java.util.List;
import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.platform.Member;

public record CollaboratorPage(Page page, Contact contact, List<Member> collaborators) {
}
