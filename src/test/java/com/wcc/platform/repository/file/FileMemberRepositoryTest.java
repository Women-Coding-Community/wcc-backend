package com.wcc.platform.repository.file;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.platform.Member;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FileMemberRepositoryTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = Mockito.mock(ObjectMapper.class);
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
}
