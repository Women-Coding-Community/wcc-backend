package com.wcc.platform.domain.cms.pages.programme;

import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.cms.pages.Page;
import com.wcc.platform.domain.platform.EventSection;
import com.wcc.platform.domain.platform.Programme;
import java.util.List;

/**
 * BookClub programme details.
 *
 * @param page basic information of the page
 * @param contact social network contact information
 * @param programmeDetails programme details section
 * @param eventSection event details section
 */
public record ProgrammePage(
    Page page,
    Contact contact,
    List<Programme> programmeDetails,
    List<EventSection> eventSection) {}
    // TODO Add resources section
