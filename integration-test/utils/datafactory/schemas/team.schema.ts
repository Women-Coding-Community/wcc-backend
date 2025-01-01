import { linkSchema } from "./link.schema"
import { imagesSchema } from "./images.schema"
export const teamSchema = {
    $ref: '#/definitions/teamSchema',
    definitions: {
        teamSchema: {
            "type": "object",
            "properties": {
                "id": {
                    "type": "string",
                    "minLength": 1
                },
                "properties": {
                "page": {
                    "type": "string",
                    "minLength": 1,
                    "title": {
                    "type": "string",
                    "minLength": 1
                },
            },
            "properties": {
                "contact": {
                    "type": "string",
                    "minLength": 1,
                    },
            },
               "membersByType": {
                "type": "string",
                "minLength": 1,
                "directors": {
                    "type": "string",
                    "minLength": 1
                },
               
                "leads": {
                    "type": "string",
                    "minLength": 1
                },
                "evenagelists": {
                    "type": "string",
                    "minLength": 1
                },
                 "fullName": {
                  "type": "string",
                  "minLength": 1
                 },
                 "postion": {
                    "type": "string",
                    "minLength": 1
                 },
                "email": {
                "type": "string",
                 "minLength": 1
                },
                "slackDisplayName": {
                "type": "string",
                "minLength": 1
                },
                "country": {
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

                "link": {...linkSchema.definitions.linkSchema},
                "images": {...imagesSchema.definitions.imagesSchema}
            },
              "required": [
                "id",
                "title",
                "page",
                "contact",
                "links",
                "membersByType",
                "directors",
                "fullName",
                "position",
                "email",
                "slackDisplayName",
                "country",
                "images",
                "leads",
                "evangelists",
                "link",
                              
            ],
            "additionalProperties": false
        }
    
    }
}
}
}