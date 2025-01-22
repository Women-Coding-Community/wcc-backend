package com.wcc.platform.configuration;

import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.client.RestTemplate;

public class GitHubTokenValidator implements JwtDecoder {

  @Override
  public Jwt decode(String token) throws JwtException {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);

    HttpEntity<Void> request = new HttpEntity<>(headers);

    ResponseEntity<Map> response =
        restTemplate.exchange("https://api.github.com/user", HttpMethod.GET, request, Map.class);

    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new JwtException("Invalid token");
    }

    // Parse and return a Jwt object
    Map<String, Object> claims = response.getBody();
    return Jwt.withTokenValue(token)
        .header("alg", "none")
        .claims(existingClaims -> existingClaims.putAll(claims))
        .build();
  }
}
