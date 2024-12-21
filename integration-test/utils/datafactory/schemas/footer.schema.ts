import { linkSchema } from "./link.schema"

export const footerSchema = {
    $ref: '#/definitions/footerSchema',
    definitions: {
        footerSchema: {
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
                "link": {...linkSchema.definitions.linkSchema}
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
    }
}
