package com.wcc.platform.utils;

import static java.util.Locale.ENGLISH;

import com.wcc.platform.domain.cms.attributes.Languages;
import com.wcc.platform.domain.cms.attributes.MentorshipFocusArea;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import com.wcc.platform.domain.cms.pages.mentorship.MentorAppliedFilters;
import com.wcc.platform.domain.cms.pages.mentorship.MentorFilterSection;
import com.wcc.platform.domain.platform.mentorship.MentorDto;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycle;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.domain.platform.mentorship.Skills;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

/** Utility class for filtering search results. */
public final class FiltersUtil {

  private FiltersUtil() {}

  /** Returns a MentorFilterSection object with all mentorship types and skills. */
  public static MentorFilterSection mentorshipAllFilters() {
    final var skills =
        new Skills(0, TechnicalArea.getAll(), Languages.getAll(), MentorshipFocusArea.getAll());

    return MentorFilterSection.builder()
        .types(List.of(MentorshipType.LONG_TERM, MentorshipType.AD_HOC))
        .skills(skills)
        .build();
  }

  /**
   * Filters a list of mentors based on the specified criteria in the filters object and the current
   * mentorship cycle.
   *
   * @param filters the filtering criteria encapsulated in a {@link MentorAppliedFilters} object; it
   *     can include various fields such as keyword, mentorship types, years of experience,
   *     technical areas, languages, or mentorship focus. If null, no filtering is applied.
   * @param currentCycle the current mentorship cycle as a {@link MentorshipCycle} object, used to
   *     retrieve the initial list of mentors to be filtered.
   * @return a list of {@link MentorDto} objects that match the specified filtering criteria, or the
   *     full list of mentors if no filters are provided.
   */
  public static List<MentorDto> applyFilters(
      final List<MentorDto> mentors, final MentorAppliedFilters filters) {

    if (filters == null) {
      return mentors;
    }

    return mentors.stream()
        .filter(mentor -> matchesKeyword(mentor, filters))
        .filter(mentor -> matchesTypes(mentor, filters))
        .filter(mentor -> matchesMinYears(mentor, filters))
        .filter(mentor -> matchesAreas(mentor, filters))
        .filter(mentor -> matchesLanguages(mentor, filters))
        .filter(mentor -> matchesFocus(mentor, filters))
        .toList();
  }

  private static boolean matchesKeyword(
      final MentorDto mentor, final MentorAppliedFilters filters) {
    final var filterKeyword = filters.keyword();
    if (filterKeyword == null || filterKeyword.isBlank()) {
      return true;
    }
    final var keyword = filterKeyword.toLowerCase(ENGLISH);
    return mentor.getFullName().toLowerCase(ENGLISH).contains(keyword)
        || (!StringUtils.isBlank(mentor.getPosition())
            && mentor.getPosition().toLowerCase(ENGLISH).contains(keyword))
        || (!StringUtils.isBlank(mentor.getCity())
            && mentor.getCity().toLowerCase(ENGLISH).contains(keyword))
        || (!StringUtils.isBlank(mentor.getCompanyName())
            && mentor.getCompanyName().toLowerCase(ENGLISH).contains(keyword))
        || (!StringUtils.isBlank(mentor.getBio())
            && mentor.getBio().toLowerCase(ENGLISH).contains(keyword))
        || (!StringUtils.isEmpty(mentor.getMenteeSection().additional())
            && mentor.getMenteeSection().additional().toLowerCase(ENGLISH).contains(keyword))
        || (!CollectionUtils.isEmpty(mentor.getSpokenLanguages())
            && mentor.getSpokenLanguages().stream()
                .anyMatch(language -> language.toLowerCase(ENGLISH).contains(keyword)));
  }

  private static boolean matchesTypes(final MentorDto mentor, final MentorAppliedFilters filters) {
    final var types = filters.mentorshipTypes();
    if (CollectionUtils.isEmpty(types)) {
      return true;
    }
    if (mentor.getMenteeSection() == null || mentor.getMenteeSection().mentorshipType() == null) {
      return false;
    }
    return types.stream().anyMatch(t -> mentor.getMenteeSection().mentorshipType().contains(t));
  }

  private static boolean matchesMinYears(
      final MentorDto mentorDto, final MentorAppliedFilters filters) {
    final Integer minYears = filters.yearsExperience();
    if (minYears == null) {
      return true;
    }
    final var skills = mentorDto.getSkills();
    return skills != null
        && skills.yearsExperience() != null
        && skills.yearsExperience() >= minYears;
  }

  private static boolean matchesAreas(
      final MentorDto mentorDto, final MentorAppliedFilters filters) {
    final var wantedAreas = filters.areas();
    if (wantedAreas == null || wantedAreas.isEmpty()) {
      return true;
    }
    final var skills = mentorDto.getSkills();
    return skills != null
        && skills.areas() != null
        && skills.areas().stream().anyMatch(wantedAreas::contains);
  }

  private static boolean matchesLanguages(
      final MentorDto mentorDto, final MentorAppliedFilters filters) {
    final var wantedLangs = filters.languages();
    if (wantedLangs == null || wantedLangs.isEmpty()) {
      return true;
    }
    final var skills = mentorDto.getSkills();
    return skills != null
        && skills.languages() != null
        && skills.languages().stream().anyMatch(wantedLangs::contains);
  }

  private static boolean matchesFocus(
      final MentorDto mentorDto, final MentorAppliedFilters filters) {
    final var wantedFocus = filters.focus();
    if (wantedFocus == null || wantedFocus.isEmpty()) {
      return true;
    }
    final var skills = mentorDto.getSkills();
    return skills != null
        && skills.mentorshipFocus() != null
        && skills.mentorshipFocus().stream().anyMatch(wantedFocus::contains);
  }
}
