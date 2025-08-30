package com.wcc.platform.repository.postgres.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.repository.postgres.PostgresCountryRepository;
import com.wcc.platform.repository.postgres.PostgresMemberImageRepository;
import com.wcc.platform.repository.postgres.PostgresMemberMemberTypeRepository;
import com.wcc.platform.repository.postgres.PostgresSocialNetworkRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Unit tests for the MemberMapper class, which maps database rows to Member objects and handles
 * member-related database operations.
 */
class MemberMapperTest {
  @Mock private JdbcTemplate jdbc;
  @Mock private PostgresCountryRepository countryRepository;
  @Mock private PostgresMemberMemberTypeRepository memberTypeRepo;
  @Mock private PostgresMemberImageRepository imageRepository;
  @Mock private PostgresSocialNetworkRepository socialNetworkRepo;

  @InjectMocks private MemberMapper memberMapper;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    memberMapper =
        new MemberMapper(
            jdbc, countryRepository, memberTypeRepo, imageRepository, socialNetworkRepo);
  }

  @Test
  void testMapRowToMember() throws SQLException {
    ResultSet resultSet = mock(ResultSet.class);
    when(resultSet.getLong("id")).thenReturn(1L);
    when(resultSet.getLong("country_id")).thenReturn(2L);
    when(resultSet.getString("full_name")).thenReturn("John Doe");
    when(resultSet.getString("position")).thenReturn("Developer");
    when(resultSet.getString("email")).thenReturn("john@example.com");
    when(resultSet.getString("slack_name")).thenReturn("johnny");
    when(resultSet.getString("city")).thenReturn("London");
    when(resultSet.getString("company_name")).thenReturn("WCC");

    Country country = mock(Country.class);
    when(countryRepository.findById(2L)).thenReturn(Optional.of(country));
    when(memberTypeRepo.findByMemberId(1L)).thenReturn(Collections.emptyList());
    when(imageRepository.findByMemberId(1L)).thenReturn(Collections.emptyList());
    when(socialNetworkRepo.findByMemberId(1L)).thenReturn(Collections.emptyList());

    Member member = memberMapper.mapRowToMember(resultSet);

    assertEquals("John Doe", member.getFullName());
    assertEquals("Developer", member.getPosition());
    assertEquals("john@example.com", member.getEmail());
    assertEquals("johnny", member.getSlackDisplayName());
    assertEquals(country, member.getCountry());
    assertEquals("London", member.getCity());
    assertEquals("WCC", member.getCompanyName());
    assertNotNull(member.getMemberTypes());
    assertNotNull(member.getImages());
    assertNotNull(member.getNetwork());
  }

  @Test
  void testAddMember() {
    Member member = mock(Member.class);
    when(member.getFullName()).thenReturn("Jane Doe");
    when(member.getSlackDisplayName()).thenReturn("jane");
    when(member.getPosition()).thenReturn("Manager");
    when(member.getCompanyName()).thenReturn("WCC");
    when(member.getEmail()).thenReturn("jane@example.com");
    when(member.getCity()).thenReturn("Amsterdam");
    Country country = mock(Country.class);
    when(member.getCountry()).thenReturn(country);
    when(countryRepository.findCountryIdByCode(anyString())).thenReturn(3L);

    when(jdbc.queryForObject(
            anyString(), eq(Long.class), any(), any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(10L);

    when(member.getImages()).thenReturn(Collections.emptyList());
    when(member.getMemberTypes()).thenReturn(Collections.emptyList());
    when(member.getNetwork()).thenReturn(Collections.emptyList());

    Long memberId = memberMapper.addMember(member, "insert sql");

    assertEquals(10L, memberId);
    verify(imageRepository, never()).addMemberImage(anyLong(), any());
    verify(memberTypeRepo, never()).addMemberType(anyLong(), anyLong());
    verify(socialNetworkRepo, never()).addSocialNetwork(anyLong(), any());
  }

  @Test
  void testUpdateMember() {
    Member member = mock(Member.class);
    when(member.getFullName()).thenReturn("Jane Doe");
    when(member.getSlackDisplayName()).thenReturn("jane");
    when(member.getPosition()).thenReturn("Manager");
    when(member.getCompanyName()).thenReturn("WCC");
    when(member.getEmail()).thenReturn("jane@example.com");
    when(member.getCity()).thenReturn("Amsterdam");
    Country country = mock(Country.class);
    when(member.getCountry()).thenReturn(country);
    when(countryRepository.findCountryIdByCode(anyString())).thenReturn(3L);

    when(member.getImages()).thenReturn(Collections.emptyList());
    when(member.getMemberTypes()).thenReturn(Collections.emptyList());
    when(member.getNetwork()).thenReturn(Collections.emptyList());

    memberMapper.updateMember(member, "update sql", 20L);

    verify(jdbc).update(anyString(), any(), any(), any(), any(), any(), any(), any(), any());
    verify(memberTypeRepo).deleteByMemberId(20L);
    verify(imageRepository).deleteMemberImage(20L);
    verify(socialNetworkRepo).deleteByMemberId(20L);
  }
}
