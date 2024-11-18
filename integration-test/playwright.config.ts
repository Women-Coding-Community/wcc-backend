import {defineConfig} from '@playwright/test';
import dotenv from 'dotenv';

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