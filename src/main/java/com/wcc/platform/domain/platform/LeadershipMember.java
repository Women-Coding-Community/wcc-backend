package com.wcc.platform.domain.platform;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the core team of the community: {@link MemberType#DIRECTOR}, {@link MemberType#LEADER} and
 * {@link MemberType#EVANGELIST}
 */
@Data
@NoArgsConstructor
public class LeadershipMember extends Member {

    @JsonIgnore
    private MemberType memberType;

}