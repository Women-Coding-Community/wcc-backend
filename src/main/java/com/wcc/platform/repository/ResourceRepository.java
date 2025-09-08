package com.wcc.platform.repository;

import com.wcc.platform.domain.platform.type.ResourceType;
import com.wcc.platform.domain.resource.Resource;
import java.util.List;
import java.util.UUID;

/** Repository interface for managing resources. */
public interface ResourceRepository extends CrudRepository<Resource, UUID> {

  /**
   * Find resources by type.
   *
   * @param resourceType the type of resources to find
   * @return a list of resources of the specified type
   */
  List<Resource> findByType(ResourceType resourceType);

  /**
   * Find resources by name (partial match).
   *
   * @param name the name to search for
   * @return a list of resources with names containing the search term
   */
  List<Resource> findByNameContaining(String name);
}
