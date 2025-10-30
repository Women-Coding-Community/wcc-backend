package com.wcc.platform.domain.platform.email.templates.service;

import com.wcc.platform.domain.platform.email.templates.RenderedTemplate;
import com.wcc.platform.domain.platform.email.templates.TemplateRegistry;
import com.wcc.platform.domain.platform.email.templates.TemplateRequest;
import com.wcc.platform.domain.platform.email.templates.TemplateSpec;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class YamlTemplateWriterService implements TemplateWriterService {

  private static final Pattern PLACEHOLDER =
      Pattern.compile("\\{\\{\\s*([a-zA-Z0-9_.]+)\\s*\\}\\}");
  private final TemplateRegistry templateRegistry;

  @Override
  public RenderedTemplate render(final TemplateRequest request) {
    Objects.requireNonNull(request, "TemplateRequest must not be null");
    if (request.getTemplateType() == null) {
      throw new IllegalArgumentException("templateType is required");
    }
    if (request.getMentorshipType() == null) {
      throw new IllegalArgumentException("mentorshipType is required");
    }

    TemplateSpec spec =
        templateRegistry.get(request.getTemplateType(), request.getMentorshipType());
    if (spec == null) {
      throw new IllegalArgumentException(
          "No template found for " + request.getTemplateType() + "/" + request.getMentorshipType());
    }

    Map<String, Object> params = request.getParams() == null ? Map.of() : request.getParams();

    String subject = renderText(spec.getSubject(), params);
    String body = renderText(spec.getBody(), params);

    return RenderedTemplate.builder().subject(subject).bodyHtml(body).build();
  }

  private String renderText(String template, Map<String, Object> params) {
    if (!StringUtils.hasText(template)) return "";

    Matcher m = PLACEHOLDER.matcher(template);
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      String key = m.group(1);
      Object value = resolveParam(key, params);
      String replacement = renderValue(key, value, params);
      // protect Matcher.appendReplacement from $ signs
      replacement = replacement.replace("$", "\\$");
      m.appendReplacement(sb, replacement);
    }
    m.appendTail(sb);
    return sb.toString();
  }

  /** Resolve nested keys separated by dot. Example: "mentor.name" will attempt to traverse maps. */
  @SuppressWarnings("unchecked")
  private Object resolveParam(String key, Map<String, Object> params) {
    if (params == null) return null;
    if (!key.contains(".")) return params.get(key);

    String[] parts = key.split("\\.");
    Object cur = params.get(parts[0]);
    for (int i = 1; i < parts.length && cur != null; i++) {
      String p = parts[i];
      if (cur instanceof Map) {
        cur = ((Map<String, Object>) cur).get(p);
      } else {
        // cannot traverse further
        return null;
      }
    }
    return cur;
  }

  private String renderValue(String key, Object val, Map<String, Object> params) {
    if (val == null) {
      // support convention: if placeholder ends with List try alt param without "List"
      if (key.endsWith("List")) {
        String alt = key.substring(0, key.length() - 4);
        Object altVal = params.get(alt);
        if (altVal instanceof Collection) {
          return renderHtmlList((Collection<?>) altVal);
        }
      }
      return "";
    }

    if (val instanceof Collection) {
      return renderHtmlList((Collection<?>) val);
    }

    if (val instanceof @SuppressWarnings("unchecked") Map m) {
      // if a Map provided for a scalar placeholder, try common keys name/email or fallback to
      // toString
      if (m.containsKey("name") && m.containsKey("email")) {
        String name = escapeHtml(String.valueOf(m.get("name")));
        String email = escapeHtml(String.valueOf(m.get("email")));
        return name + " &lt;" + email + "&gt;";
      }
      if (m.containsKey("name")) {
        return escapeHtml(String.valueOf(m.get("name")));
      }
      return escapeHtml(m.toString());
    }

    return escapeHtml(String.valueOf(val));
  }

  private String renderHtmlList(Collection<?> items) {
    if (items == null || items.isEmpty()) return "<ul></ul>";
    String inner = items.stream().map(this::renderListItem).collect(Collectors.joining());
    return "<ul>" + inner + "</ul>";
  }

  private String renderListItem(Object item) {
    if (item == null) return "<li></li>";
    if (item instanceof String) {
      return "<li>" + escapeHtml((String) item) + "</li>";
    }
    if (item instanceof @SuppressWarnings("unchecked") Map m) {
      String name = m.getOrDefault("name", "").toString();
      String email = m.getOrDefault("email", "").toString();
      return "<li>" + escapeHtml(name) + " &lt;" + escapeHtml(email) + "&gt;</li>";
    }
    // fallback
    return "<li>" + escapeHtml(item.toString()) + "</li>";
  }

  /**
   * Minimal HTML escaping for inserted values. Keep simple to avoid breaking intended HTML
   * structure. If you accept HTML fragments in params, replace this with a proper sanitizer (e.g.,
   * OWASP HTML Sanitizer).
   */
  private String escapeHtml(String s) {
    if (s == null || s.isEmpty()) return "";
    return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
  }
}
