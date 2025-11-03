package com.wcc.platform.domain.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Data;

@Data
public class TemplateRequest {
  @JsonProperty("templateType")
  private TemplateType templateType;

  @JsonProperty("params")
  private Map<String, String> params;
}
