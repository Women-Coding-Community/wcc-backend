package com.wcc.platform.repository.jdbc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.platform.type.ResourceType;
import com.wcc.platform.domain.resource.MentorProfilePicture;
import com.wcc.platform.domain.resource.Resource;
import com.wcc.platform.repository.ResourceRepository;
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
class JdbcMentorProfilePictureRepositoryTest {

  @Mock private JdbcTemplate jdbcTemplate;
  @Mock private ResourceRepository resourceRepository;

  private JdbcMentorProfilePictureRepository repository;

  private UUID resourceId;
  private UUID mppId;

  @BeforeEach
  void setUp() {
    repository = new JdbcMentorProfilePictureRepository(jdbcTemplate, resourceRepository);
    resourceId = UUID.randomUUID();
    mppId = UUID.randomUUID();
  }

  @Test
  void deleteByResourceIdShouldExecuteDeleteQuery() {
    when(jdbcTemplate.update(anyString(), any(UUID.class))).thenReturn(1);

    repository.deleteByResourceId(resourceId);

    verify(jdbcTemplate).update(anyString(), eq(resourceId));
  }

  @Test
  void findByResourceIdShouldReturnProfilePictureWhenFound() {
    MentorProfilePicture expected =
        MentorProfilePicture.builder()
            .id(mppId)
            .mentorEmail("mentor@example.com")
            .resourceId(resourceId)
            .resource(
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
                    .build())
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), any(UUID.class)))
        .thenReturn(expected);

    Optional<MentorProfilePicture> found = repository.findByResourceId(resourceId);

    assertTrue(found.isPresent());
    verify(jdbcTemplate).queryForObject(anyString(), any(RowMapper.class), eq(resourceId));
  }
}
