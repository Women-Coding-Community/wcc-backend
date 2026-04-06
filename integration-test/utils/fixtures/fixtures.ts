import { test as base, APIRequestContext } from '@playwright/test';
import axios from 'axios';
import SwaggerParser from '@apidevtools/swagger-parser';
import { PATHS } from '@utils/datafactory/paths.data';
import { loginResponseSchema } from '@utils/datafactory/schemas/auth.schema';

const ADMIN_EMAIL = process.env.ADMIN_EMAIL;
const ADMIN_PASSWORD = process.env.ADMIN_PASSWORD;

type OpenAPI3Spec = {
  openapi: string;
  components: {
    schemas: Record<string, any>;
  };
};

type Fixtures = {
  openApiSchemas: Record<string, any>;
  token: string;
  authRequest: APIRequestContext;
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
  token: async ({ request }, use) => {
    const response = await request.post(PATHS.AUTH_LOGIN, {
      data: {
        email: ADMIN_EMAIL,
        password: ADMIN_PASSWORD,
      },
    });
    const responseBody = await response.json();
    await use(loginResponseSchema.parse(responseBody).token);
  },

  authRequest: async ({ playwright, token }, use) => {
    const context = await playwright.request.newContext({
      baseURL: process.env.API_HOST ?? 'http://localhost:8080',
      extraHTTPHeaders: {
        'X-API-KEY': process.env.API_KEY ?? '',
        Authorization: `Bearer ${token}`,
      },
    });
    await use(context);
    await context.dispose();
  },
});

export { expect } from '@playwright/test';
