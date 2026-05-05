package com.wcc.platform.controller.platform;

import static com.wcc.platform.domain.auth.Permission.MEMBER_WRITE;
import static com.wcc.platform.domain.auth.Permission.USER_WRITE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.configuration.SecurityConfig;
import com.wcc.platform.configuration.TestConfig;
import com.wcc.platform.configuration.security.RequiresPermission;
import com.wcc.platform.controller.platform.AuthController.ConfirmPasswordResetRequest;
import com.wcc.platform.controller.platform.AuthController.LoginRequest;
import com.wcc.platform.controller.platform.AuthController.ResetPasswordRequest;
import com.wcc.platform.domain.auth.LoginResponse;
import com.wcc.platform.domain.auth.UpdateUserRolesRequest;
import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.auth.UserToken;
import com.wcc.platform.domain.exceptions.InvalidTokenException;
import com.wcc.platform.domain.exceptions.MemberNotFoundException;
import com.wcc.platform.domain.platform.member.MemberDto;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.service.AuthService;
import com.wcc.platform.service.MemberService;
import com.wcc.platform.service.PasswordResetService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/** Unit test for about page apis. */
@ActiveProfiles("test")
@Import({SecurityConfig.class, TestConfig.class})
@WebMvcTest(AuthController.class)
class AuthControllerTest {

  private static final String LOGIN_PATH = "/api/auth/login";
  private static final String AUTHENTICATED_PATH = "/api/auth/me";
  private static final String USERS_PATH = "/api/auth/users";
  private static final String RESET_REQUEST_PATH = "/api/auth/reset-password/request";
  private static final String RESET_CONFIRM_PATH = "/api/auth/reset-password/confirm";
  private static final String API_KEY_HEADER = "X-API-KEY";
  private static final String API_KEY_VALUE = "test-api-key";

  private final UserToken userToken =
      UserToken.builder()
          .token("newAccessToken")
          .userId(1)
          .expiresAt(OffsetDateTime.now().plusMinutes(40))
          .build();
  private final LoginResponse response =
      new LoginResponse(
          userToken.getToken(),
          userToken.getExpiresAt().toString(),
          List.of(RoleType.ADMIN),
          new MemberDto());

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private AuthService authService;
  @MockBean private MemberService memberService;
  @MockBean private PasswordResetService passwordResetService;

