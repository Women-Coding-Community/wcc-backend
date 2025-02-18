import { linkSchema } from './link.schema';
import { imagesSchema } from './images.schema';
import { customStyleSchema } from './custom.style.schema';

export const sectionSchema = {
  $ref: '#/definitions/sectionSchema',
  definitions: {
    sectionSchema: {
      type: 'object',
      properties: {
        title: {
          type: 'string',
          minLength: 1,
        },
        subtitle: {
            type: 'string',
            minLength: 1,
          },
        description: {
          type: 'string',
          minLength: 1,
        },
         link: { ...linkSchema.definitions.linkSchema },
        images: { ...imagesSchema.definitions.imagesSchema },
        customStyle: { ...customStyleSchema.definitions.customStyleSchema },
      },
        
      },
      additionalProperties: false,
      
    },
}
