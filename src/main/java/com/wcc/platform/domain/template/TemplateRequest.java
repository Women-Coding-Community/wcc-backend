package com.wcc.platform.domain.template;

import java.util.Map;

/**
 * TemplateRequest record representing a request to render a template.
 *
 * @param templateType
 * @param params
 */
public record TemplateRequest(TemplateType templateType, Map<String, String> params) {}
