package com.wcc.platform.repository.file;

import static java.nio.file.Files.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.repository.MemberRepository;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;

/** FileMemberRepository class to write/read member's data to/from file repository. */
public class FileMemberRepository implements MemberRepository {
  private final String FILE_NAME = "members.json";
  private final String FOLDER_NAME = "data";
  private final ObjectMapper objectMapper;
  boolean isRunningInDocker = true;
  private File file;
  private List<Member> members;

  @Value("${file.storage.directory}")
  private String directoryPath;

  /**
   * FileMemberRepository constructor.
   *
   * @param objectMapper objectMapper for reading/writing of JSON
   */
  public FileMemberRepository(final ObjectMapper objectMapper) throws IOException {
    this.objectMapper = objectMapper;
  }

  /**
   * Create file path to the data folder
   *
   * @return file
   */
  private File getFile() {
    isRunningInDocker = System.getenv("RUNNING_IN_DOCKER") != null;
    if (!isRunningInDocker) {
      directoryPath = FOLDER_NAME;
    }
    return new File(directoryPath + File.separator + FILE_NAME);
  }

  /**
   * Save member to temporary variable?.
   *
   * @param member member to be saved to file
   * @return Member pojo
   */
  @Override
  public Member save(final Member member) {
    file = getFile();
    members = getAll();
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
      if (!file.exists()) {
        return new ArrayList<>();
      }
      return objectMapper.readValue(file, new TypeReference<List<Member>>() {});
    } catch (IOException e) {
      // todo: FileRepositoryException - create
      throw new PlatformInternalException(e.getMessage(), e);
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
      // todo: FileRepositoryException - create
      throw new PlatformInternalException(e.getMessage(), e);
    }
  }
}
