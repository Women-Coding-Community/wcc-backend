package com.wcc.platform.repository.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.wcc.platform.repository.file.config.RepositoryConfigFile.MEMBERS_FILE;

public class FileMemberRepository implements MemberRepository {

    private final ObjectMapper objectMapper;
    private List<Member> listOfMembers;
    private File file;

    public FileMemberRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

        try {
            this.file = Path.of(FileUtil.getFileUri(MEMBERS_FILE.getFileName())).toFile();
            //listOfMembers = objectMapper.readValue(file, List.class);

            if (this.file.length() > 0) {
                this.listOfMembers = this.objectMapper.readValue(this.file, new TypeReference<List<Member>>() {
                });
            } else {
                this.listOfMembers = new ArrayList<>();
            }
        } catch (IOException e) {
            //todo: FileRepositoryException - create
            throw new PlatformInternalException(e.getMessage(), e);
        }
    }

    @Override
    public Member save(Member member) {
        listOfMembers.add(member);
        writeFile(listOfMembers);
        return member;
    }

    @Override
    public List<Member> getAll() {
        try {
            List<Member> list = objectMapper.readValue(file, new TypeReference<List<Member>>() {
            });
            return list;
        } catch (IOException e) {
            // todo: FileRepositoryException - create
            throw new PlatformInternalException(e.getMessage(), e);
        }
    }

    private void writeFile(List<Member> list) {
        try {
            objectMapper.writeValue(file, list);
        } catch (IOException e) {
            //todo: FileRepositoryException - create
            throw new PlatformInternalException(e.getMessage(), e);
        }
    }


}
