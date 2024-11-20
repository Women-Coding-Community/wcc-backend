export const footerSchema = {
    "$schema": "http://json-schema.org/draft-04/schema#",
    "type": "object",
    "properties": {
      "title": {
        "type": "string"
      },
      "subtitle": {
        "type": "string"
      },
      "description": {
        "type": "string"
      },
      "network": {
        "type": "array",
        "items": [
          {
            "type": "object",
            "properties": {
              "type": {
                "type": "string"
              },
              "link": {
                "type": "string",
                "format": "uri", 
                "pattern": "^https?://"
              }
            },
            "required": [
              "type",
              "link"
            ]
          },
          ]  
      },
      "link": {
        "type": "object",
        "properties": {
          "title": {
            "type": "string"
          },
          "label": {
            "type": "string"
          },
          "uri": {
            "type": "string",
            "format": "uri", 
            "pattern": "^https?://"
          }
        },
        "required": [
          "title",
          "label",
          "uri"
        ]
      }
    },
    "required": [
      "title",
      "subtitle",
      "description",
      "network",
      "link"
    ]
  }
