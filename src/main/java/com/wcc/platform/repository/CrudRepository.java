package com.wcc.platform.repository;

import java.util.Optional;

/** Simple Crud method to be implemented for all repositories. */
public interface CrudRepository<T, K> {
  T create(T entity);

  T update(K id, T entity);

  Optional<T> findById(K id);

  void deleteById(K id);
  
}
