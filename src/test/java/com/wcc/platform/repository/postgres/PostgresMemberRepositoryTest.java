package com.wcc.platform.repository.postgres;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.exceptions.MemberNotFoundException;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.repository.postgres.component.MemberMapper;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

/** PostgresMemberRepositoryTest class for testing the PostgresMemberRepository. */
class PostgresMemberRepositoryTest {

  private JdbcTemplate jdbc;
  private PostgresMemberRepository repository;
  private MemberMapper memberMapper;

  @BeforeEach
  void setUp() {
    jdbc = mock(JdbcTemplate.class);
    memberMapper = mock(MemberMapper.class);
    repository = spy(new PostgresMemberRepository(jdbc, memberMapper));
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
    Member member = getMember();
    when(memberMapper.addMember(any(), anyString())).thenReturn(1L);
    doReturn(Optional.of(member)).when(repository).findById(1L);

    Member result = repository.create(member);

    assertEquals("Test User", result.getFullName());
  }

  @Test
  void testUpdate() {
    Member updatedMember = getMember();
    doNothing().when(memberMapper).updateMember(any(), anyString(), eq(1L));
    doReturn(Optional.of(updatedMember)).when(repository).findById(1L);

    Member result = repository.update(1L, updatedMember);

    assertNotNull(result);
    assertEquals("Test User", result.getFullName());
  }

  @Test
  void testFindById() {
    Long memberId = 1L;
    Member member = getMember();
    when(jdbc.query(anyString(), (ResultSetExtractor<Object>) any(), eq(memberId)))
        .thenReturn(Optional.of(member));

    Optional<Member> result = repository.findById(memberId);

    assertNotNull(result);
    assertEquals("Test User", result.get().getFullName());
  }

  @Test
  void testFindByEmailNotFound() {
    String email = "notfound@example.com";
    when(jdbc.query(anyString(), (ResultSetExtractor<Object>) any(), eq(email)))
        .thenReturn(Optional.empty());

    Optional<Member> result = repository.findByEmail(email);

    assertNotNull(result);
    assertEquals(Optional.empty(), result);
  }

  @Test
  void testFindByIdNotFound() {
    Long memberId = 99L;
    when(jdbc.query(anyString(), (ResultSetExtractor<Object>) any(), eq(memberId)))
        .thenReturn(Optional.empty());

    Optional<Member> result = repository.findById(memberId);

    assertNotNull(result);
    assertEquals(Optional.empty(), result);
  }

  @Test
  void testUpdateNonExistentMember() {
    Member updatedMember = getMember();
    doNothing().when(memberMapper).updateMember(any(), anyString(), eq(2L));
    doReturn(Optional.empty()).when(repository).findById(2L);

    try {
      repository.update(2L, updatedMember);
    } catch (NoSuchElementException e) {
      assertNotNull(e);
    }
  }

  @Test
  void testFindIdByEmail() {
    String email = "test@example.com";
    when(jdbc.queryForObject(anyString(), eq(Long.class), eq(email))).thenReturn(1L);

    Long result = repository.findIdByEmail(email);

    assertEquals(1L, result);
  }

  @Test
  void testFindIdByEmailNotFound() {
    String email = "notfound@example.com";
    when(jdbc.queryForObject(anyString(), eq(Long.class), eq(email)))
        .thenThrow(new MemberNotFoundException("Not found"));

    try {
      repository.findIdByEmail(email);
    } catch (MemberNotFoundException e) {
      assertNotNull(e);
    }
  }

  private Member getMember() {
    return Member.builder()
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
  }
}
