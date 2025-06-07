# Resource API Documentation

This document provides information about the Resource API, which allows you to upload, retrieve, and delete resources and mentor profile pictures.

## Setup

### Google Drive Configuration

1. Create a project in the [Google Cloud Console](https://console.cloud.google.com/).
2. Enable the Google Drive API for your project.
3. Create OAuth 2.0 credentials (Desktop application type).
4. Download the credentials JSON file and save it as `credentials.json` in the `src/main/resources` directory.
5. Create a folder in Google Drive where the files will be stored.
6. Get the folder ID from the URL of the folder (e.g., `https://drive.google.com/drive/folders/FOLDER_ID`).
7. Set the `GOOGLE_DRIVE_FOLDER_ID` environment variable to the folder ID.

## API Endpoints

All endpoints require the `X-API-KEY` header for authentication.

### Resources

#### Upload a Resource

```
POST /api/platform/v1/resources
Content-Type: multipart/form-data
```

**Parameters:**
- `file` (required): The file to upload
- `name` (required): The name of the resource
- `description` (optional): The description of the resource
- `resourceType` (required): The type of resource (PROFILE_PICTURE, IMAGE, PDF, PRESENTATION, OTHER)

**Response:**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "Example Resource",
  "description": "Example Description",
  "fileName": "example.jpg",
  "contentType": "image/jpeg",
  "size": 1024,
  "driveFileId": "drive-file-id",
  "driveFileLink": "https://drive.google.com/file/d/drive-file-id/view",
  "resourceType": "IMAGE",
  "createdAt": "2023-06-01T12:00:00Z",
  "updatedAt": "2023-06-01T12:00:00Z"
}
```

#### Get a Resource by ID

```
GET /api/platform/v1/resources/{id}
```

**Parameters:**
- `id` (path parameter): The ID of the resource

**Response:**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "Example Resource",
  "description": "Example Description",
  "fileName": "example.jpg",
  "contentType": "image/jpeg",
  "size": 1024,
  "driveFileId": "drive-file-id",
  "driveFileLink": "https://drive.google.com/file/d/drive-file-id/view",
  "resourceType": "IMAGE",
  "createdAt": "2023-06-01T12:00:00Z",
  "updatedAt": "2023-06-01T12:00:00Z"
}
```

#### Get Resources by Type

```
GET /api/platform/v1/resources?resourceType={resourceType}
```

**Parameters:**
- `resourceType` (query parameter): The type of resources to get (PROFILE_PICTURE, IMAGE, PDF, PRESENTATION, OTHER)

**Response:**
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "name": "Example Resource",
    "description": "Example Description",
    "fileName": "example.jpg",
    "contentType": "image/jpeg",
    "size": 1024,
    "driveFileId": "drive-file-id",
    "driveFileLink": "https://drive.google.com/file/d/drive-file-id/view",
    "resourceType": "IMAGE",
    "createdAt": "2023-06-01T12:00:00Z",
    "updatedAt": "2023-06-01T12:00:00Z"
  }
]
```

#### Search for Resources by Name

```
GET /api/platform/v1/resources/search?name={name}
```

**Parameters:**
- `name` (query parameter): The name to search for

**Response:**
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "name": "Example Resource",
    "description": "Example Description",
    "fileName": "example.jpg",
    "contentType": "image/jpeg",
    "size": 1024,
    "driveFileId": "drive-file-id",
    "driveFileLink": "https://drive.google.com/file/d/drive-file-id/view",
    "resourceType": "IMAGE",
    "createdAt": "2023-06-01T12:00:00Z",
    "updatedAt": "2023-06-01T12:00:00Z"
  }
]
```

#### Delete a Resource

```
DELETE /api/platform/v1/resources/{id}
```

**Parameters:**
- `id` (path parameter): The ID of the resource to delete

**Response:**
- Status: 204 No Content

### Mentor Profile Pictures

#### Upload a Mentor's Profile Picture

```
POST /api/platform/v1/resources/mentor-profile-pictures
Content-Type: multipart/form-data
```

**Parameters:**
- `file` (required): The profile picture file
- `mentorEmail` (required): The email of the mentor

**Response:**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "mentorEmail": "mentor@example.com",
  "resourceId": "123e4567-e89b-12d3-a456-426614174001",
  "resource": {
    "id": "123e4567-e89b-12d3-a456-426614174001",
    "name": "Profile picture for mentor@example.com",
    "description": "Profile picture for mentor with email mentor@example.com",
    "fileName": "profile.jpg",
    "contentType": "image/jpeg",
    "size": 1024,
    "driveFileId": "drive-file-id",
    "driveFileLink": "https://drive.google.com/file/d/drive-file-id/view",
    "resourceType": "PROFILE_PICTURE",
    "createdAt": "2023-06-01T12:00:00Z",
    "updatedAt": "2023-06-01T12:00:00Z"
  },
  "createdAt": "2023-06-01T12:00:00Z",
  "updatedAt": "2023-06-01T12:00:00Z"
}
```

#### Get a Mentor's Profile Picture

```
GET /api/platform/v1/resources/mentor-profile-pictures/{mentorEmail}
```

**Parameters:**
- `mentorEmail` (path parameter): The email of the mentor

**Response:**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "mentorEmail": "mentor@example.com",
  "resourceId": "123e4567-e89b-12d3-a456-426614174001",
  "resource": {
    "id": "123e4567-e89b-12d3-a456-426614174001",
    "name": "Profile picture for mentor@example.com",
    "description": "Profile picture for mentor with email mentor@example.com",
    "fileName": "profile.jpg",
    "contentType": "image/jpeg",
    "size": 1024,
    "driveFileId": "drive-file-id",
    "driveFileLink": "https://drive.google.com/file/d/drive-file-id/view",
    "resourceType": "PROFILE_PICTURE",
    "createdAt": "2023-06-01T12:00:00Z",
    "updatedAt": "2023-06-01T12:00:00Z"
  },
  "createdAt": "2023-06-01T12:00:00Z",
  "updatedAt": "2023-06-01T12:00:00Z"
}
```

#### Delete a Mentor's Profile Picture

```
DELETE /api/platform/v1/resources/mentor-profile-pictures/{mentorEmail}
```

**Parameters:**
- `mentorEmail` (path parameter): The email of the mentor

**Response:**
- Status: 204 No Content

## Error Handling

The API returns appropriate HTTP status codes and error messages for different error scenarios:

- 400 Bad Request: Invalid request parameters
- 404 Not Found: Resource or profile picture not found
- 500 Internal Server Error: Server-side error

Example error response:

```json
{
  "timestamp": "2023-06-01T12:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found with ID: 123e4567-e89b-12d3-a456-426614174000",
  "path": "/api/platform/v1/resources/123e4567-e89b-12d3-a456-426614174000"
}
```