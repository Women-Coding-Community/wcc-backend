package com.wcc.platform.repository.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.repository.MemberRepository;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
   * Save new member.
   *
   * @param member member to be saved to file
   * @return Member pojo
   */
  @Override
  public Member save(final Member member) {
    final var members = getAll();
    members.add(member);

    writeFile(members);

    return member;
  }

  /**
   * Update an existing member.
   *
   * @param updatedMember member with updated fields
   * @return updated member
   */
  @Override
  public Member update(final Member updatedMember) {
    List<Member> members = getAll();

    final var updatedMembers =
        members.stream()
            .map(
                member -> {
                  if (member.getEmail().equals(updatedMember.getEmail())) {
                    return updatedMember;
                  }
                  return member;
                })
            .toList();

    writeFile(updatedMembers);
    return updatedMember;
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
   * Find member by email.
   *
   * @param email member's email as unique identifier
   * @return Optional with Member object or empty Optional
   */
  @Override
  public Optional<Member> findByEmail(final String email) {
    final var members = getAll();

    if (!members.isEmpty()) {
      final Member existingMember =
          members.stream()
              .filter(member -> member.getEmail().equals(email))
              .findFirst()
              .orElse(null);

      return Optional.ofNullable(existingMember);
    }
    return Optional.empty();
  }

  /**
   * Write list of members to file.
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
