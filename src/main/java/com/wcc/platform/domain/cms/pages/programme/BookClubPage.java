package com.wcc.platform.domain.cms.pages.programme;

import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.cms.pages.Page;

/**
 * BookClub programme details.
 *
 * @param page basic information of the page
 * @param contact social network contact information
 * @param programme programme details section
 */
public record BookClubPage(Page page, Contact contact, Programme programme) {}
