package com.wcc.platform.service;

import com.wcc.platform.domain.exceptions.DuplicatedMemberException;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.repository.MenteeRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class MenteeService {

  private final MenteeRepository menteeRepository;

    public MenteeService(final MenteeRepository menteeRepository) {
        this.menteeRepository = menteeRepository;
    }

    /**
     * Create a mentee record.
     *
     * @return Mentee record created successfully.
     */
    public Mentee create(final Mentee mentee) {
        final Optional<Mentee> menteeExists = menteeRepository.findById(mentee.getId());

        if (menteeExists.isPresent()) {
            throw new DuplicatedMemberException(menteeExists.get().getEmail());
        }
        return menteeRepository.create(mentee);
    }
}
