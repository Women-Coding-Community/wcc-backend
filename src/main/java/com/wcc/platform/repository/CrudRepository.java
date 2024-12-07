package com.wcc.platform.repository;

import java.util.Collection;
import java.util.Optional;

/** Simple Crud method to be implemented for all repositories. */
public interface CrudRepository<T, K> {
  T create(T entity);

  T update(K id, T entity);

  Collection<T> findAll();

  Optional<T> findById(K id);

  void deleteById(K id);
}
