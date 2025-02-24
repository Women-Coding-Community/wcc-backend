import { linkSchema } from "./link.schema";

export const aboutHerSchema = {
  $ref: '#/definitions/aboutHerSchema',
  definitions: {
    aboutHerSchema: {
        "type": "object",
        "properties": {
            "listOfName": {
              "type": "array",
              "items": [
                {
                  "type": "string"
                }
              ]
            },
            "description": {
              "type": "string"
            },
            "link": {...linkSchema.definitions.linkSchema}
          },
          additionalProperties: false,
          "required": [
            "listOfName",
            "description"
          ]
        }
    }
};


