package com.wcc.platform.repository.file;

import static com.wcc.platform.repository.file.config.RepositoryConfigFile.MEMBERS_FILE;

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

public class FileMemberRepository implements MemberRepository {

    private final ObjectMapper objectMapper;
    private List<Member> members;
    private File file;

    public FileMemberRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        file = Path.of(FileUtil.getFileUri(MEMBERS_FILE.getFileName())).toFile();
    }

    @Override
    public void save(Member member) {
        members = getAll();
        members.add(member);
        writeFile(members);
    }

    @Override
    public List<Member> getAll() {
        try {
            if (file.length() > 0) {
                members = objectMapper.readValue(
                    file,
                    new TypeReference<List<Member>>() {
                    });
            } else {
                members = new ArrayList<>();
            }
            return members;
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
