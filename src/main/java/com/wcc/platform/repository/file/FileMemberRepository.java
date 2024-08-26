package com.wcc.platform.repository.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.exceptions.FileRepositoryException;
import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.repository.MemberRepository;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;

/** FileMemberRepository class to write/read member's data to/from file repository. */
public class FileMemberRepository implements MemberRepository {

  private static final String FILE_NAME = "members.json";
  private final ObjectMapper objectMapper;
  private final File file;

  /**
   * FileMemberRepository constructor.
   *
   * @param objectMapper objectMapper for reading/writing of JSON
   * @param directoryPath path to the data folder where the file is saved
   */
  public FileMemberRepository(
      final ObjectMapper objectMapper,
      @Value("${file.storage.directory}") final String directoryPath) {
    this.objectMapper = objectMapper;
    file = new File(directoryPath + File.separator + FILE_NAME);
  }

  /**
   * Save member to temporary variable?.
   *
   * @param member member to be saved to file
   * @return Member pojo
   */
  @Override
  public Member save(final Member member) {
    var members = getAll();
    members.add(member);

    writeFile(members);

    return member;
  }

  /**
   * Read all members from file.
   *
   * @return list of members
   */
  @Override
  public List<Member> getAll() {
    try {
      if (!file.exists() || file.length() == 0) {
        return new ArrayList<>();
      }
      return objectMapper.readValue(file, new TypeReference<>() {});
    } catch (IOException e) {
      throw new FileRepositoryException(e.getMessage(), e);
    }
  }

  /**
   * Write list of members to the file.
   *
   * @param list list of members
   */
  private void writeFile(final List<Member> list) {
    try {
      objectMapper.writeValue(file, list);
    } catch (IOException e) {
      throw new FileRepositoryException(e.getMessage(), e);
    }
  }
}
