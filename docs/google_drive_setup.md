# Google Drive API Setup Instructions

<!-- TOC -->

* [Google Drive API Setup Instructions](#google-drive-api-setup-instructions)
    * [Setup Google Drive API](#setup-google-drive-api)
        * [Steps](#steps)
        * [Troubleshooting](#troubleshooting)
    * [Google Drive Project's folders Setup](#google-drive-projects-folders-setup)

<!-- TOC -->

## Setup Google Drive API

Enable the Google Drive API for your project in the Google Cloud Console.

### Steps

1. **Create a New OAuth Client in Google Cloud Console**

   a. Go to the [Google Cloud Console](https://console.cloud.google.com/).

   b. Select your project or create a new one.

   c. Navigate to "APIs & Services" > "Credentials".

   d. Click "Create Credentials" and select "OAuth client ID".

   e. Select "Desktop app" as the application type.

   f. Enter a name for your OAuth client (e.g., "WCC Backend").

   g. Click "Create".

   h. Note down the Client ID and Client Secret that are displayed.

2. **Update the `credentials.json.sample`  File**

   a. Open the file and rename to `src/main/resources/credentials.json`.

   b. Replace the placeholder values with your actual credentials:

   ```json
   {
     "installed": {
       "client_id": "YOUR_NEW_CLIENT_ID.apps.googleusercontent.com",
       "project_id": "YOUR_PROJECT_ID",
       "auth_uri": "https://accounts.google.com/o/oauth2/auth",
       "token_uri": "https://oauth2.googleapis.com/token",
       "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
       "client_secret": "YOUR_NEW_CLIENT_SECRET",
       "redirect_uris": [
         "http://localhost"
       ]
     }
   }
   ```

   c. Replace:
    - `YOUR_NEW_CLIENT_ID.apps.googleusercontent.com` with your actual Client ID
    - `YOUR_PROJECT_ID` with your Google Cloud project ID
    - `YOUR_NEW_CLIENT_SECRET` with your actual Client Secret

<b>Note:</b> Check the sample file `credentials.json.sample` for more details.

3. **Enable Google Drive API**

   a. In the Google Cloud Console, navigate to "APIs & Services" > "Library".

   b. Search for "Google Drive API" and select it.

   c. Click "Enable" if it's not already enabled.

4. **First Run Authentication**

   When you run the application for the first time after updating the credentials, it will:

   a. Open a browser window asking you to authorize the application.

   b. Sign in with the Google account that should have access to the Drive files.

   c. Grant the requested permissions.

   d. The application will then store the authentication tokens in the `tokens` directory.

### Troubleshooting

- If you encounter any issues during authentication, check the application logs for detailed error
  messages.
- Make sure the Google Drive API is enabled for your project in the Google Cloud Console.
- Ensure that the Google account you're using has the necessary permissions for the Google Drive
  folder specified in the application configuration.

## Google Drive Project's folders Setup

### Proposed Folder Structure

1. The main folder is called 'Platform'
2. Inside the 'Platform' folder, there are subfolders:
    - `DEV`: For storing development resources from fly.io.
    - `LOCAL`: For storing local tests resources for developers.
    - `TEST`: For storing automation tests resources.
3. Internal Sub-folder per environment
    - EVENTS
    - IMAGES
    - MENTOR_PICTURES
    - MENTOR_RESOURCES
    - RESOURCES

### Get Folder ID

1. Go inside in each folder and copy the folder ID from the URL.
2. Example: https://drive.google.com/drive/u/0/folders/12345678901234567890 the folder ID is
   `12345678901234567890`
3. Save the respective folder ID in the application.yml file for the respective environment.
4. Example: for the local profile, create application-local.yml and add the following:
   ```yaml
   storage:
     type: google
     folders:
       main-folder: folder_id_local_env
       resources-folder: folder_id_local_env_and_sub_folder_resources
       events-folder: folder_id_local_env_and_sub_folder_events
       images-folder: folder_id_local_env_and_sub_folder_images
       mentor-pictures-folder: folder_id_local_env_and_sub_folder_mentor_pictures
       mentor-resources-folder: folder_id_local_env_and_sub_folder_mentor_resources
   ```