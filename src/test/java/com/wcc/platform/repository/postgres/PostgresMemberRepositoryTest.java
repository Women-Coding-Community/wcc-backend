package com.wcc.platform.repository.postgres;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.domain.platform.MemberType;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

/** PostgresMemberRepositoryTest class for testing the PostgresMemberRepository. */
class PostgresMemberRepositoryTest {

  private JdbcTemplate jdbc;
  private PostgresCountryRepository countryRepository;
  private PostgresMemberMemberTypeRepository memberTypeRepository;
  private PostgresMemberRepository repository;

  @BeforeEach
  void setUp() {
    PostgresImageRepository mockImageRepo = mock(PostgresImageRepository.class);
    PostgresSocialNetworkRepository mockSocialNetworkRepo =
        mock(PostgresSocialNetworkRepository.class);
    jdbc = mock(JdbcTemplate.class);
    countryRepository = mock(PostgresCountryRepository.class);
    memberTypeRepository = mock(PostgresMemberMemberTypeRepository.class);
    repository =
        spy(
            new PostgresMemberRepository(
                jdbc,
                countryRepository,
                memberTypeRepository,
                mockImageRepo,
                mockSocialNetworkRepo));
  }

  @Test
  void testFindByEmail() {
    String email = "test@example.com";
    Member member = Member.builder().email(email).fullName("Test User").build();
    when(jdbc.query(anyString(), (ResultSetExtractor<Object>) any(), eq(email)))
        .thenReturn(Optional.of(member));

    Optional<Member> result = repository.findByEmail(email);

    assertNotNull(result);
    assertEquals("Test User", result.get().getFullName());
  }

  @Test
  void testCreate() {
    Member member =
        Member.builder()
            .fullName("Test User")
            .slackDisplayName("testuser")
            .position("Dev")
            .companyName("Company")
            .email("test@example.com")
            .city("City")
            .country(new Country("UK", "United Kingdom"))
            .memberTypes(List.of(MemberType.LEADER))
            .images(List.of())
            .network(List.of())
            .build();

    when(countryRepository.findCountryIdByCode("UK")).thenReturn(1L);
    when(jdbc.queryForObject(anyString(), eq(Long.class), any(Object[].class))).thenReturn(1L);
    when(memberTypeRepository.findIdByType(MemberType.LEADER)).thenReturn(2L);
    doReturn(Optional.of(member)).when(repository).findById(1L);

    Member created = repository.create(member);

    assertEquals("Test User", created.getFullName());
    verify(memberTypeRepository).addMemberType(1L, 2L);
  }
}
