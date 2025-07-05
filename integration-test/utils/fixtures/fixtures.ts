import { test as base } from '@playwright/test';
import axios from 'axios';
import SwaggerParser from '@apidevtools/swagger-parser';

type OpenAPI3Spec = {
  openapi: string;
  components: {
    schemas: Record<string, any>;
  };
};

type Fixtures = {
  openApiSchemas: Record<string, any>;
};

export const test = base.extend<Fixtures>({
  openApiSchemas: async ({}, use) => {
    const url = `${process.env.API_HOST}/api-docs`; 
    const { data } = await axios.get(url);
    const parsed = await SwaggerParser.dereference(data) as OpenAPI3Spec;
    const schemas = parsed.components.schemas;
    for (const schema of Object.values(schemas)) {
      if (schema && typeof schema === 'object' && schema.properties) {
        schema.additionalProperties ??= false;
      }
    }
    await use(schemas);
  },
});

export { expect } from '@playwright/test';
