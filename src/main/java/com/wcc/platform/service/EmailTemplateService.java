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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailTemplateService {
  private static final String TEMPLATE_PATH = "email-templates/";
  private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{(.*?)}}");

  private final ObjectMapper yamlObjectMapper;

  public EmailTemplateService(final @Qualifier("yamlObjectMapper") ObjectMapper yamlObjectMapper) {
    this.yamlObjectMapper = yamlObjectMapper;
  }

  public RenderedTemplate renderTemplate(
      final TemplateType templateType, final Map<String, String> params) {
    final Template template = loadTemplate(templateType);
    validateTemplateParams(template, params);
    return RenderedTemplate.from(replacePlaceholders(template, params));
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

  private void validateTemplateParams(final Template template, final Map<String, String> params) {
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
    final Set<String> placeholders = extractPlaceholdersFromText(template.getSubject());
    placeholders.addAll(extractPlaceholdersFromText(template.getBody()));
    return placeholders;
  }

  private Set<String> extractPlaceholdersFromText(final String text) {
    final Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
    return matcher.results().map(result -> result.group(1)).collect(Collectors.toSet());
  }

  private Template replacePlaceholders(final Template template, final Map<String, String> params) {
    final Template rendered = new Template();
    rendered.setSubject(replacePlaceholdersInText(template.getSubject(), params));
    rendered.setBody(replacePlaceholdersInText(template.getBody(), params));
    return rendered;
  }

  private String replacePlaceholdersInText(final String text, final Map<String, String> params) {
    String result = text;
    for (final Map.Entry<String, String> entry : params.entrySet()) {
      result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
    }
    return result;
  }
}
