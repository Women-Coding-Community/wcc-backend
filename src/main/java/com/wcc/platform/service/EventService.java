package com.wcc.platform.service;

import static com.wcc.platform.domain.cms.PageType.EVENTS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.pages.PageData;
import com.wcc.platform.domain.cms.pages.Pagination;
import com.wcc.platform.domain.cms.pages.events.EventsPage;
import com.wcc.platform.domain.cms.pages.events.EventsPageMetaData;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.domain.platform.Event;
import com.wcc.platform.utils.FileUtil;
import com.wcc.platform.utils.PaginationUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Event service. */
@Service
public class EventService {

  private final ObjectMapper objectMapper;

  @Autowired
  public EventService(final ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /**
   * Read Json and convert to paginated event page.
   *
   * @return POJO eventsPage
   */
  public EventsPage getEvents(int currentPage, int pageSize) {
    try {
      final var data = FileUtil.readFileAsString(EVENTS.getFileName());

      EventsPage page = objectMapper.readValue(data, EventsPage.class);
      List<Event> allEvents = page.data().items();

      List<Event> paginatedEvents =
          PaginationUtil.getPaginatedResult(allEvents, currentPage, pageSize);
      Pagination paginationRecord =
          new Pagination(
              allEvents.size(),
              PaginationUtil.getTotalPages(allEvents, pageSize),
              currentPage,
              pageSize);
      PageData<Event> eventPageData = new PageData<>(null, null, null, paginatedEvents);

      return new EventsPage(
          new EventsPageMetaData(
              paginationRecord, page.metadata().hero(), page.metadata().contact()),
          eventPageData);

    } catch (JsonProcessingException e) {
      throw new PlatformInternalException(e.getMessage(), e);
    }
  }
}
