package com.wcc.platform.repository.surrealdb;

import static com.wcc.platform.factories.SetupFactories.createFooterPageTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.surrealdb.driver.SyncSurrealDriver;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.pages.FooterPage;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SurrealDbPageRepositoryTest {

  private static final String TABLE = SurrealDbPageRepository.TABLE;

  @Mock private SyncSurrealDriver mockDriver;

  private SurrealDbPageRepository<FooterPage> repository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    repository = new SurrealDbPageRepository<>(mockDriver, FooterPage.class);
  }

  @Test
  void testSave() {
    FooterPage entity = createFooterPageTest();
    when(mockDriver.create(TABLE, entity)).thenReturn(entity);

    var savedEntity = repository.save(entity);

    verify(mockDriver, times(1)).create(TABLE, entity);
    assertEquals(entity, savedEntity);
  }

  @Test
  void testFindAll() {
    List<FooterPage> mockResult = List.of(createFooterPageTest());

    when(mockDriver.select(TABLE, FooterPage.class)).thenReturn(mockResult);

    Collection<FooterPage> result = repository.findAll();

    verify(mockDriver, times(1)).select(TABLE, FooterPage.class);
    assertEquals(mockResult, result);
  }

  @Test
  void testFindByIdNotFoundCase1() {
    Optional<FooterPage> result = repository.findById(PageType.FOOTER.name());

    verify(mockDriver, times(1)).query(anyString(), anyMap(), eq(FooterPage.class));
    assertTrue(result.isEmpty());
  }

  @Test
  void testFindByIdNotFoundCase2() {
    when(mockDriver.query(anyString(), anyMap(), eq(FooterPage.class))).thenReturn(List.of());

    Optional<FooterPage> result = repository.findById("test-id-2");

    verify(mockDriver, times(1)).query(anyString(), anyMap(), eq(FooterPage.class));
    assertTrue(result.isEmpty());
  }

  @Test
  void testDeleteById() {
    String pageId = "test-pageId";

    repository.deleteById(pageId);

    verify(mockDriver, times(1)).delete("test-pageId");
  }
}
