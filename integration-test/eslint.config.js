const { configs: recommendedConfigs } = require('@typescript-eslint/eslint-plugin');
const { configs: stylisticConfigs } = require('@typescript-eslint/eslint-plugin');

module.exports = [
  {
    // Specify the files to lint
    files: ['**/*.ts', '**/*.tsx'],

    // Set the parser under languageOptions
    languageOptions: {
      parser: require('@typescript-eslint/parser'), // Use the parser as an object
      parserOptions: {
        project: './tsconfig.json', // Path to your tsconfig file
      },
    },

    // Include plugins and rules directly
    plugins: {
      '@typescript-eslint': recommendedConfigs,
    },

    // Combine rules from different configurations
    rules: {
      ...recommendedConfigs.rules,
      ...stylisticConfigs.rules,
    },
  },
];
