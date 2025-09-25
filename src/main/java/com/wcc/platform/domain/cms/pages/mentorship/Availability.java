package com.wcc.platform.domain.cms.pages.mentorship;

import java.time.Month;

/**
 * Availability of the mentor
 *
 * @param month which months eg: MAY, JUNE
 * @param hours number of hours available eg: 2
 */
public record Availability(Month month, Integer hours) {}
