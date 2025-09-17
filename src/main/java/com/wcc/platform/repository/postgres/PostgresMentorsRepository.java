package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.repository.MentorsRepository;
import java.util.List;
import java.util.Optional;

public class PostgresMentorsRepository implements MentorsRepository {

  @Override
  public Optional<Mentor> findByEmail(String email) {
    return Optional.empty();
  }

  @Override
  public List<Mentor> getAll() {
    return List.of();
  }

  @Override
  public Long findIdByEmail(String email) {
    return 0L;
  }

  @Override
  public Mentor create(Mentor entity) {
    return null;
  }

  @Override
  public Mentor update(Long id, Mentor entity) {
    return null;
  }

  @Override
  public Optional<Mentor> findById(Long id) {
    return Optional.empty();
  }

  @Override
  public void deleteById(Long id) {}
}
