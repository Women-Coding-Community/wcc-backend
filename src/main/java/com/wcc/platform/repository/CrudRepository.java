package com.wcc.platform.repository;

import java.util.Collection;
import java.util.Optional;

public interface CrudRepository<T, ID> {

  T save(T entity);

  Collection<T> findAll();

  Optional<T> findById(ID id);
}
