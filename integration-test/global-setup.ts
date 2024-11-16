import dotenv from 'dotenv';

export default async function globalSetup() {
  const environment = process.env.NODE_ENV || 'local';

  const envFile = environment === 'dev' ? '.env.dev' : '.env.local';
  dotenv.config({ path: envFile });

  console.log(`Loaded environment variables from ${envFile}`);
}