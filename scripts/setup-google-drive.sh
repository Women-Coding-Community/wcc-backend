#!/usr/bin/env bash
set -euo pipefail

# ==============================================================================
# Google Drive Integration Setup & Verification Script for WCC Backend
#
# Usage:
#   ./scripts/setup-google-drive.sh [local|dev|prod] [options]
#
# Options:
#   --env <env>             Target environment: local (default), dev, or prod
#   --key <file>            Path to service account JSON key file
#   --fly-app <app>         Fly.io app name (default: wcc-backend-dev or wcc-backend-prod)
#   --set-fly-secrets       Directly set Fly.io secrets using the 'fly' CLI
#   --create-missing        Auto-create missing subfolders in Google Drive
#   -h, --help              Show help message
# ==============================================================================

ENV_TARGET="local"
KEY_FILE=""
FLY_APP=""
SET_FLY_SECRETS=false
CREATE_MISSING=false

# Helper for base64url encoding
b64url() {
  openssl base64 -e -A | tr "+/" "-_" | tr -d "="
}

print_help() {
  cat <<EOF
Google Drive Setup & Verification Script

Usage:
  ./scripts/setup-google-drive.sh [local|dev|prod] [OPTIONS]

Environments:
  local   (Default) Configure and test for local development
  dev     Configure and test for Fly.io dev environment
  prod    Configure and test for Fly.io prod environment

Options:
  --key <file>          Path to Service Account JSON key file
  --fly-app <name>      Override Fly.io app name (default: wcc-backend-dev or wcc-backend-prod)
  --set-fly-secrets     Execute 'fly secrets set' automatically (requires fly CLI)
  --create-missing      Create any missing subfolders in Google Drive automatically
  -h, --help            Show this help message

Examples:
  ./scripts/setup-google-drive.sh local --key ~/Downloads/service-account.json
  ./scripts/setup-google-drive.sh dev --key ~/Downloads/service-account.json --set-fly-secrets
  ./scripts/setup-google-drive.sh prod --key ~/Downloads/service-account.json
EOF
}

# Parse arguments
while [[ $# -gt 0 ]]; do
  case "$1" in
    local|dev|prod)
      ENV_TARGET="$1"
      shift
      ;;
    --env)
      ENV_TARGET="$2"
      shift 2
      ;;
    --key)
      KEY_FILE="$2"
      shift 2
      ;;
    --fly-app)
      FLY_APP="$2"
      shift 2
      ;;
    --set-fly-secrets)
      SET_FLY_SECRETS=true
      shift
      ;;
    --create-missing)
      CREATE_MISSING=true
      shift
      ;;
    -h|--help)
      print_help
      exit 0
      ;;
    *)
      if [ -z "$KEY_FILE" ] && [ -f "$1" ]; then
        KEY_FILE="$1"
      else
        echo "❌ Unknown argument: $1"
        print_help
        exit 1
      fi
      shift
      ;;
  esac
done

# Set default Fly app if applicable
if [ -z "$FLY_APP" ]; then
  if [ "$ENV_TARGET" = "dev" ]; then
    FLY_APP="wcc-backend-dev"
  elif [ "$ENV_TARGET" = "prod" ]; then
    FLY_APP="wcc-backend-prod"
  fi
fi

# Locate service account key if not provided
if [ -z "$KEY_FILE" ]; then
  if [ -n "${GOOGLE_DRIVE_KEY_PATH:-}" ] && [ -f "$GOOGLE_DRIVE_KEY_PATH" ]; then
    KEY_FILE="$GOOGLE_DRIVE_KEY_PATH"
  else
    # Check default download patterns
    FOUND_KEY=$(find "$HOME/Downloads" -maxdepth 1 -name "*platform-backend*.json" -o -name "*service-account*.json" 2>/dev/null | head -n 1 || true)
    if [ -n "$FOUND_KEY" ] && [ -f "$FOUND_KEY" ]; then
      KEY_FILE="$FOUND_KEY"
    fi
  fi
fi

