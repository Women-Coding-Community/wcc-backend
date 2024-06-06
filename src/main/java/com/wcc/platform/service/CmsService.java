package com.wcc.platform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.LeadershipMember;
import com.wcc.platform.domain.pages.TeamPage;
import com.wcc.platform.domain.pages.CodeOfConductPage;
import com.wcc.platform.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CmsService {
    private final ObjectMapper objectMapper;

    @Autowired
    public CmsService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public TeamPage getTeam() {
        String teamPage = FileUtil.readFileAsString("teamPage.json");

        try {
            return objectMapper.readValue(teamPage, TeamPage.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public LeadershipMember getMember() {
        String teamPage = FileUtil.readFileAsString("member.json");

        try {
            return objectMapper.readValue(teamPage, LeadershipMember.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public CodeOfConductPage getCodeOfConduct() {
        String codeOfConductPage = FileUtil.readFileAsString("codeOfConductPage.json");

        try {
            return objectMapper.readValue(codeOfConductPage, CodeOfConductPage.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}