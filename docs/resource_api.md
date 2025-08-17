# Resource API Documentation

This document provides information about the Resource API, which allows you to upload, retrieve, and
delete resources and mentor profile pictures.

## Setup

### Google Drive Configuration

1. Create a project in the [Google Cloud Console](https://console.cloud.google.com/).
2. Enable the Google Drive API for your project.
3. Create OAuth 2.0 credentials (Desktop application type).
4. Download the credential JSON file and save it as `credentials.json` in the `src/main/resources`
   directory.
5. Create a folder in Google Drive where the files will be stored.
6. Get the folder ID from the URL of the folder (e.g.,
   `https://drive.google.com/drive/folders/FOLDER_ID`).
7. Set the `GOOGLE_DRIVE_FOLDER_ID` environment variable to the folder ID.

### Google Drive folder structure

1. The main folder is called 'Platform'
2. Inside the 'Platform' folder, there are subfolders:
    - `DEV`: For storing development resources from fly.io.
    - `LOCAL`: For storing local tests resources for developers.
    - `TEST`: For storing automation tests resources.