export const paginationSchema = {
  $ref: '#/definitions/paginationSchema',
  definitions: {
    paginationSchema: {
      type: 'object',
      properties: {
        totalItems: {
          type: 'integer',
        },
        totalPages: {
          type: 'integer',
        },
        currentPage: {
          type: 'integer',
        },
        pageSize: {
          type: 'integer',
        },
      },
      additionalProperties: false,
    },
  },
};
