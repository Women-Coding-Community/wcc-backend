# Backend Integration Tests

<!-- TOC -->

- [Backend Integration Tests](#backend-integration-tests)
  - [Steps to Set Up](#steps-to-set-up)
    - [1. Install Dependencies](#1-install-dependencies)
  - [Install dependencies](#install-dependencies)
    - [2. Update .env File](#2-update-env-file)
  - [Run Tests](#run-tests)
    - [Command line](#command-line)

<!-- TOC -->

## Steps to Set Up

### 1. Install Dependencies

Make sure you have Node.js installed. If not, [download and install Node.js](https://nodejs.org/).

## Install dependencies

```shell
npm install
```

## Run Tests

Make sure you have created .env file `.env` with `API_HOST` configured with respective server. Example is provided in `.env.example` file.

### Command line

```shell
npm run test
```

### Command line by environment

For local environment:

```shell
npm run test-local
```

For dev environment:

```shell
npm run test-dev
```

## Running ESLint and Prettier

```shell
npm run lint && npm run prettier
``` 

**What It Does:**

- `npm run lint`: Runs ESLint to analyze your code for syntax and style issues.

- `npm run prettier`: Formats your code according to Prettier's configuration.

**Usage:**

Open a terminal in the `ìntegration-test` directory and run command:

```shell
npm run lint && npm run prettier
```

**Check the output:**
- ESLint will display any linting errors or warnings.
- Prettier will indicate whether it formatted any files.
- If there are errors, follow the provided messages to fix them manually.

### Automatically Fixing Issues

```shell
npm run lint:fix && npm run prettier:fix
```

**What It Does:**
- `npm run lint:fix`: Runs ESLint with the --fix flag to automatically correct fixable issues.
- `npm run prettier:fix`: Formats all supported files automatically using Prettier.

**Usage:**

Open a terminal in the `ìntegration-test` directory and run command:

```shell
npm run lint:fix && npm run prettier:fix
```

**The command will:**
- Fix as many ESLint issues as possible.
- Reformat code according to Prettier's rules.
- Review the changes to ensure correctness before committing.
