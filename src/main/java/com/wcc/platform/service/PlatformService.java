package com.wcc.platform.service;

import com.wcc.platform.domain.platform.ResourceContent;
import com.wcc.platform.repository.ResourceContentRepository;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/** Platform Service. */
@Service
public class PlatformService {

  private final ResourceContentRepository resourceRepository;

  @Autowired
  public PlatformService(
      @Qualifier("getResourceRepository") final ResourceContentRepository resourceRepository) {
    this.resourceRepository = resourceRepository;
  }

  public ResourceContent saveResourceContent(final ResourceContent resourceContent) {
    return resourceRepository.save(resourceContent);
  }

  public Collection<ResourceContent> getAllResources() {
    return resourceRepository.findAll();
  }
}
