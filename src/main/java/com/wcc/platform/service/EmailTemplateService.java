package com.wcc.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailTemplateService {
  private static final String TEMPLATE_PATH = "email-templates/";
  private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{(.*?)}}");

  private final ObjectMapper objectMapper;

  public RenderedTemplate renderTemplate(TemplateType templateType, Map<String, String> params) {
    Template template = loadTemplate(templateType);
    validateTemplateParams(template, params);
    return RenderedTemplate.from(replacePlaceholders(template, params));
  }

  private Template loadTemplate(TemplateType templateType) {
    try {
      ClassPathResource resource =
          new ClassPathResource(TEMPLATE_PATH + templateType.getTemplateFile());
      ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
      Map<String, Template> templates =
          yamlMapper.readValue(
              resource.getInputStream(),
              yamlMapper
                  .getTypeFactory()
                  .constructMapType(Map.class, String.class, Template.class));

      return templates.get(templateType.name());
    } catch (IOException e) {
      log.error("Failed to load template: {}", templateType, e);
      throw new RuntimeException("Failed to load template", e);
    }
  }

  private void validateTemplateParams(Template template, Map<String, String> params) {
    Set<String> requiredParams = extractPlaceholders(template);
    Set<String> missingParams =
        requiredParams.stream()
            .filter(param -> !params.containsKey(param))
            .collect(Collectors.toSet());

    if (!missingParams.isEmpty()) {
      throw new TemplateValidationException("Missing required parameters: " + missingParams);
    }
  }

  private Set<String> extractPlaceholders(Template template) {
    Set<String> placeholders = extractPlaceholdersFromText(template.getSubject());
    placeholders.addAll(extractPlaceholdersFromText(template.getBody()));
    return placeholders;
  }

  private Set<String> extractPlaceholdersFromText(String text) {
    Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
    return matcher.results().map(result -> result.group(1)).collect(Collectors.toSet());
  }

  private Template replacePlaceholders(Template template, Map<String, String> params) {
    Template rendered = new Template();
    rendered.setSubject(replacePlaceholdersInText(template.getSubject(), params));
    rendered.setBody(replacePlaceholdersInText(template.getBody(), params));
    return rendered;
  }

  private String replacePlaceholdersInText(String text, Map<String, String> params) {
    String result = text;
    for (Map.Entry<String, String> entry : params.entrySet()) {
      result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
    }
    return result;
  }
}
