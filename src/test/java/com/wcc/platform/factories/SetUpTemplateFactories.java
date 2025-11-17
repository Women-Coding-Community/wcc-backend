package com.wcc.platform.factories;

import com.wcc.platform.domain.template.Template;
import com.wcc.platform.domain.template.TemplateRequest;
import com.wcc.platform.domain.template.TemplateType;
import java.util.Map;

public class SetUpTemplateFactories {

  public static Template createFeedbackTemplate() {
    Template template = new Template();
    template.setSubject("Request: feedback from {{mentorName}} for {{menteeName}}");
    template.setBody("Hi {{mentorName}},Test feedback for the {{menteeName}}.");
    return template;
  }

  public static TemplateRequest createTemplateRequest(
      final TemplateType type, final Map<String, String> parameters) {
    TemplateRequest request = new TemplateRequest();
    request.setTemplateType(type);
    request.setParams(parameters);
    return request;
  }
}
