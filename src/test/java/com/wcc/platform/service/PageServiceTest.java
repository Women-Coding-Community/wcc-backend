package com.wcc.platform.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.repository.PageRepository;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PageServiceTest {

  @Mock private PageRepository pageRepository;
  @Mock private ObjectMapper objectMapper;

  @InjectMocks private PageService pageService;

  private PageType pageType;
  private Map<String, Object> pageEntity;
  private Map<String, Object> expectedEntity;

  @BeforeEach
  void setUp() {
    pageType = PageType.ABOUT_US;
    pageEntity = Map.of("id", "invalid-id", "content", "content");
    expectedEntity = Map.of("id", pageType.getId(), "content", "content");
  }

  @Test
  @DisplayName(
      "Given valid pageType and page, When update is called, "
          + "Then it should update the page successfully")
  void updateShouldUpdatePageSuccessfully() {
    when(objectMapper.convertValue(any(), eq(Map.class))).thenReturn(pageEntity);
    when(pageRepository.update(anyString(), any())).thenReturn(pageEntity);

    Object result = pageService.update(pageType, pageEntity);
    assertEquals(pageEntity, result);

    verify(pageRepository).update(eq(pageType.getId()), eq(expectedEntity));
  }

  @Test
  @DisplayName(
      "Given update page, When page does not exist, "
          + "Then it should throw ContentNotFoundException")
  void updateShouldThrowsContentNotFound() {
    when(pageRepository.findById(pageType.getId())).thenReturn(Optional.empty());

    assertThrows(ContentNotFoundException.class, () -> pageService.update(pageType, pageEntity));
  }

  @Test
  @DisplayName(
      "Given invalid pageType and page, When update is called, "
          + "Then it should throw PlatformInternalException")
  void updateShouldThrowExceptionOnConversionError() {
    when(objectMapper.convertValue(any(), eq(Map.class))).thenThrow(IllegalArgumentException.class);

    assertThrows(PlatformInternalException.class, () -> pageService.update(pageType, pageEntity));
  }

  @Test
  @DisplayName(
      "Given pageType and page content are valid, When create is called, "
          + "Then it should create the page successfully")
  void createShouldCreatePageSuccessfully() {
    when(objectMapper.convertValue(any(), eq(Map.class))).thenReturn(pageEntity);
    when(pageRepository.create(any())).thenReturn(expectedEntity);

    Object result = pageService.create(pageType, expectedEntity);
    assertEquals(expectedEntity, result);

    verify(pageRepository).create(eq(expectedEntity));
  }

  @Test
  @DisplayName(
      "Given pageType and page, When ids are not the same And create is called, "
          + "Then it override id with pageType And create the page successfully")
  void createShouldCreatePageSuccessfullyWhenInvalidId() {
    when(objectMapper.convertValue(any(), eq(Map.class))).thenReturn(pageEntity);
    when(pageRepository.create(any())).thenReturn(expectedEntity);

    // When id is not the same as pageType
    Object result = pageService.create(pageType, pageEntity);
    assertEquals(expectedEntity, result);

    verify(pageRepository).create(eq(expectedEntity));
  }

  @Test
  @DisplayName(
      "Given invalid page content, when create is called, "
          + "Then it should throw PlatformInternalException")
  void createShouldThrowExceptionOnConversionError() {
    when(objectMapper.convertValue(any(), eq(Map.class))).thenThrow(IllegalArgumentException.class);

    assertThrows(PlatformInternalException.class, () -> pageService.create(pageType, pageEntity));
  }

  @Test
  @DisplayName(
      "Given existing page ID, When deletePageById is called, Then it should delete the page")
  void deletePageByIdShouldDeleteWhenPageExists() {
    when(pageRepository.findById("123")).thenReturn(Optional.of(pageEntity));

    pageService.deletePageById("123");

    verify(pageRepository).deleteById("123");
  }

  @Test
  @DisplayName(
      "Given non-existing page ID, when deletePageById is called, "
          + "Then it should throw ContentNotFoundException")
  void deletePageByIdShouldThrowExceptionWhenPageNotFound() {
    when(pageRepository.findById("123")).thenReturn(Optional.empty());

    assertThrows(ContentNotFoundException.class, () -> pageService.deletePageById("123"));
  }
}
