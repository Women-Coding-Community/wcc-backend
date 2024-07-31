package com.wcc.platform.repository.file;

import static com.wcc.platform.repository.file.config.RepositoryConfigFile.MEMBERS_FILE;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.utils.FileUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** FileMemberRepository class to write/read member's data to/from file repository. */
public class FileMemberRepository implements MemberRepository {

  private final ObjectMapper objectMapper;
  private final File file;
  private List<Member> members;

  public FileMemberRepository(final ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    file = Path.of(FileUtil.getFileUri(MEMBERS_FILE.getFileName())).toFile();
  }

  /**
   * Save member to file
   *
   * @param member member to be saved to file
   * @return member pojo
   */
  @Override
  public Member save(final Member member) {
    members = getAll();
    members.add(member);

    writeFile(members);

    return member;
  }

  /**
   * Read all members from file
   *
   * @return list of members
   */
  @Override
  public List<Member> getAll() {
    try {
      if (file.length() > 0) {
        members = objectMapper.readValue(file, new TypeReference<List<Member>>() {});
      } else {
        members = new ArrayList<>();
      }
      return members;
    } catch (IOException e) {
      // todo: FileRepositoryException - create
      throw new PlatformInternalException(e.getMessage(), e);
    }
  }

  /**
   * Write list of members to the file
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
