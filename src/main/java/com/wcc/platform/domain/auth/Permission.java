package com.wcc.platform.domain.auth;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Permission {
  // User Management
  USER_READ("user:read", "View user information"),
  USER_WRITE("user:write", "Create and update users"),
  USER_DELETE("user:delete", "Delete users"),

  // Mentor Operations
  MENTOR_APPL_READ("mentor:application:read", "View mentor applications"),
  MENTOR_APPL_WRITE("mentor:application:write", "Accept/decline mentee applications"),
  MENTOR_PROFILE_UPDATE("mentor:profile:update", "Update mentor profile"),

  // Mentee Operations
  MENTEE_APPL_SUBMIT("mentee:application:submit", "Submit mentee applications"),
  MENTEE_APPL_READ("mentee:application:read", "View own application status"),

  // Admin Operations
  MENTOR_APPROVE("admin:mentor:approve", "Approve/reject mentors"),
  MENTEE_APPROVE("admin:mentee:approve", "Approve/reject mentees"),
  CYCLE_EMAIL_SEND("admin:cycle:email", "Send cycle emails"),
  MATCH_MANAGE("admin:match:manage", "Manage mentor-mentee matches");

  private final String code;
  private final String description;
}
