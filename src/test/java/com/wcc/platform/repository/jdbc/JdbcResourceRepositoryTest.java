package com.wcc.platform.repository.jdbc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.wcc.platform.domain.platform.type.ResourceType;
import com.wcc.platform.domain.resource.Resource;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@ExtendWith(MockitoExtension.class)
class JdbcResourceRepositoryTest {

  @Mock private JdbcTemplate jdbcTemplate;

  private JdbcResourceRepository repository;

  private Resource testResource;

  @BeforeEach
  void setUp() {
    repository = new JdbcResourceRepository(jdbcTemplate);
    testResource =
        Resource.builder()
            .id(UUID.randomUUID())
            .name("Test Resource")
            .description("Test Description")
            .fileName("test.txt")
            .contentType("text/plain")
            .size(1000L)
            .driveFileId("drive123")
            .driveFileLink("http://drive.google.com/test")
            .resourceType(ResourceType.EVENT_IMAGE)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();
  }

  @Test
  void createShouldGenerateIdAndTimestampsWhenNotProvided() {
    Resource resourceWithoutId = testResource.toBuilder().id(null).build();

    when(jdbcTemplate.update(anyString(), any(Object[].class))).thenReturn(1);

    Resource created = repository.create(resourceWithoutId);

    assertNotNull(created.getId());
    assertNotNull(created.getCreatedAt());
    assertNotNull(created.getUpdatedAt());
    verify(jdbcTemplate).update(anyString(), any(Object[].class));
  }

  @Test
  void updateShouldUpdateResourceAndTimestamp() {
    when(jdbcTemplate.update(anyString(), any(Object[].class))).thenReturn(1);

    Resource updated = repository.update(testResource.getId(), testResource);

    assertEquals(testResource.getId(), updated.getId());
    assertNotNull(updated.getUpdatedAt());
    verify(jdbcTemplate).update(anyString(), any(Object[].class));
  }

  @Test
  void findByIdShouldReturnResourceWhenFound() {
    when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), any(UUID.class)))
        .thenReturn(testResource);

    Optional<Resource> found = repository.findById(testResource.getId());

    assertTrue(found.isPresent());
    assertEquals(testResource, found.get());
  }

  @Test
  void deleteByIdShouldExecuteDeleteQuery() {
    when(jdbcTemplate.update(anyString(), any(UUID.class))).thenReturn(1);

    repository.deleteById(testResource.getId());

    verify(jdbcTemplate).update(anyString(), eq(testResource.getId()));
  }

  @Test
  void findByTypeShouldReturnMatchingResources() {
    when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString()))
        .thenReturn(List.of(testResource));

    List<Resource> found = repository.findByType(ResourceType.OTHER);

    assertFalse(found.isEmpty());
    assertEquals(testResource, found.getFirst());
  }

  @Test
  void findByNameContainingShouldReturnMatchingResources() {
    when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString()))
        .thenReturn(List.of(testResource));

    List<Resource> found = repository.findByNameContaining("Test");

    assertFalse(found.isEmpty());
    assertEquals(testResource, found.getFirst());
  }

  @Test
  void updateShouldThrowExceptionWhenResourceNotFound() {
    when(jdbcTemplate.update(anyString(), any(Object[].class))).thenReturn(0);

    Resource resourceToUpdate = testResource.toBuilder().build();
    Resource updated = repository.update(testResource.getId(), resourceToUpdate);

    assertEquals(testResource.getId(), updated.getId());
    assertNotNull(updated.getUpdatedAt());
    verify(jdbcTemplate).update(anyString(), any(Object[].class));
  }

  @Test
  void findByTypeShouldReturnEmptyListWhenNoResourcesMatch() {
    when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString())).thenReturn(List.of());

    List<Resource> found = repository.findByType(ResourceType.EVENT_PDF);

    assertTrue(found.isEmpty());
  }

  @Test
  void findByNameContainingShouldReturnEmptyListWhenNoResourcesMatch() {
    when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString())).thenReturn(List.of());

    List<Resource> found = repository.findByNameContaining("NonExistent");

    assertTrue(found.isEmpty());
  }

  @Test
  void deleteByIdShouldThrowExceptionWhenResourceNotFound() {
    when(jdbcTemplate.update(anyString(), any(UUID.class))).thenReturn(0);

    repository.deleteById(testResource.getId());

    verify(jdbcTemplate).update(anyString(), eq(testResource.getId()));
  }
}
