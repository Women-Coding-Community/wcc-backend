package com.wcc.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlatformService {
    private final ObjectMapper objectMapper;

    @Autowired
    public PlatformService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}