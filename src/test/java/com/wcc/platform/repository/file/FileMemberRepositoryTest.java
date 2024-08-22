package com.wcc.platform.repository.file;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.platform.Member;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FileMemberRepositoryTest {

  private ObjectMapper objectMapper;
  @InjectMocks private FileMemberRepository fileMemberRepository;

  @BeforeEach
  void setUp() {
    objectMapper = Mockito.mock(ObjectMapper.class);
    String mockDirectoryPath = "mockDirPath" + File.separator + "data";
    fileMemberRepository = new FileMemberRepository(objectMapper, mockDirectoryPath);
  }

  @Test
  void whenGetAllFileDoesNotExist() {
    List<Member> result = fileMemberRepository.getAll();
    assertTrue(result.isEmpty());
  }
}
