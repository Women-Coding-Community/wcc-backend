package com.wcc.platform.domain.cms.pages.mentorship;

import java.time.Month;
import java.util.List;

/**
 * Availability of the mentor
 *
 * @param months which months eg: 4-11
 * @param hours number of hours available eg: 2
 */
public record Availability(List<Month> months, Integer hours) {}
