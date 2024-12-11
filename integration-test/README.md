# Backend Integration Tests

<!-- TOC -->

- [Backend Integration Tests](#backend-integration-tests)
  - [Steps to Set Up](#steps-to-set-up)
    - [1. Install Dependencies](#1-install-dependencies)
  - [Install dependencies](#install-dependencies)
    - [2. Update .env File](#2-update-env-file)
  - [Run Tests](#run-tests)
    - [Command line](#command-line)
  * [Postman Collection import in repo](#steps-to-add)
    - [1. Export Collections](#1-export-collections)
    - [2. Export Env](#2-export-envs)
    - [3. Import Col and Env](#3-import-col-env-files)

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

#### Postman Collection import in repo

Link - https://app.getpostman.com/join-team?invite_code=188096ad281000ecd33932a724d74e5f&target_code=d1271d0429db3e9126cc48e18bfa08fb

### 1. Export Collection Postman

In the left-hand panel, under the Collections tab, hover over the collection you want to export.
Click on the three dots (ellipsis) next to the collection name and select Export.
Choose the export format (usually Collection v2.1).
Click Export and select the location where you want to save the file.

### 2. Export Enviroments Postman

In the left-hand panel, go to the Environments tab.
Hover over the environment you want to export.
Click on the three dots (ellipsis) next to the environment name and select Export.
Save the file to your desired location.

For local environment: Dev

For dev environment: Localhost

### 3. Import Col and Env

After exporting both the collections and environments, you need to import them into your workspace to continue experimenting with them.

To import, click on the Import button in the top-left corner of your Postman interface.
Select the files you just exported and follow the prompts to complete the import process.
