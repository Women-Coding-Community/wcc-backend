package com.wcc.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Platform service. */
@Service
public class PlatformService {
  private final ObjectMapper objectMapper;

  @Autowired
  public PlatformService(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  // todo: Move here declaration: public Member createMember(Member member) {} from CmsService
}
