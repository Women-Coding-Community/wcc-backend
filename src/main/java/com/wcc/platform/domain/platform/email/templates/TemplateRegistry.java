package com.wcc.platform.domain.platform.email.templates;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.domain.platform.type.TemplateType;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

/**
 * Loads all YAML files found under classpath:email-templates/*.yml and merges them into an
 * in-memory registry.
 *
 * <p>YAML file shape expected: TEMPLATE_TYPE: MENTORSHIP_TYPE: subject: "..." body: | ...
 */
@Component
public class TemplateRegistry {

  private final Map<String, Map<String, TemplateSpec>> registry = new HashMap<>();
  private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

  @PostConstruct
  public void init() {
    try {
      PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
      Resource[] resources = resolver.getResources("classpath:email-templates/*.yml");

      for (Resource r : resources) {
        try (InputStream in = r.getInputStream()) {
          @SuppressWarnings("unchecked")
          Map<String, Object> fileMap = yamlMapper.readValue(in, Map.class);

          if (fileMap == null) continue;

          for (Map.Entry<String, Object> top : fileMap.entrySet()) {
            String templateTypeKey = top.getKey();
            Object byMentorshipObj = top.getValue();
            if (!(byMentorshipObj instanceof Map)) continue;

            Map<String, Object> byMentorship = (Map<String, Object>) byMentorshipObj;
            Map<String, TemplateSpec> existing =
                registry.computeIfAbsent(templateTypeKey, k -> new HashMap<>());

            for (Map.Entry<String, Object> mm : byMentorship.entrySet()) {
              String mentorshipKey = mm.getKey();
              if (existing.containsKey(mentorshipKey)) {
                throw new IllegalStateException(
                    "Duplicate template for "
                        + templateTypeKey
                        + "/"
                        + mentorshipKey
                        + " found in "
                        + r.getFilename());
              }
              Object specObj = mm.getValue();
              if (!(specObj instanceof Map)) continue;
              @SuppressWarnings("unchecked")
              Map<String, Object> specMap = (Map<String, Object>) specObj;
              String subject = specMap.getOrDefault("subject", "").toString();
              String body = specMap.getOrDefault("body", "").toString();
              TemplateSpec ts = new TemplateSpec(subject, body);
              existing.put(mentorshipKey, ts);
            }
          }
        }
      }
      validatePresence();
    } catch (Exception e) {
      throw new IllegalStateException(
          "Failed to load email templates from classpath:email-templates", e);
    }
  }

  private void validatePresence() {
    for (TemplateType tt : TemplateType.values()) {
      if (!registry.containsKey(tt.name())) {
        System.out.println("[TemplateRegistry] Warning: no templates found for " + tt.name());
      }
    }
  }

  /** Lookup by enum names. Returns null if not found. */
  public TemplateSpec get(TemplateType templateType, MentorshipType mentorshipType) {
    if (templateType == null || mentorshipType == null) return null;
    Map<String, TemplateSpec> byMent = registry.get(templateType.name());
    if (byMent == null) return null;
    return byMent.get(mentorshipType.name());
  }

  /** Expose an unmodifiable copy for read-only usage. */
  public Map<String, Map<String, TemplateSpec>> all() {
    return Collections.unmodifiableMap(registry);
  }
}
