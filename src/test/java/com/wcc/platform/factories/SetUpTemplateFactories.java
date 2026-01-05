package com.wcc.platform.factories;

import com.wcc.platform.domain.template.Template;
import com.wcc.platform.domain.template.TemplateRequest;
import com.wcc.platform.domain.template.TemplateType;
import java.util.Map;

/** Template set-up factories. */
public class SetUpTemplateFactories {

  /** Creates a feedback template.* */
  public static Template createFeedbackTemplate() {
    var subject = "Request: feedback from {{mentorName}} for {{menteeName}}";
    var body = "Hi {{mentorName}},Test feedback for the {{menteeName}}.";
    return new Template(subject, body);
  }

  /** Creates a template request.* */
  public static TemplateRequest createTemplateRequest(
      final TemplateType type, final Map<String, String> parameters) {
    return new TemplateRequest(type, parameters);
  }
}
