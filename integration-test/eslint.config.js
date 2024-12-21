const { configs: recommendedConfigs } = require('@typescript-eslint/eslint-plugin');
const { configs: stylisticConfigs } = require('@typescript-eslint/eslint-plugin');

module.exports = [
  {
    files: ['**/*.ts', '**/*.tsx'],
    languageOptions: {
      parser: require('@typescript-eslint/parser'), 
      parserOptions: {
        project: './tsconfig.json',
      },
    },
    plugins: {
      '@typescript-eslint': recommendedConfigs,
    },
    rules: {
      ...recommendedConfigs.rules,
      ...stylisticConfigs.rules,
    },
  },
];
