package com.wcc.platform.factories;

import com.wcc.platform.domain.template.Template;

public class SetUpTemplateFactories {

  public static Template createFeedbackTemplate() {
    Template template = new Template();
    template.setSubject("Request: feedback from {{mentorName}} for {{menteeName}}");
    template.setBody("Hi {{mentorName}},Test feedback for the {{menteeName}}.");
    return template;
  }
}