if [ -z "$KEY_FILE" ] || [ ! -f "$KEY_FILE" ]; then
  echo "❌ Service Account JSON key file not found."
  echo "👉 Provide key path via: ./scripts/setup-google-drive.sh $ENV_TARGET --key /path/to/key.json"
  exit 1
fi

echo "============================================================"
echo "🚀 WCC Google Drive Setup & Verification ($ENV_TARGET environment)"
echo "============================================================"
echo "🔑 Using Service Account Key: $KEY_FILE"

# Extract fields from key
CLIENT_EMAIL=$(jq -r '.client_email // empty' "$KEY_FILE")
TOKEN_URI=$(jq -r '.token_uri // "https://oauth2.googleapis.com/token"' "$KEY_FILE")
PROJECT_ID=$(jq -r '.project_id // empty' "$KEY_FILE")

if [ -z "$CLIENT_EMAIL" ]; then
  echo "❌ Invalid Service Account JSON key (missing client_email)."
  exit 1
fi

echo "📧 Service Account Email:     $CLIENT_EMAIL"
echo "🌐 Google Cloud Project:      $PROJECT_ID"
echo "------------------------------------------------------------"

# Generate JWT and exchange for OAuth2 access token
TMP_DIR=$(mktemp -d)
trap 'rm -rf "$TMP_DIR"' EXIT
jq -r .private_key "$KEY_FILE" > "$TMP_DIR/private.key"

HEADER_B64=$(echo -n '{"alg":"RS256","typ":"JWT"}' | b64url)
NOW=$(date +%s)
EXP=$((NOW + 3600))
CLAIM=$(printf '{"iss":"%s","scope":"https://www.googleapis.com/auth/drive","aud":"%s","iat":%d,"exp":%d}' "$CLIENT_EMAIL" "$TOKEN_URI" "$NOW" "$EXP")
CLAIM_B64=$(echo -n "$CLAIM" | b64url)

SIGNATURE_B64=$(echo -n "$HEADER_B64.$CLAIM_B64" | openssl dgst -sha256 -sign "$TMP_DIR/private.key" -binary | b64url)
JWT="$HEADER_B64.$CLAIM_B64.$SIGNATURE_B64"

echo "🔄 Requesting Google OAuth2 access token..."
TOKEN_RESPONSE=$(curl -s -X POST "$TOKEN_URI" \
  --data-urlencode "grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer" \
  --data-urlencode "assertion=$JWT")

ACCESS_TOKEN=$(echo "$TOKEN_RESPONSE" | jq -r '.access_token // empty')

if [ -z "$ACCESS_TOKEN" ]; then
  echo "❌ Failed to obtain access token from Google:"
  echo "$TOKEN_RESPONSE" | jq . || echo "$TOKEN_RESPONSE"
  exit 1
fi

echo "✅ Authenticated successfully with Google Drive API!"
echo "------------------------------------------------------------"

# Function to search Google Drive folders
drive_list_folders() {
  local query="mimeType='application/vnd.google-apps.folder' and trashed=false"
  if [ -n "${1:-}" ]; then
    query="$query and '$1' in parents"
  fi
  local encoded_query
  encoded_query=$(python3 -c "import urllib.parse; print(urllib.parse.quote('''$query'''))")

  curl -s -H "Authorization: Bearer $ACCESS_TOKEN" \
    "https://www.googleapis.com/drive/v3/files?q=${encoded_query}&fields=files(id,name,parents)&supportsAllDrives=true&includeItemsFromAllDrives=true&pageSize=100"
}

# Function to create a folder in Google Drive
drive_create_folder() {
  local folder_name="$1"
  local parent_id="${2:-}"
  local payload
  if [ -n "$parent_id" ]; then
    payload=$(jq -n --arg name "$folder_name" --arg parent "$parent_id" '{name: $name, mimeType: "application/vnd.google-apps.folder", parents: [$parent]}')
  else
    payload=$(jq -n --arg name "$folder_name" '{name: $name, mimeType: "application/vnd.google-apps.folder"}')
  fi

  curl -s -X POST \
    -H "Authorization: Bearer $ACCESS_TOKEN" \
    -H "Content-Type: application/json" \
    -d "$payload" \
    "https://www.googleapis.com/drive/v3/files?supportsAllDrives=true" | jq -r '.id // empty'
}

