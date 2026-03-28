import { expect, test } from '@utils/fixtures/fixtures';
import { PATHS } from '@utils/datafactory/paths.data';
import { loginResponseSchema } from '@utils/datafactory/schemas/auth.schema';
import { usersResponseSchema } from '@utils/datafactory/schemas/user.account.schema';

const ADMIN_EMAIL = process.env.ADMIN_EMAIL;
const ADMIN_PASSWORD = process.env.ADMIN_PASSWORD;

test.describe('Authentication', () => {
  test('AUTH-01: Login with valid credentials returns token', async ({ request }) => {
    const response = await request.post(PATHS.AUTH_LOGIN, {
      data: {
        email: ADMIN_EMAIL,
        password: ADMIN_PASSWORD,
      },
    });
    expect(response.status()).toBe(200);

    const body = await response.json();
    expect(loginResponseSchema.parse(body)).toBeTruthy();
  });

  test('AUTH-07: Get users with admin token returns user list', async ({ authRequest }) => {
    const response = await authRequest.get(PATHS.AUTH_USERS);

    expect(response.status()).toBe(200);

    const body = await response.json();
    const users = usersResponseSchema.parse(body);
    expect(users.length).toBeGreaterThan(0);
  });
});
