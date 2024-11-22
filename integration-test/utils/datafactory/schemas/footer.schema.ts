export const footerSchema = {
    type: "object",
    properties: {
      "title": {
        type: "string",
        minLength: 1
      },
      "subtitle": {
        type: "string",
        minLength: 1
      },
      "description": {
        type: "string",
        minLength: 1
      },
      "network": {
        type: "array",
        items: [
          {
            type: "object",
            properties: {
              type: {
                type: "string",
                minLength: 1,
                enum: ["linkedIn", "twitter", "github", "instagram", "email", "slack"]
              },
              "link": {
                type: "string",
                format: "uri", 
                pattern: "^https?://",
                minLength: 1
              }
            },
            required: [
              "type",
              "link"
            ],
            additionalProperties: false
          },
          ]  
      },
      "link": {
        type: "object",
        properties: {
          "title": {
            type: "string",
            minLength: 1
          },
          "label": {
            type: "string",
            minLength: 1
          },
          "uri": {
            type: "string",
            format: "uri", 
            pattern: "^https?://",
            minLength: 1
          }
        },
        required: [
          "title",
          "label",
          "uri"
        ],
        additionalProperties: false
      }
    },
    required: [
      "title",
      "subtitle",
      "description",
      "network",
      "link"
    ],
    additionalProperties: false
  }

