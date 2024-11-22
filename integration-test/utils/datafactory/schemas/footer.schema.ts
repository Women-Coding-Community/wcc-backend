export const footerSchema = {
    "type": "object",
    "properties": {
      "title": {
        "type": "string",
        "minLength": 1
      },
      "subtitle": {
        "type": "string",
        "minLength": 1
      },
      "description": {
        "type": "string",
        "minLength": 1
      },
      "network": {
        "type": "array",
        "items": {
          "type": "object",
          "properties": {
            "type": {
              "type": "string",
              "enum": ["linkedIn", "twitter", "github", "instagram", "email", "slack"]
            },
            "link": {
              "type": "string", 
              "minLength": 1
            }
          },
          "required": ["type", "link"],
          "additionalProperties": false,
          "if": {
            "properties": {
              "type": { "const": "email" }
            }
          },
          "then": {
            "properties": {
              "link": {
                "pattern": "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"  // Custom email regex
              }
            }
          },
          "else": {
            "properties": {
              "link": {
                "format": "uri",  // Format as URI
                "pattern": "^https?://",  // URL pattern
                "minLength": 1
              }
            }
          }
        },
        "minItems": 6,
        "maxItems": 6,
      },
      "link": {
        "type": "object",
        "properties": {
          "title": {
            "type": "string",
            "minLength": 1
          },
          "label": {
            "type": "string",
            "minLength": 1
          },
          "uri": {
            "type": "string",
            "format": "uri",
            "pattern": "^https?://",  
            "minLength": 1
          }
        },
        "required": [
          "title",
          "label",
          "uri"
        ],
        "additionalProperties": false
      }
    },
    "required": [
      "title",
      "subtitle",
      "description",
      "network",
      "link"
    ],
    "additionalProperties": false
  }
