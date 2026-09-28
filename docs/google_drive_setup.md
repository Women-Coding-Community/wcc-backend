# Google Drive Integration Setup Instructions

<!-- TOC -->

* [Google Drive Integration Setup Instructions](#google-drive-integration-setup-instructions)
    * [Overview](#overview)
    * [Setup Google Drive API & Service Account](#setup-google-drive-api--service-account)
        * [1. Enable Google Drive API](#1-enable-google-drive-api)
        * [2. Create a Service Account & Download Key](#2-create-a-service-account--download-key)
        * [3. Organization Policy Troubleshooting](#3-organization-policy-troubleshooting)
    * [Google Drive Folder Setup & Permissions](#google-drive-folder-setup--permissions)
        * [Proposed Folder Structure](#proposed-folder-structure)
        * [Share Folder with Service Account](#share-folder-with-service-account)
    * [Automated Setup & Verification Script](#automated-setup--verification-script)
        * [Local Development](#local-development)
        * [Fly.io Dev / Prod Deployment](#flyio-dev--prod-deployment)
    * [Environment Variables Reference](#environment-variables-reference)

<!-- TOC -->

## Overview

The WCC Platform Backend uses a **headless Google Cloud Service Account** to store and manage mentor profile pictures and platform resources in Google Drive. 

Credentials are provided via environment variables (`GOOGLE_DRIVE_CREDENTIALS_JSON` / `STORAGE_GOOGLE_DRIVE_CREDENTIALS_JSON`), requiring no interactive browser logins or secrets bundled into container images.

---

## Setup Google Drive API & Service Account

### 1. Enable Google Drive API

1. Go to the [Google Cloud Console](https://console.cloud.google.com/).
2. Select your Google Cloud project (e.g. `wcc-platform`).
3. Navigate to **APIs & Services > Library**.
4. Search for **Google Drive API** and click **Enable**.

### 2. Create a Service Account & Download Key

1. Navigate to **IAM & Admin > Service Accounts**.
2. Click **Create Service Account**.
3. Name it (e.g. `wcc-backend-drive`) and click **Done**. (No project-level IAM roles are needed because folder permissions are granted directly within Google Drive).
4. Click on the created service account > **Keys** tab > **Add Key > Create new key**.
5. Select **JSON** and click **Create** to download the key file (e.g. `service-account.json`).
6. Note the **Service Account Email** (e.g. `wcc-backend-drive@<project-id>.iam.gserviceaccount.com`).

### 3. Organization Policy Troubleshooting

If you receive the error `Service account key creation is disabled (iam.disableServiceAccountKeyCreation)`, disable the organization policy constraint via Google Cloud Shell:

```bash
gcloud resource-manager org-policies disable-enforce \
    iam.disableServiceAccountKeyCreation \
    --organization=ORGANIZATION_ID
```
*(Or at project level: `--project=PROJECT_ID`).*

---

## Google Drive Folder Setup & Permissions

### Proposed Folder Structure

Create or identify the following folder structure in Google Drive:

```text
Platform
├── DEV                     <-- Dev environment resources
├── LOCAL                   <-- Local development & testing resources
└── PROD                    <-- Production resources
    ├── EVENTS              <-- Event materials and flyers
    ├── IMAGES              <-- General platform images
    ├── MENTOR_PICTURES     <-- Mentor profile pictures
    ├── MENTOR_RESOURCES    <-- Mentor-specific documents
    └── RESOURCES           <-- General mentorship resources
```

### Share Folder with Service Account

Service accounts operate in their own storage space. To allow the backend to read/write into your shared folders:

1. Right-click the root folder (e.g. `LOCAL`, `DEV`, or `Platform`) in Google Drive.
2. Select **Share**.
3. Paste the **Service Account Email** (`...@...iam.gserviceaccount.com`).
4. Assign the role **Editor** and uncheck "Notify people".
5. Click **Share**. All subfolders will inherit editor access automatically.

---

## Automated Setup & Verification Script

The repository includes a self-contained helper script: [`scripts/setup-google-drive.sh`](../scripts/setup-google-drive.sh).

### Local Development

Run the script pointing to your downloaded key file:

```bash
./scripts/setup-google-drive.sh local --key ~/Downloads/service-account.json
```

The script will:
1. Authenticate with Google Drive API via OAuth2 JWT bearer flow.
2. Automatically discover all folder IDs.
3. Perform a live upload and permission verification test.
4. Generate a `.env.local` file with all required configuration.

Then start the backend locally:
```bash
export $(cat .env.local | xargs)
./gradlew bootRun
```

### Fly.io Dev / Prod Deployment

To verify and apply secrets to Fly.io automatically:

```bash
# For dev environment:
./scripts/setup-google-drive.sh dev --key ~/Downloads/service-account.json --set-fly-secrets

# For prod environment:
./scripts/setup-google-drive.sh prod --key ~/Downloads/service-account.json --set-fly-secrets
```

---

## Environment Variables Reference

| Variable | Description | Example |
| :--- | :--- | :--- |
| `STORAGE_TYPE` | Storage backend implementation (`google` or `local`) | `google` |
| `GOOGLE_DRIVE_CREDENTIALS_JSON` | Full JSON content of the service account key | `{"type":"service_account",...}` |
| `STORAGE_GOOGLE_DRIVE_CREDENTIALS_JSON` | Alias for Spring Boot property binding | `{"type":"service_account",...}` |
| `STORAGE_FOLDERS_MAIN_FOLDER` | Google Drive parent folder ID | `1a2b3c4d...` |
| `STORAGE_FOLDERS_MENTORS_PROFILE_FOLDER` | Folder ID for mentor profile pictures | `1e2f3g4h...` |
| `STORAGE_FOLDERS_RESOURCES_FOLDER` | Folder ID for mentorship resources | `1i2j3k4l...` |
| `STORAGE_FOLDERS_EVENTS_FOLDER` | Folder ID for event resources | `1m2n3o4p...` |
| `STORAGE_FOLDERS_MENTORS_FOLDER` | Folder ID for mentor resources | `1q2r3s4t...` |
| `STORAGE_FOLDERS_IMAGES_FOLDER` | Folder ID for general images | `1u2v3w4x...` |