echo "🔍 Searching for accessible Google Drive folders..."
ALL_FOLDERS_JSON=$(drive_list_folders "")
TOTAL_ACCESSIBLE=$(echo "$ALL_FOLDERS_JSON" | jq '.files | length')

if [ "$TOTAL_ACCESSIBLE" -eq 0 ]; then
  echo ""
  echo "⚠️  NO ACCESSIBLE FOLDERS FOUND in Google Drive!"
  echo "👉 Action Required:"
  echo "   1. Open Google Drive (web or Finder)."
  echo "   2. Right-click your target folder (e.g. 'LOCAL', 'DEV', or 'Platform')."
  echo "   3. Click 'Share' and paste the Service Account Email:"
  echo "      $CLIENT_EMAIL"
  echo "   4. Set permission to 'Editor' and save."
  echo "   5. Re-run this script."
  exit 1
fi

echo "📁 Found $TOTAL_ACCESSIBLE accessible folder(s)."

# Identify target root folder based on environment name
ROOT_NAME="LOCAL"
if [ "$ENV_TARGET" = "dev" ]; then
  ROOT_NAME="DEV"
elif [ "$ENV_TARGET" = "prod" ]; then
  ROOT_NAME="PROD"
fi

ROOT_FOLDER_ID=$(echo "$ALL_FOLDERS_JSON" | jq -r --arg name "$ROOT_NAME" '.files[] | select(.name | ascii_upcase == ($name | ascii_upcase)) | .id' | head -n 1)

if [ -z "$ROOT_FOLDER_ID" ]; then
  # Fallback to parent '4. Platform & Automation' or 'Platform'
  ROOT_FOLDER_ID=$(echo "$ALL_FOLDERS_JSON" | jq -r '.files[] | select(.name | contains("Platform")) | .id' | head -n 1)
fi

# Search for required subfolders
find_or_create_subfolder() {
  local name="$1"
  local folder_id=""

  # First try matching by parent folder ID (exact hierarchy match)
  if [ -n "$ROOT_FOLDER_ID" ]; then
    folder_id=$(echo "$ALL_FOLDERS_JSON" | jq -r --arg name "$name" --arg parent "$ROOT_FOLDER_ID" \
      '.files[] | select((.name | ascii_upcase) == ($name | ascii_upcase)) | select(.parents != null and (.parents | index($parent) != null)) | .id' | head -n 1)
  fi

  # Fallback to general name match if not matched by parent
  if [ -z "$folder_id" ]; then
    folder_id=$(echo "$ALL_FOLDERS_JSON" | jq -r --arg name "$name" \
      '.files[] | select((.name | ascii_upcase) == ($name | ascii_upcase)) | .id' | head -n 1)
  fi

  if [ -z "$folder_id" ] && [ "$CREATE_MISSING" = true ] && [ -n "$ROOT_FOLDER_ID" ]; then
    echo "  ➕ Creating missing folder '$name' in Google Drive..."
    folder_id=$(drive_create_folder "$name" "$ROOT_FOLDER_ID")
  fi

  echo "$folder_id"
}

MAIN_FOLDER_ID="${ROOT_FOLDER_ID:-}"
MENTORS_PROFILE_ID=$(find_or_create_subfolder "MENTOR_PICTURES")
RESOURCES_ID=$(find_or_create_subfolder "RESOURCES")
EVENTS_ID=$(find_or_create_subfolder "EVENTS")
MENTORS_ID=$(find_or_create_subfolder "MENTOR_RESOURCES")
IMAGES_ID=$(find_or_create_subfolder "IMAGES")

if [ -z "$MENTORS_ID" ]; then
  MENTORS_ID=$(find_or_create_subfolder "MENTORS")
fi

