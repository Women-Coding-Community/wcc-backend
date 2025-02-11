package com.wcc.platform.service;

import static com.wcc.platform.domain.cms.PageType.EVENTS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.pages.PageData;
import com.wcc.platform.domain.cms.pages.PageMetadata;
import com.wcc.platform.domain.cms.pages.Pagination;
import com.wcc.platform.domain.cms.pages.events.EventsPage;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.domain.platform.Event;
import com.wcc.platform.repository.PageRepository;
import com.wcc.platform.utils.PaginationUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Event service. */
@Service
public class EventService {

  private final ObjectMapper objectMapper;
  private final PageRepository pageRepository;

  @Autowired
  public EventService(final ObjectMapper objectMapper, final PageRepository pageRepository) {
    this.objectMapper = objectMapper;
    this.pageRepository = pageRepository;
  }

  /**
   * Find Event Page in DB and convert to POJO paginated event page.
   *
   * @return POJO eventsPage
   */
  public EventsPage getEvents(final int currentPage, final int pageSize) {
    final var pageOptional = pageRepository.findById(EVENTS.getId());
    if (pageOptional.isPresent()) {
      try {
        final var page = objectMapper.convertValue(pageOptional.get(), EventsPage.class);
        final var allEvents = page.data().items();

        final List<Event> paginatedEvents =
            PaginationUtil.getPaginatedResult(allEvents, currentPage, pageSize);

        final Pagination paginationRecord =
            new Pagination(
                allEvents.size(),
                PaginationUtil.getTotalPages(allEvents, pageSize),
                currentPage,
                pageSize);
        final PageData<Event> eventPageData = new PageData<>(paginatedEvents);

        return new EventsPage(
            EVENTS.getId(),
            new PageMetadata(paginationRecord),
            page.heroSection(),
            page.section(),
            page.contact(),
            eventPageData);

      } catch (IllegalArgumentException e) {
        throw new PlatformInternalException(e.getMessage(), e);
      }
    }
    throw new ContentNotFoundException(EVENTS);
  }
}
