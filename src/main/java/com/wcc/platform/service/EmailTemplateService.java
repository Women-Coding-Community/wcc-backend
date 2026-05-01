package com.wcc.platform.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.exceptions.TemplateValidationException;
import com.wcc.platform.domain.template.RenderedTemplate;
import com.wcc.platform.domain.template.Template;
import com.wcc.platform.domain.template.TemplateType;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

/** Service for rendering email templates with placeholders replaced by actual values. */
@Slf4j
@Service
public class EmailTemplateService {
  private static final String TEMPLATE_PATH = "email-templates/";
  private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{(.*?)}}");
  private static final String SIGNATURE_KEY = "teamEmailSignature";

  private final ObjectMapper yamlObjectMapper;
  private final String teamEmailSignature;

  public EmailTemplateService(
      final @Qualifier("yamlObjectMapper") ObjectMapper yamlObjectMapper,
      final @Value("${app.email.team-signature}") String teamEmailSignature) {
    this.yamlObjectMapper = yamlObjectMapper;
    this.teamEmailSignature = teamEmailSignature;
  }

  /**
   * Renders an email template by replacing all placeholders with the actual values provided in the
   * parameters map.
   *
   * @param templateType the type of the email template to render
   * @param params a map of placeholder names to their replacement values
   * @return a {@link RenderedTemplate} containing the subject and body with all placeholders
   *     replaced
   * @throws IllegalArgumentException if required placeholders are missing in {@code params}
   */
  public RenderedTemplate renderTemplate(
      final TemplateType templateType, final Map<String, Object> params) {
    final Template template = loadTemplate(templateType);
    final Map<String, Object> mergedParams = mergeWithDefaults(params);
    validateTemplateParams(template, mergedParams);
    return RenderedTemplate.from(replacePlaceholders(template, mergedParams));
  }

  private Map<String, Object> mergeWithDefaults(final Map<String, Object> params) {
    @SuppressWarnings("PMD.UseConcurrentHashMap") // HashMap is used to support null values
    final Map<String, Object> merged = new java.util.HashMap<>();
    merged.put(SIGNATURE_KEY, teamEmailSignature);
    merged.putAll(params);
    return merged;
  }

  private Template loadTemplate(final TemplateType templateType) {
    try {
      final ClassPathResource resource =
          new ClassPathResource(TEMPLATE_PATH + templateType.getTemplateFile());
      final Map<String, Template> templates =
          yamlObjectMapper.readValue(resource.getInputStream(), new TypeReference<>() {});

      return templates.get(templateType.name());
    } catch (IOException e) {
      log.error("Failed to load template: {}", templateType, e);
      throw new IllegalArgumentException("Failed to load template", e);
    }
  }

  private void validateTemplateParams(final Template template, final Map<String, Object> params) {
    final Set<String> requiredParams = extractPlaceholders(template);
    final Set<String> missingParams =
        requiredParams.stream()
            .filter(param -> !params.containsKey(param))
            .collect(Collectors.toSet());

    if (!missingParams.isEmpty()) {
      throw new TemplateValidationException("Missing required parameters: " + missingParams);
    }
  }

  private Set<String> extractPlaceholders(final Template template) {
    final Set<String> placeholders = extractPlaceholdersFromText(template.subject());
    placeholders.addAll(extractPlaceholdersFromText(template.body()));
    return placeholders;
  }

  private Set<String> extractPlaceholdersFromText(final String text) {
    final Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
    return matcher.results().map(result -> result.group(1)).collect(Collectors.toSet());
  }

  private Template replacePlaceholders(final Template template, final Map<String, Object> params) {
    return new Template(
        replacePlaceholdersInText(template.subject(), params),
        replacePlaceholdersInText(template.body(), params));
  }

  private String replacePlaceholdersInText(final String text, final Map<String, Object> params) {
    String result = text;
    for (final Map.Entry<String, Object> entry : params.entrySet()) {
      result = result.replace("{{" + entry.getKey() + "}}", paramToValue(entry.getValue()));
    }
    return result;
  }

  private String paramToValue(final Object paramValue) {
    if (paramValue == null) {
      return StringUtils.EMPTY;
    }

    return paramValue instanceof String ? (String) paramValue : paramValue.toString();
  }
}