echo ""
echo "============================================================"
echo "📋 DISCOVERED GOOGLE DRIVE FOLDER IDS ($ENV_TARGET)"
echo "============================================================"
echo "MAIN_FOLDER:            ${MAIN_FOLDER_ID:-[Not Found]}"
echo "MENTORS_PROFILE_FOLDER: ${MENTORS_PROFILE_ID:-[Not Found]}"
echo "RESOURCES_FOLDER:       ${RESOURCES_ID:-[Not Found]}"
echo "EVENTS_FOLDER:          ${EVENTS_ID:-[Not Found]}"
echo "MENTORS_FOLDER:         ${MENTORS_ID:-[Not Found]}"
echo "IMAGES_FOLDER:          ${IMAGES_ID:-[Not Found]}"
echo "============================================================"
echo ""

# Live Upload & Permission Test
TEST_FOLDER_ID="${MENTORS_PROFILE_ID:-$MAIN_FOLDER_ID}"
if [ -n "$TEST_FOLDER_ID" ]; then
  echo "🧪 Running Live Upload & Permission Verification..."
  TEST_FILE_CONTENT="WCC Google Drive Live Connection Test - $(date)"
  
  # Upload test file via multipart upload
  BOUNDARY="-------314159265358979323846"
  UPLOAD_BODY="$TMP_DIR/upload_body.bin"
  
  cat <<EOF > "$UPLOAD_BODY"
--$BOUNDARY
Content-Type: application/json; charset=UTF-8

{
  "name": "wcc-test-verification.txt",
  "parents": ["$TEST_FOLDER_ID"]
}

--$BOUNDARY
Content-Type: text/plain

$TEST_FILE_CONTENT
--$BOUNDARY--
EOF

  UPLOAD_RESP=$(curl -s -X POST \
    -H "Authorization: Bearer $ACCESS_TOKEN" \
    -H "Content-Type: multipart/related; boundary=$BOUNDARY" \
    --data-binary @"$UPLOAD_BODY" \
    "https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart&supportsAllDrives=true&fields=id,name,webViewLink")

  UPLOADED_FILE_ID=$(echo "$UPLOAD_RESP" | jq -r '.id // empty')
  WEB_LINK=$(echo "$UPLOAD_RESP" | jq -r '.webViewLink // empty')

  if [ -n "$UPLOADED_FILE_ID" ]; then
    echo "  ✅ Upload test passed! File ID: $UPLOADED_FILE_ID"
    
    # Set public reader permission
    PERM_RESP=$(curl -s -X POST \
      -H "Authorization: Bearer $ACCESS_TOKEN" \
      -H "Content-Type: application/json" \
      -d '{"role":"reader","type":"anyone"}' \
      "https://www.googleapis.com/drive/v3/files/${UPLOADED_FILE_ID}/permissions?supportsAllDrives=true")
    
    echo "  ✅ Public reader permission applied!"
    echo "  🔗 Web View Link: $WEB_LINK"

    # Delete test file
    curl -s -X DELETE \
      -H "Authorization: Bearer $ACCESS_TOKEN" \
      "https://www.googleapis.com/drive/v3/files/${UPLOADED_FILE_ID}?supportsAllDrives=true" > /dev/null
    
    echo "  🧹 Cleaned up test file."
    echo "🎉 Live verification successful! Service account has full Read/Write permissions."
  else
    echo "❌ Upload test failed. Response:"
    echo "$UPLOAD_RESP" | jq . || echo "$UPLOAD_RESP"
  fi
  echo "------------------------------------------------------------"
fi

# Format Raw JSON Credentials for environment variables
RAW_JSON_CONTENT=$(jq -c . "$KEY_FILE")

# Handle Output per Environment
if [ "$ENV_TARGET" = "local" ]; then
  ENV_FILE=".env.local"
  echo "💾 Writing local configuration to $ENV_FILE..."
  cat <<EOF > "$ENV_FILE"
