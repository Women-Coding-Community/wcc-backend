package com.wcc.platform.controller.platform;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.configuration.SecurityConfig;
import com.wcc.platform.configuration.TestConfig;
import com.wcc.platform.controller.platform.AuthController.LoginRequest;
import com.wcc.platform.controller.platform.AuthController.LoginResponse;
import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.auth.UserToken;
import com.wcc.platform.domain.platform.member.MemberDto;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.service.AuthService;
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
}
