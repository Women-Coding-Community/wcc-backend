package com.wcc.platform.repository.jdbc;

import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@ExtendWith(MockitoExtension.class)
class PostgresMemberProfilePictureRepositoryTest {

  private static final Integer MEMBER_ID = 42;
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
    when(jdbcTemplate.update(anyString(), any(Integer.class))).thenReturn(1);

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

    assertTrue(found.isPresent());
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

    when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), any(Integer.class)))
        .thenReturn(expected);

    Optional<MemberProfilePicture> found = repository.findByMemberId(MEMBER_ID);

    assertTrue(found.isPresent());
    verify(jdbcTemplate).queryForObject(anyString(), any(RowMapper.class), eq(MEMBER_ID));
  }
}
