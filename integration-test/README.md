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