  @Test
  @DisplayName(
      "Given valid login credentials When POST /api/auth/login Then return 200 and token response")
  void givenValidLoginWhenLoginThenReturnToken() throws Exception {
    var request = new LoginRequest("admin@wcc.dev", "password123");
    when(authService.authenticateAndIssueToken(request.email(), request.password()))
        .thenReturn(Optional.of(userToken));

    UserAccount userAccount = mock(UserAccount.class);
    when(userAccount.getRoles()).thenReturn(List.of(RoleType.ADMIN));
    when(userAccount.getMemberId()).thenReturn(1L);
    when(authService.findUserByEmail(request.email())).thenReturn(Optional.of(userAccount));
    when(authService.getMember(1L)).thenReturn(new MemberDto());

    mockMvc
        .perform(
            post(LOGIN_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value(response.token()))
        .andExpect(jsonPath("$.expiresAt").value(response.expiresAt()));
  }

  @Test
  @DisplayName("Given user is not found When POST /auth/login Then return 401 Unauthorized")
  void givenInvalidLoginWhenLoginThenReturnUnauthorized() throws Exception {
    LoginRequest loginRequest = new LoginRequest("user@wcc.dev", "somePwd");
    when(authService.authenticateAndIssueToken(any(String.class), any(String.class)))
        .thenReturn(Optional.of(userToken));
    when(authService.findUserByEmail(any(String.class))).thenReturn(Optional.empty());

    mockMvc
        .perform(
            post(LOGIN_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Given token was not generated When GET /auth/me Then return 401 Unauthorized")
  void givenInvalidRefreshTokenWhenRefreshThenReturnUnauthorized() throws Exception {
    when(authService.findUserByEmail(anyString())).thenReturn(Optional.empty());

    mockMvc
        .perform(get(AUTHENTICATED_PATH).header("Authorization", "Bearer invalidToken"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Given missing Authorization header When GET /auth/me Then return 401 Unauthorized")
  void givenMissingAuthHeaderWhenRefreshThenReturnBadRequest() throws Exception {
    mockMvc.perform(get(AUTHENTICATED_PATH)).andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Given users exist, when getting users, then return 200 OK with user list")
  void shouldReturnUsersWhenCallingGetUsers() throws Exception {
    var userAccount = new UserAccount(1L, "admin@wcc.dev", RoleType.ADMIN);
    when(memberService.getUsers()).thenReturn(List.of(userAccount));

    mockMvc
        .perform(get(USERS_PATH).header(API_KEY_HEADER, API_KEY_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].email").value("admin@wcc.dev"));
  }

  @Test
  @DisplayName(
      "Given requestPasswordReset method, when inspecting its annotations,"
          + " then it should require USER or MEMBER write permission")
  void requireMemberUserWritePermOnPasswordResetRequest() throws NoSuchMethodException {
    var method =
        AuthController.class.getDeclaredMethod("requestPasswordReset", ResetPasswordRequest.class);
    var annotation = method.getAnnotation(RequiresPermission.class);

    assertThat(annotation).isNotNull();
    assertThat(annotation.value()).containsExactlyInAnyOrder(USER_WRITE, MEMBER_WRITE);
  }

  @Test
  @DisplayName(
      "Given valid reset request with API key, when POST /reset-password/request,"
          + " then return 200 with message")
  void shouldReturn200WhenRequestingPasswordReset() throws Exception {
    var request = new ResetPasswordRequest("user@wcc.dev", "Test User");
    when(passwordResetService.requestReset(anyString(), anyString()))
        .thenReturn("Password reset email sent");

    mockMvc
        .perform(
            post(RESET_REQUEST_PATH)
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName(
      "Given valid token and new password, when POST /reset-password/confirm,"
          + " then return 200 with success message")
  void shouldReturn200WhenConfirmingPasswordReset() throws Exception {
    var request = new ConfirmPasswordResetRequest("valid-token", "NewP@ssword1");

    mockMvc
        .perform(
            post(RESET_CONFIRM_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Password has been reset successfully"));
  }

  @Test
  @DisplayName(
      "Given invalid or expired token, when POST /reset-password/confirm,"
          + " then return 400 Bad Request")
  void shouldReturn400WhenConfirmingPasswordResetWithInvalidToken() throws Exception {
    var request = new ConfirmPasswordResetRequest("expired-token", "NewP@ssword1");
    doThrow(new InvalidTokenException("Password reset token is invalid or has expired"))
        .when(passwordResetService)
        .confirmReset(anyString(), anyString());

    mockMvc
        .perform(
            post(RESET_CONFIRM_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName(
      "Given a valid user ID and roles with API key, when PUT /api/auth/users/{id}/roles,"
          + " then return 200 OK with the updated user account")
  void shouldReturn200WhenUpdatingUserRoles() throws Exception {
    var userId = 1;
    var request = new UpdateUserRolesRequest(List.of(RoleType.MENTOR, RoleType.LEADER));
    var updated = new UserAccount(userId, null, "user@wcc.dev", null, request.roles(), true);

    when(authService.updateUserRoles(userId, request.roles())).thenReturn(updated);

    mockMvc
        .perform(
            put(USERS_PATH + "/" + userId + "/roles")
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("user@wcc.dev"));
  }

  @Test
  @DisplayName(
      "Given a user ID that does not exist, when PUT /api/auth/users/{id}/roles,"
          + " then return 404 Not Found")
  void shouldReturn404WhenUpdatingRolesForNonExistentUser() throws Exception {
    var userId = 999;
    var request = new UpdateUserRolesRequest(List.of(RoleType.MENTOR));

    when(authService.updateUserRoles(userId, request.roles()))
        .thenThrow(new MemberNotFoundException("User not found with id: " + userId));

    mockMvc
        .perform(
            put(USERS_PATH + "/" + userId + "/roles")
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName(
      "Given updateUserRoles method, when inspecting its annotations,"
          + " then it should require USER_WRITE permission")
  void shouldRequireUserWritePermissionOnUpdateUserRoles() throws NoSuchMethodException {
    var method =
        AuthController.class.getDeclaredMethod(
            "updateUserRoles", Integer.class, UpdateUserRolesRequest.class);
    var annotation = method.getAnnotation(RequiresPermission.class);

    assertThat(annotation).isNotNull();
    assertThat(annotation.value()).containsExactlyInAnyOrder(USER_WRITE);
  }
}
