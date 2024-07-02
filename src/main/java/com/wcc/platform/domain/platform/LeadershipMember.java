package com.wcc.platform.domain.platform;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents the core team of the community: {@link MemberType#DIRECTOR}, {@link MemberType#LEADER}
<<<<<<< HEAD
 * and {@link MemberType#EVANGELIST}.
=======
 * and {@link MemberType#EVANGELIST}
>>>>>>> 99dacc76e5849b960f2eae2a18c8c44e2edc091e
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LeadershipMember extends Member {
  
  @JsonIgnore
  private MemberType memberType;

}