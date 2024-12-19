import {defineConfig} from '@playwright/test';
import dotenv from 'dotenv';
import path from 'path';

dotenv.config({path: path.resolve(__dirname, '.env')});

dotenv.config();

export default defineConfig({
  projects: [
    {
      name: 'api_dev',
      use: { 
        baseURL: process.env.API_HOST
       },
    },
    {
      name: 'api_local',
      use: {
        baseURL: 'http://localhost:8080'
      },
    },
  ],
});
