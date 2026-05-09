package com.wcc.platform.domain.auth;

import lombok.AllArgsConstructor;

/** Enum representing permissions for different actions within the platform. */
@AllArgsConstructor
public enum Permission {
  // User Management
  USER_READ("user:read", "View user information"),
  USER_WRITE("user:write", "Create and update users"),
  USER_DELETE("user:delete", "Delete users"),

  // Member Management
  MEMBER_READ("member:read", "View member information"),
  MEMBER_WRITE("member:write", "Create and update member information"),
  MEMBER_DELETE("member:delete", "Delete member information"),

  // Mentor Operations
  MENTOR_APPL_READ("mentor:application:read", "View mentor applications"),
  MENTOR_APPL_WRITE("mentor:application:write", "Accept/decline mentee applications"),
  MENTOR_PROFILE_UPDATE("mentor:profile:update", "Update mentor profile"),

  // Mentee Operations
  MENTEE_APPL_SUBMIT("mentee:application:submit", "Submit mentee applications"),
  MENTEE_APPL_READ("mentee:application:read", "View own application status"),

  // Email Operations
  EMAIL_TEMPLATE_MANAGE("email:template:manage", "Create and update email templates"),
  SEND_EMAIL("email:send", "Send emails to members"),

  // Admin Operations
  MENTOR_APPROVE("admin:mentor:approve", "Approve/reject mentors"),
  MENTEE_APPROVE("admin:mentee:approve", "Approve/reject mentees"),
  CYCLE_EMAIL_SEND("admin:cycle:email", "Send cycle emails"),
  MATCH_MANAGE("admin:match:manage", "Manage mentor-mentee matches");

  private final String code;
  private final String description;
}
