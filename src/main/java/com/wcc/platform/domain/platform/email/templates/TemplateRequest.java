package com.wcc.platform.domain.platform.email.templates;

import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.domain.platform.type.TemplateType;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRequest {

  private TemplateType templateType;
  private MentorshipType mentorshipType;
  private Map<String, Object> params = new HashMap<>();

  /** Convenience mutator for fluent building in tests/usage. */
  public TemplateRequest addParam(String key, Object value) {
    if (this.params == null) this.params = new HashMap<>();
    this.params.put(key, value);
    return this;
  }
}
