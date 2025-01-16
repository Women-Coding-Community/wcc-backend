export const customstyleSchema = {
  $ref: '#/definitions/customstyleSchema',
  definitions: {
    customstyleSchema: {
      backgroundcolor: {
        type: 'object',
        properties: {
          items: {
            type: 'array',
            color: {
              type: 'string',
              enum: ['primary', 'secondary', 'tertiary'],
            },
            shade: {
              type: 'object',
              properties: {
                name: {
                  type: 'string',
                  enum: [' main', 'light', 'dark'],
                  value: {
                    type: 'integer',
                  },  
                },
              },
            },
          }, 
        } 
      },
   },
 },
}