# WCC Backend - Google Drive Configuration (Local)
SPRING_PROFILES_ACTIVE=qa
STORAGE_TYPE=google
STORAGE_GOOGLE_DRIVE_CREDENTIALS_JSON='$RAW_JSON_CONTENT'
GOOGLE_DRIVE_CREDENTIALS_JSON='$RAW_JSON_CONTENT'
STORAGE_FOLDERS_MAIN_FOLDER=${MAIN_FOLDER_ID:-}
STORAGE_FOLDERS_MENTORS_PROFILE_FOLDER=${MENTORS_PROFILE_ID:-}
STORAGE_FOLDERS_RESOURCES_FOLDER=${RESOURCES_ID:-}
STORAGE_FOLDERS_EVENTS_FOLDER=${EVENTS_ID:-}
STORAGE_FOLDERS_MENTORS_FOLDER=${MENTORS_ID:-}
STORAGE_FOLDERS_IMAGES_FOLDER=${IMAGES_ID:-}
EOF
  echo "✅ Configuration saved to $ENV_FILE"
  echo ""
  echo "👉 To run the backend with these settings:"
  echo "   export \$(cat $ENV_FILE | xargs)"
  echo "   ./gradlew bootRun"

elif [ "$ENV_TARGET" = "dev" ] || [ "$ENV_TARGET" = "prod" ]; then
  echo "✈️ Fly.io Configuration for '$FLY_APP':"
  echo ""
  
  if [ "$SET_FLY_SECRETS" = true ]; then
    echo "🚀 Applying secrets to Fly.io ($FLY_APP)..."
    fly secrets set \
      STORAGE_TYPE="google" \
      GOOGLE_DRIVE_CREDENTIALS_JSON="$RAW_JSON_CONTENT" \
      STORAGE_GOOGLE_DRIVE_CREDENTIALS_JSON="$RAW_JSON_CONTENT" \
      STORAGE_FOLDERS_MAIN_FOLDER="${MAIN_FOLDER_ID:-}" \
      STORAGE_FOLDERS_MENTORS_PROFILE_FOLDER="${MENTORS_PROFILE_ID:-}" \
      STORAGE_FOLDERS_RESOURCES_FOLDER="${RESOURCES_ID:-}" \
      STORAGE_FOLDERS_EVENTS_FOLDER="${EVENTS_ID:-}" \
      STORAGE_FOLDERS_MENTORS_FOLDER="${MENTORS_ID:-}" \
      STORAGE_FOLDERS_IMAGES_FOLDER="${IMAGES_ID:-}" \
      -a "$FLY_APP"
    echo "✅ Fly.io secrets successfully set for $FLY_APP!"
  else
    echo "Run the following command to set Fly.io secrets:"
    echo ""
    echo "fly secrets set \\"
    echo "  STORAGE_TYPE=\"google\" \\"
    echo "  GOOGLE_DRIVE_CREDENTIALS_JSON='$(echo "$RAW_JSON_CONTENT")' \\"
    echo "  STORAGE_GOOGLE_DRIVE_CREDENTIALS_JSON='$(echo "$RAW_JSON_CONTENT")' \\"
    echo "  STORAGE_FOLDERS_MAIN_FOLDER=\"${MAIN_FOLDER_ID:-}\" \\"
    echo "  STORAGE_FOLDERS_MENTORS_PROFILE_FOLDER=\"${MENTORS_PROFILE_ID:-}\" \\"
    echo "  STORAGE_FOLDERS_RESOURCES_FOLDER=\"${RESOURCES_ID:-}\" \\"
    echo "  STORAGE_FOLDERS_EVENTS_FOLDER=\"${EVENTS_ID:-}\" \\"
    echo "  STORAGE_FOLDERS_MENTORS_FOLDER=\"${MENTORS_ID:-}\" \\"
    echo "  STORAGE_FOLDERS_IMAGES_FOLDER=\"${IMAGES_ID:-}\" \\"
    echo "  -a $FLY_APP"
    echo ""
    echo "💡 Tip: Pass '--set-fly-secrets' to execute this command automatically."
  fi
fi

echo "============================================================"
