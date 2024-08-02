package com.wcc.platform.domain.cms.pages.programme;

import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.cms.pages.Page;

/**
 * BookClub programme details.
 *
 * @param page basic information of the page
 * @param contact social network contact information
 * @param programmeInfo programme details section
 */
public record ProgrammePage(Page page, Contact contact, ProgrammeInfo programmeInfo) {}
