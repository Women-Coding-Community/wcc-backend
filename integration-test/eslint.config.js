const { configs: recommendedConfigs } = require('@typescript-eslint/eslint-plugin');
const { configs: stylisticConfigs } = require('@typescript-eslint/eslint-plugin');

module.exports = [
  {
    files: ['**/*.ts', '**/*.tsx'],
    ignores: [
      'node_modules/',
      '*.config.js',
    ],
    languageOptions: {
      parser: require('@typescript-eslint/parser'),
      parserOptions: {
        tsconfigRootDir: __dirname,
        project: './tsconfig.json',
      },
    },
    plugins: {
      '@typescript-eslint': require('@typescript-eslint/eslint-plugin'),
    },
    rules: {
      ...(recommendedConfigs['recommended'].rules || {}),
      ...(stylisticConfigs['stylistic'].rules || {}),
    },
  },
];
