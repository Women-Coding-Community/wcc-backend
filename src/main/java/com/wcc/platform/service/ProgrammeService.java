package com.wcc.platform.service;

import static com.wcc.platform.domain.cms.PageType.PROGRAMMES;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.pages.programme.ProgrammePage;
import com.wcc.platform.domain.cms.pages.programme.ProgrammesPage;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.domain.platform.type.ProgramType;
import com.wcc.platform.repository.PageRepository;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Programme Service. */
@Service
@Slf4j
public class ProgrammeService {

  private final PageRepository pageRepository;

  private final ObjectMapper objectMapper;

  @Autowired
  public ProgrammeService(final PageRepository pageRepository, final ObjectMapper objectMapper) {
    this.pageRepository = pageRepository;
    this.objectMapper = objectMapper;
  }

  /**
   * API to fetch details about different types of programmes.
   *
   * @return Programme Page json response
   */
  public ProgrammePage getProgramme(final ProgramType programType) {
    final var page = pageRepository.findById(programType.toPageId());

    if (page.isPresent()) {
      return objectMapper.convertValue(page.get(), ProgrammePage.class);
    }

    throw new ContentNotFoundException(programType);
  }

  /** Save programme page based on program Type. */
  @SuppressWarnings("unchecked")
  public Object update(final ProgramType programType, final Object page) {
    try {
      return pageRepository.update(
          programType.toPageId(), objectMapper.convertValue(page, Map.class));
    } catch (IllegalArgumentException e) {
      log.error("Error while updating page: {}, {}", programType, page.toString(), e);
      throw new PlatformInternalException(programType, e);
    }
  }

  /** Create any page. */
  @SuppressWarnings("unchecked")
  public Object create(final ProgramType programType, final Object page) {
    try {
      return pageRepository.create(objectMapper.convertValue(page, Map.class));
    } catch (IllegalArgumentException e) {
      log.error("Error while creating page: {}, {}", programType, page.toString(), e);
      throw new PlatformInternalException(programType, e);
    }
  }

  /** Get the page with all programmes. */
  public ProgrammesPage getAllProgrammes() {
    final var page = pageRepository.findById(PROGRAMMES.getId());
    if (page.isPresent()) {
      try {
        return objectMapper.convertValue(page.get(), ProgrammesPage.class);
      } catch (IllegalArgumentException e) {
        throw new PlatformInternalException(e.getMessage(), e);
      }
    }
    return pageRepository.getFallback(PROGRAMMES, ProgrammesPage.class, objectMapper);
  }
}
