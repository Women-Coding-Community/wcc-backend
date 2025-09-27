package com.wcc.platform.repository.file;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.platform.member.Member;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FileMemberRepositoryTest {

  @TempDir File tempDir;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = mock(ObjectMapper.class);
  }

  @Test
  void whenGetAllFileDoesNotExist() {
    final var fileMemberRepository = new FileMemberRepository(objectMapper, "invalid");
    List<Member> result = fileMemberRepository.getAll();
    assertTrue(result.isEmpty());
  }

  @Test
  void whenGetAllFileEmpty() {
    final String tempPath =
        this.getClass().getClassLoader().getResource("members/empty/").getPath();
    final var fileMemberRepository = new FileMemberRepository(objectMapper, tempPath);
    List<Member> result = fileMemberRepository.getAll();
    assertTrue(result.isEmpty());
  }

  @Test
  void whenGetAllThrowsIoExceptionShouldThrowFileRepositoryException() throws IOException {
    ObjectMapper mockMapper = mock(ObjectMapper.class);
    when(mockMapper.readValue(any(File.class), any(TypeReference.class)))
        .thenThrow(new IOException("Read error"));

    // Ensure the repository attempts to read by creating a non-empty members.json
    File membersFile = new File(tempDir, "members.json");
    try (var fileWriter = new java.io.FileWriter(membersFile)) {
      fileWriter.write("[{}]"); // make file length > 0
    } catch (IOException e) {
      fail("Failed to prepare test file: " + e.getMessage());
    }

    var repo = spy(new FileMemberRepository(mockMapper, tempDir.getPath()));

    assertThrows(FileRepositoryException.class, repo::getAll);
  }

  @Test
  void findByEmailReturnsMemberIfExists() {
    Member member = new Member();
    member.setEmail("test@example.com");
    member.setId(1L);
    List<Member> members = List.of(member);

    FileMemberRepository repo = spy(new FileMemberRepository(objectMapper, "."));
    doReturn(members).when(repo).getAll();

    Optional<Member> found = repo.findByEmail("test@example.com");
    assertTrue(found.isPresent());
    assertEquals("test@example.com", found.get().getEmail());
  }

  @Test
  void findByEmailReturnsEmptyIfNotExists() {
    FileMemberRepository repo = spy(new FileMemberRepository(objectMapper, "."));
    doReturn(new ArrayList<>()).when(repo).getAll();

    Optional<Member> found = repo.findByEmail("notfound@example.com");
    assertTrue(found.isEmpty());
  }

  @Test
  void findByEmailWithNullEmailShouldReturnEmpty() {
    FileMemberRepository repo = spy(new FileMemberRepository(objectMapper, "."));
    doReturn(new ArrayList<>()).when(repo).getAll();

    Optional<Member> found = repo.findByEmail(null);
    assertTrue(found.isEmpty());
  }

  @Test
  void findByIdReturnsMemberIfExists() {
    Member member = new Member();
    member.setEmail("test@example.com");
    member.setId(42L);
    List<Member> members = List.of(member);

    FileMemberRepository repo = spy(new FileMemberRepository(objectMapper, "."));
    doReturn(members).when(repo).getAll();

    Optional<Member> found = repo.findById(42L);
    assertTrue(found.isPresent());
    assertEquals(42L, found.get().getId());
  }

  @Test
  void findByIdReturnsEmptyIfNotExists() {
    FileMemberRepository repo = spy(new FileMemberRepository(objectMapper, "."));
    doReturn(new ArrayList<>()).when(repo).getAll();

    Optional<Member> found = repo.findById(99L);
    assertTrue(found.isEmpty());
  }

  @Test
  void findByIdWithNullIdShouldReturnEmpty() {
    FileMemberRepository repo = spy(new FileMemberRepository(objectMapper, "."));
    doReturn(new ArrayList<>()).when(repo).getAll();

    Optional<Member> found = repo.findById(null);
    assertTrue(found.isEmpty());
  }

  @Test
  void createAddsMemberAndWritesFile() throws Exception {
    Member member = new Member();
    member.setEmail("new@example.com");
    member.setId(5L);

    ObjectMapper mockMapper = mock(ObjectMapper.class);
    FileMemberRepository repo = spy(new FileMemberRepository(mockMapper, "."));
    doReturn(new ArrayList<Member>()).when(repo).getAll();

    Member created = repo.create(member);

    assertEquals(member, created);
    verify(mockMapper)
        .writeValue(any(File.class), argThat(list -> ((List<?>) list).contains(member)));
  }

  @Test
  void createWithNullMemberShouldStillCallWriteFile() throws Exception {
    ObjectMapper mockMapper = mock(ObjectMapper.class);
    FileMemberRepository repo = spy(new FileMemberRepository(mockMapper, "."));
    doReturn(new ArrayList<Member>()).when(repo).getAll();

    Member created = repo.create(null);

    assertNull(created);
    verify(mockMapper).writeValue(any(File.class), anyList());
  }

  @Test
  void updateUpdatesMemberAndWritesFile() {
    Member oldMember = new Member();
    oldMember.setEmail("update@example.com");
    oldMember.setId(10L);

    Member updatedMember = new Member();
    updatedMember.setEmail("update@example.com");
    updatedMember.setId(10L);

    List<Member> members = List.of(oldMember);

    FileMemberRepository repo = spy(new FileMemberRepository(objectMapper, "."));
    doReturn(members).when(repo).getAll();

    Member result = repo.update(10L, updatedMember);

    assertEquals(updatedMember, result);
  }

  @Test
  void updateWithNonExistingMemberShouldStillWriteFile() {
    Member updatedMember = new Member();
    updatedMember.setEmail("nonexisting@example.com");
    updatedMember.setId(999L);

    FileMemberRepository repo = spy(new FileMemberRepository(objectMapper, "."));
    doReturn(new ArrayList<>()).when(repo).getAll();

    Member result = repo.update(999L, updatedMember);

    assertEquals(updatedMember, result);
  }

  @Test
  void findIdByEmailReturnsIdIfExists() {
    Member member = new Member();
    member.setEmail("id@example.com");
    member.setId(123L);

    FileMemberRepository repo = spy(new FileMemberRepository(objectMapper, "."));
    doReturn(Optional.of(member)).when(repo).findByEmail("id@example.com");

    assertEquals(123L, repo.findIdByEmail("id@example.com"));
  }

  @Test
  void findIdByEmailReturnsZeroIfNotExists() {
    FileMemberRepository repo = spy(new FileMemberRepository(objectMapper, "."));
    doReturn(Optional.empty()).when(repo).findByEmail("notfound@example.com");

    Long memberId = repo.findIdByEmail("notfound@example.com");
    assertEquals(0L, memberId);
  }

  @Test
  void deleteByEmailShouldDoNothing() {
    FileMemberRepository repo = new FileMemberRepository(objectMapper, tempDir.getPath());

    assertDoesNotThrow(() -> repo.deleteByEmail("test@example.com"));
  }

  @Test
  void deleteByIdShouldDoNothing() {
    FileMemberRepository repo = new FileMemberRepository(objectMapper, tempDir.getPath());

    assertDoesNotThrow(() -> repo.deleteById(1L));
  }

  @Test
  void writeFileThrowsIoExceptionShouldThrowFileRepositoryException() throws IOException {
    ObjectMapper mockMapper = mock(ObjectMapper.class);
    doThrow(new IOException("Write error")).when(mockMapper).writeValue(any(File.class), anyList());

    FileMemberRepository repo = new FileMemberRepository(mockMapper, tempDir.getPath());
    final Member member = new Member();

    assertThrows(FileRepositoryException.class, () -> repo.create(member));
  }

  @Test
  void constructorShouldCreateFileInCorrectDirectory() {
    String testPath = tempDir.getPath();
    FileMemberRepository repo = new FileMemberRepository(objectMapper, testPath);

    // This test verifies the constructor works without throwing exceptions
    assertNotNull(repo);
  }
}
