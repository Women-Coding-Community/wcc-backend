package com.wcc.platform.domain.cms.pages;

import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.platform.Member;
import java.util.List;

/** CMS Collaborators Page to highlight volunteers in the community. */
public record CollaboratorPage(Page page, Contact contact, List<Member> collaborators) {}
