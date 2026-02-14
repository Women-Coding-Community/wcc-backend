package com.wcc.platform.domain.email;

import com.wcc.platform.domain.template.TemplateType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for sending an email using template")
public class TemplateEmailRequest {
    @NotBlank(message = "Recipient email is required")
    @Email(message = "Recipient email must be valid")
    @Schema(description = "Recipient email address", example = "recipient@example.com")
    private String to;

    @Schema(description = "CC recipients", example = "[\"cc1@example.com\", \"cc2@example.com\"]")
    private List<String> cc;

    @Schema(description = "BCC recipients", example = "[\"bcc@example.com\"]")
    private List<String> bcc;

    @NotBlank(message = "Template type is required")
    private TemplateType templateType;

    @NotBlank(message = "Template parameters are required")
    @Schema(description = "Key-value pairs used to replace placeholders inside the email template",
        example = """
    {
      "mentorName": "Mike",
      "program": "Ad-hoc mentorship",
      "menteeName": "Alice",
      "deadline": "27/09/25"
    }
    """)
    private Map<String, String> templateParameters;

    @Schema(
        description = "Whether the email body contains HTML content",
        example = "false",
        defaultValue = "false")
    private boolean html;

    @Schema(description = "Reply-to email address", example = "noreply@womencodingcommunity.com")
    private String replyTo;
}
