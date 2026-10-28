package com.wcc.platform.repository.jdbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.platform.type.ResourceType;
import com.wcc.platform.domain.resource.MemberProfilePicture;
import com.wcc.platform.domain.resource.Resource;
import com.wcc.platform.repository.postgres.PostgresMemberProfilePictureRepository;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@ExtendWith(MockitoExtension.class)
class PostgresMemberProfilePictureRepositoryTest {

  private static final Long MEMBER_ID = 42L;
  @Mock private JdbcTemplate jdbcTemplate;
  private PostgresMemberProfilePictureRepository repository;
  private UUID resourceId;
  private Resource resource;

  @BeforeEach
  void setUp() {
    repository = new PostgresMemberProfilePictureRepository(jdbcTemplate);
    resourceId = UUID.randomUUID();
    resource =
        Resource.builder()
            .id(resourceId)
            .name("Profile picture")
            .fileName("pic.jpg")
            .contentType("image/jpeg")
            .size(123L)
            .driveFileId("driveId")
            .driveFileLink("http://link")
            .resourceType(ResourceType.PROFILE_PICTURE)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();
  }

  @Test
  void deleteByResourceIdShouldExecuteDeleteQuery() {
    when(jdbcTemplate.update(anyString(), any(UUID.class))).thenReturn(1);

    repository.deleteById(resourceId);

    verify(jdbcTemplate).update(anyString(), eq(resourceId));
  }

  @Test
  void deleteByMemberIdShouldExecuteDeleteQuery() {
    when(jdbcTemplate.update(anyString(), any(Long.class))).thenReturn(1);

    repository.deleteByMemberId(MEMBER_ID);

    verify(jdbcTemplate).update(anyString(), eq(MEMBER_ID));
  }

  @Test
  void findByResourceIdShouldReturnProfilePictureWhenFound() {
    var expected =
        MemberProfilePicture.builder()
            .memberId(MEMBER_ID)
            .resourceId(resourceId)
            .resource(resource)
            .build();

    when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), any(UUID.class)))
        .thenReturn(expected);

    Optional<MemberProfilePicture> found = repository.findById(resourceId);

    assertThat(found).isPresent();
    verify(jdbcTemplate).queryForObject(anyString(), any(RowMapper.class), eq(resourceId));
  }

  @Test
  void findByMemberIdShouldReturnProfilePictureWhenFound() {
    MemberProfilePicture expected =
        MemberProfilePicture.builder()
            .memberId(MEMBER_ID)
            .resourceId(resourceId)
            .resource(resource)
            .build();

    when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), any(Long.class)))
        .thenReturn(expected);

    Optional<MemberProfilePicture> found = repository.findByMemberId(MEMBER_ID);

    assertThat(found).isPresent();
    verify(jdbcTemplate).queryForObject(anyString(), any(RowMapper.class), eq(MEMBER_ID));
  }

  @Test
  void createShouldInsertAndReturnProfilePicture() {
    MemberProfilePicture toCreate =
        MemberProfilePicture.builder().memberId(MEMBER_ID).resourceId(resourceId).build();

    when(jdbcTemplate.update(anyString(), any(Long.class), any(UUID.class)) ).thenReturn(1);

    MemberProfilePicture created = repository.create(toCreate);

    assertEquals(toCreate, created);
    verify(jdbcTemplate).update(anyString(), eq(MEMBER_ID), eq(resourceId));
  }

  @Test
  void findByResourceIdShouldReturnEmptyWhenNotFound() {
    when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), any(UUID.class)))
        .thenThrow(new EmptyResultDataAccessException(1));

    Optional<MemberProfilePicture> found = repository.findById(resourceId);

    assertThat(found).isEmpty();
    verify(jdbcTemplate).queryForObject(anyString(), any(RowMapper.class), eq(resourceId));
  }

  @Test
  void findByMemberIdShouldReturnEmptyWhenNotFound() {
    when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), any(Long.class)))
        .thenThrow(new EmptyResultDataAccessException(1));

    Optional<MemberProfilePicture> found = repository.findByMemberId(MEMBER_ID);

    assertThat(found).isEmpty();
    verify(jdbcTemplate).queryForObject(anyString(), any(RowMapper.class), eq(MEMBER_ID));
  }

  @Test
  @DisplayName("Given duplicate rows for a member, when reading, then the error propagates")
  void findByMemberIdShouldPropagateDuplicateRowError() {
    when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), any(Long.class)))
        .thenThrow(new IncorrectResultSizeDataAccessException(1, 2));

    assertThatThrownBy(() -> repository.findByMemberId(MEMBER_ID))
        .isInstanceOf(IncorrectResultSizeDataAccessException.class);
  }

  @Test
  @DisplayName("Given duplicate rows for a resource, when reading, then the error propagates")
  void findByIdShouldPropagateDuplicateRowError() {
    when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), any(UUID.class)))
        .thenThrow(new IncorrectResultSizeDataAccessException(1, 2));

    assertThatThrownBy(() -> repository.findById(resourceId))
        .isInstanceOf(IncorrectResultSizeDataAccessException.class);
  }

  @Test
  @DisplayName("Given a re-uploaded picture, when creating, then the row is upserted on member_id")
  void createShouldUpsertOnDuplicateMemberId() {
    MemberProfilePicture toCreate =
        MemberProfilePicture.builder().memberId(MEMBER_ID).resourceId(resourceId).build();

    when(jdbcTemplate.update(anyString(), any(Long.class), any(UUID.class))).thenReturn(1);

    MemberProfilePicture created = repository.create(toCreate);

    assertThat(created).isEqualTo(toCreate);
    ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
    verify(jdbcTemplate).update(sqlCaptor.capture(), eq(MEMBER_ID), eq(resourceId));
    assertThat(sqlCaptor.getValue()).contains("ON CONFLICT (member_id)");
  }
}
