package com.wcc.platform.service;

import com.wcc.platform.domain.exceptions.DuplicatedMemberException;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.repository.MenteeRepository;
import java.util.List;
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
        menteeRepository.findById(mentee.getId())
            .ifPresent(existing -> {
                throw new DuplicatedMemberException(String.valueOf(existing.getId()));
            });
        return menteeRepository.create(mentee);
    }

    /**
     * Return all stored mentees.
     *
     * @return List of mentees.
     */
    public List<Mentee> getAllMentees() {
        final var allMentees = menteeRepository.getAll();
        if (allMentees == null) {
            return List.of();
        }
        return allMentees;
    }
}
