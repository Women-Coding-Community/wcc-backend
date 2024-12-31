package com.wcc.platform.repository;

import java.util.Map;

/** Generic page repository interface. */
public interface PageRepository extends CrudRepository<Map<String, Object>, String> {
  String ID_PREFIX = "page:";
}
