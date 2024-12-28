import { linkSchema } from "./link.schema"
export const teamSchema = {
    $ref: '#/definitions/teamSchema',
    definitions: {
        teamSchema: {
            "type": "object",
            "properties": {
                "title": {
                    "type": "string",
                    "minLength": 1
                },
          "images": {
                    "type": "array",
                    "items": {
                        "type": "object",
                        "properties": {
                            "type": {
                                "type": "string",
                                "enum": ["MOBILE","DESKTOP","TABLET"]
                            },

                    "alt": {
                    "type": "string",
                    "minLength": 1
                },
                
                "links": {
                    "type": "array",
                    "items": {
                        "type": "object",
                        "properties": {
                            "type": {
                                "type": "string",
                                "enum": ["youtube", "github", "linkedin", "instagram", "facebook", "twitter", "medium", "slack", "meetup", "email", "unknown","MOBILE","DESKTOP","TABLET"]
                            },
                            "link": {
                                "pattern": "^(https?)://[^\s/$.?#].[^\s]*$",
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
                    "minItems": 11,
                    "maxItems": 11,
                },

                "link": {...linkSchema.definitions.linkSchema}
            },
              "required": [
                "title",
                "links",
                "directors",
                "fullName",
                "position",
                "email",
                "slackDisplayName",
                "country",
                "images",
                "leads",
                "link",
                "path",
                "alt"
                
            ],
            "additionalProperties": false
        }
    }
}
        }
    }
}
