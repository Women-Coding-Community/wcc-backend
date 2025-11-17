# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

WCC (Women Coding Community) Platform Backend - A Spring Boot 3.2.5 application (Java 21) providing:
- Public CMS API for website content (landing page, events, mentorship info)
- Private platform API for managing members, mentors, and resources
- Authentication system with API keys and JWT-like tokens
- Admin frontend (Next.js) for content management

## Build and Development Commands

### Backend (Java/Gradle)

**Build and run with Docker:**
```bash
./gradlew clean bootJar
docker compose -f docker/docker-compose.yml up --build
```

**Run tests:**
```bash
./gradlew test                    # Unit tests
./gradlew testIntegration         # Integration tests (requires Docker running)
```

**Run locally from IDE:**
1. Start PostgreSQL: `docker compose -f docker/docker-compose.yml up postgres`
2. Run `PlatformApplication.java` from IntelliJ (right-click > Run/Debug)

**Run with Gradle:**
```bash
./gradlew bootRun
```

**Quality checks:**
```bash
./gradlew pmdMain pmdTest         # PMD static analysis
./gradlew test jacocoTestReport   # Code coverage (minimum 70% required)
./gradlew check                   # Runs all checks including coverage verification
```

**Local SonarQube analysis:**
```bash
./gradlew sonarQubeAnalysis -PlocalProfile
```

### Frontend (Next.js)

```bash
cd admin-wcc-app
npm install
npm run dev        # Start dev server on http://localhost:3000
npm test           # Run Jest tests
npm run build      # Production build
```

**Default admin credentials (local):**
- Email: `admin@wcc.dev`
- Password: `wcc-admin`

## Architecture

### Layered Architecture

```
Controllers (REST endpoints)
    ↓
Services (business logic)
    ↓
Repositories (data access)
    ↓
Database (PostgreSQL) / Files (JSON)
```

### Key Architectural Patterns

**1. Hybrid Repository Pattern (File + Database)**

The application uses a dual-source repository pattern with automatic fallback:

- **Primary**: PostgreSQL with JSONB columns (via `PostgresPageRepository`)
- **Fallback**: Static JSON files in `src/main/resources/init-data/`
- **Mechanism**: If database query returns empty, falls back to JSON files using `PageType` enum

This allows:
- Development with static content (no database setup needed initially)
- Production with dynamic content managed via admin API
- Version-controlled default content

Files in `init-data/`: `landingPage.json`, `eventsPage.json`, `mentorshipPage.json`, etc.

**2. Dual Authentication System**

Two independent authentication mechanisms:

**API Key Authentication** (`ApiKeyFilter`):
- For public CMS API (`/api/cms/v1/**`) and platform API (`/api/platform/v1/**`)
- Header: `X-API-KEY` or query param `api_key`
- Configured via `security.api.key` property
- Can be disabled: `security.enabled=false`

**Token Authentication** (`TokenAuthFilter`):
- For admin frontend authentication
- Login: `POST /api/auth/login` with email/password
- Returns token with expiry (configurable TTL)
- Token sent as `Authorization: Bearer <token>`
- Tokens stored in `user_token` table with expiry tracking
- Passwords hashed with Argon2

**3. Pluggable File Storage**

Interface: `FileStorageRepository` with two implementations (selected by `storage.type` property):

- **Google Drive** (`storage.type=google-drive`):
  - Requires `credentials.json` in resources (see `docs/google_drive_setup.md`)
  - Uploads to configured folder IDs
  - Sets public read permissions
  - Returns shareable web links

- **Local Filesystem** (`storage.type=local`):
  - Stores in `file.storage.directory` path
  - Returns file:// URLs
  - Good for development

Used for: Member profile pictures, mentor resources, event images, general uploads

**4. Custom Repository Pattern**

NOT using Spring Data JPA. Custom `CrudRepository` interface with implementations:

- `PostgresMemberRepository`, `PostgresMentorRepository` - Use Spring JdbcTemplate
- `PostgresPageRepository` - JSONB storage for flexible CMS content
- `FilePageRepository` - Reads from JSON files (fallback)

Database migrations managed by Flyway in `src/main/resources/db/migration/`

### Package Structure

- `controller/` - REST endpoints, two main API namespaces:
  - `/api/cms/v1/` - Public CMS content
  - `/api/platform/v1/` - Internal CRUD operations
  - `/api/auth/` - Authentication

- `service/` - Business logic layer (CmsService, MemberService, AuthService, etc.)

- `repository/` - Data access implementations
  - `file/` - File-based repositories
  - `spi/` - Repository interfaces
  - `googledrive/` - Google Drive integration

- `domain/` - Domain models organized by concern:
  - `cms/` - CMS page objects and attributes
  - `platform/` - Core entities (Member, Mentor, Event, Programme)
  - `auth/` - UserAccount, UserToken
  - `exceptions/` - Custom exceptions
  - `resource/` - File storage models

- `configuration/` - Spring configuration beans
  - `SecurityConfig` - API key and token filters
  - `CorsConfig` - CORS setup (origins from `app.cors.allowed-origins`)
  - `DataSourceConfig` - Database configuration
  - `ObjectMapperConfig` - Jackson customizations

- `bootstrap/` - Application initialization and seeding

### Database Schema

PostgreSQL with hybrid approach:
- Structured tables for core entities (member, mentor, user_account, user_token)
- JSONB columns for flexible CMS content (page table)
- Flyway migrations for version control

Key tables:
- `page` - Dynamic CMS content (JSONB)
- `member` - Community members with skills, links, images
- `mentor` - Mentorship data with availability
- `mentorship_cycle` - Mentorship program cycles
- `user_account` - Authentication (email, hashed password)
- `user_token` - Session tokens with expiry

## Configuration Patterns

**Profile-based configuration:**
- `application.yml` - Base configuration
- `application-local.yml` - Local development
- `application-flyio.yml` - Fly.io deployment
- `application-prod.yml` - Production settings
- `application-memory.yml` - In-memory H2 database

**Key configuration properties:**

```yaml
security.enabled: true              # Enable/disable authentication
security.api.key: <your-key>        # API key value
security.token.ttl-minutes: 60      # Token expiry time

storage.type: local                 # Or 'google-drive'
file.storage.directory: data        # Local storage path

app.cors.allowed-origins: http://localhost:3000,https://your-frontend.com
app.seed.admin.enabled: true        # Auto-create admin user
app.seed.admin.email: admin@wcc.dev
app.seed.admin.password: wcc-admin
```

**CORS:** Update `app.cors.allowed-origins` when deploying frontend to include production URL.

## Testing Patterns

**Test structure:**
- `src/test/java` - Unit tests (Mockito, JUnit 5)
- `src/testInt/java` - Integration tests (Testcontainers with PostgreSQL)

**Integration tests require Docker daemon running:**
```bash
docker ps  # Verify Docker is running before tests
```

**Coverage requirements:**
- Minimum 70% coverage enforced by `jacocoTestCoverageVerification`
- Reports in `build/reports/jacoco/test/html/index.html`

**Test naming and documentation:**
- Use `@DisplayName` annotation with Given-When-Then format for all tests
- Format: `"Given [precondition], when [action], then [expected outcome]"`
- Do NOT use `// Given`, `// When`, `// Then` comments in test body
- Test method names should be descriptive using `should` prefix (e.g., `shouldCreateCorsConfigurationSourceWithAllowedOrigins`)
- Use AssertJ assertions (`assertThat`) for all assertions

Example:
```java
@Test
@DisplayName("Given allowed origins are configured, when creating CORS configuration source, then it should contain the allowed origins")
void shouldCreateCorsConfigurationSourceWithAllowedOrigins() {
  List<String> allowedOrigins = List.of("http://localhost:3000");
  CorsConfig corsConfig = new CorsConfig(allowedOrigins);

  CorsConfigurationSource source = corsConfig.corsConfigurationSource();

  assertThat(source).isInstanceOf(UrlBasedCorsConfigurationSource.class);
}
```

## Important Conventions

- **Lombok**: Extensively used (`@Data`, `@Builder`, `@RequiredArgsConstructor`)
- **Records**: Used for DTOs, requests, responses
- **Builder pattern**: For complex object construction
- **Service layer**: Contains all business logic, controllers delegate to services
- **Global exception handling**: `GlobalExceptionHandler` catches all exceptions
- **OpenAPI/Swagger**: All endpoints documented, available at `/swagger-ui/index.html`
- **Google Java Format**: Code formatting enforced (see README for IntelliJ setup)

## Common Development Tasks

**Add a new CMS page:**
1. Create JSON file in `src/main/resources/init-data/`
2. Add enum value to `PageType` mapping to file path
3. Create domain class in `domain.cms.pages/`
4. Add controller method in appropriate controller
5. Service layer will automatically use hybrid repository pattern

**Add a new database entity:**
1. Create Flyway migration in `src/main/resources/db/migration/`
2. Create domain class in `domain.platform/`
3. Create repository interface extending `CrudRepository`
4. Implement repository using JdbcTemplate (see existing repositories as examples)
5. Create service class for business logic
6. Add controller endpoints

**Modify authentication:**
- API key: Update `SecurityConfig` and `ApiKeyFilter`
- Token auth: Update `TokenAuthFilter` and `AuthService`
- Token TTL: Change `security.token.ttl-minutes` property

**Add file upload endpoint:**
1. Inject `FileStorageRepository` in service
2. Use `uploadFile(fileName, inputStream, folderId)` method
3. Store returned `FileStored` object (contains URI, metadata)
4. Configure folder IDs in `storage.folders.*` properties

## Deployment

### Backend Deployment (Fly.io)

**Deploy to Fly.io:**
```bash
# Build the JAR
./gradlew clean bootJar

# Deploy to Fly.io
fly deploy

# Check deployment status
fly status -a wcc-backend

# View logs
fly logs -a wcc-backend

# View secrets/environment variables
fly secrets list -a wcc-backend
```

**Manage Fly.io Environment Variables:**
```bash
# Set a new secret
fly secrets set SECURITY_API_KEY=your-api-key -a wcc-backend

# Set multiple secrets at once
fly secrets set \
  SECURITY_ENABLED=true \
  APP_CORS_ALLOWED_ORIGINS=https://dev-wcc-admin.vercel.app \
  -a wcc-backend

# Remove a secret
fly secrets unset SECRET_NAME -a wcc-backend

# Note: Setting secrets triggers automatic redeployment
```

**Important Fly.io Secrets:**
Required environment variables (see `application-flyio.yml`):
- `SPRING_DATASOURCE_URL` - PostgreSQL connection URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `SPRING_DATASOURCE_DRIVER_CLASS_NAME` - `org.postgresql.Driver`
- `SECURITY_API_KEY` - API key for authentication
- `SECURITY_ENABLED` - `true` or `false`
- `APP_CORS_ALLOWED_ORIGINS` - Comma-separated frontend URLs (e.g., `https://dev-wcc-admin.vercel.app,https://prod-wcc-admin.vercel.app`)
- `APP_SEED_ADMIN_EMAIL` - Admin user email
- `APP_SEED_ADMIN_PASSWORD` - Admin user password
- `APP_SEED_ADMIN_ENABLED` - `true` or `false`
- `SPRING_FLYWAY_ENABLED` - `true` for database migrations
- `SPRING_FLYWAY_CLEAN_DISABLED` - `true` for production safety

**Common Fly.io Commands:**
```bash
fly status -a wcc-backend          # Check app status
fly logs -a wcc-backend            # View real-time logs
fly ssh console -a wcc-backend     # SSH into the container
fly postgres connect -a <db-name>  # Connect to PostgreSQL database
```

### Frontend Deployment (Vercel)

**Prerequisites:**
- Vercel CLI installed: `npm install -g vercel`
- Logged in: `vercel login`

**Deploy/Redeploy Frontend:**

**Option 1: Redeploy existing deployment (recommended for updates)**
```bash
# From the admin-wcc-app directory
cd admin-wcc-app

# List recent deployments
vercel ls

# Redeploy the latest production deployment
# This rebuilds with updated environment variables
echo "y" | vercel redeploy <deployment-url>

# Example:
echo "y" | vercel redeploy https://dev-wcc-admin-dd1zukrcf-women-coding-communitys-projects.vercel.app
```

**Option 2: Fresh deployment**
```bash
# From the project root directory (NOT admin-wcc-app)
cd /path/to/wcc-backend

# Deploy to production
vercel --prod --yes
```

**Manage Vercel Environment Variables:**
```bash
# List all environment variables
vercel env ls --cwd admin-wcc-app

# Remove an environment variable
vercel env rm NEXT_PUBLIC_API_BASE production --cwd admin-wcc-app --yes

# Add a new environment variable
echo "https://wcc-backend.fly.dev" | vercel env add NEXT_PUBLIC_API_BASE production --cwd admin-wcc-app

# Add for all environments (production, preview, development)
echo "your-api-key" | vercel env add NEXT_PUBLIC_API_KEY --cwd admin-wcc-app
# When prompted, select all environments

# Pull environment variables to local .env file
vercel env pull --cwd admin-wcc-app
```

**Important Vercel Environment Variables:**
Required for frontend (see `.env.example`):
- `NEXT_PUBLIC_API_BASE` - Backend URL (e.g., `https://wcc-backend.fly.dev`)
- `NEXT_PUBLIC_API_KEY` - API key matching backend's `SECURITY_API_KEY`
- `NEXT_PUBLIC_APP_URL` - Frontend URL (e.g., `https://dev-wcc-admin.vercel.app`)

**GitHub Actions Deployment:**
Automated deployment via `.github/workflows/deploy-frontend.yml`:
- Triggers on push to `main` branch when `admin-wcc-app/**` files change
- Requires GitHub secrets:
  - `VERCEL_TOKEN` - Vercel API token
  - `VERCEL_ORG_ID` - Vercel organization ID
  - `VERCEL_PROJECT_ID` - Vercel project ID
  - `NEXT_PUBLIC_API_BASE` - Backend URL
  - `NEXT_PUBLIC_API_KEY` - API key
  - `NEXT_PUBLIC_APP_URL` - Frontend URL

**Verify Deployment:**
```bash
# Check deployment status
vercel inspect <deployment-url> --cwd admin-wcc-app

# View deployment logs (in Vercel dashboard)
# https://vercel.com/<org>/<project>/deployments

# Test the deployed app
curl https://dev-wcc-admin.vercel.app/api/health
```

### Local with Docker

For local development with Docker:
```bash
# Build and run both backend and database
./gradlew clean bootJar
docker compose -f docker/docker-compose.yml up --build

# Run only database for local development
docker compose -f docker/docker-compose.yml up postgres
```

### Deployment Checklist

**Before deploying backend:**
1. ✅ Run tests: `./gradlew test testIntegration`
2. ✅ Check code quality: `./gradlew check`
3. ✅ Build JAR: `./gradlew clean bootJar`
4. ✅ Verify Fly.io secrets are set correctly
5. ✅ Update CORS origins if frontend URL changed

**Before deploying frontend:**
1. ✅ Run tests: `cd admin-wcc-app && npm test`
2. ✅ Build locally: `npm run build`
3. ✅ Verify Vercel env vars match backend configuration
4. ✅ Ensure `NEXT_PUBLIC_API_BASE` points to correct backend URL
5. ✅ Ensure backend CORS includes frontend URL

**After deployment:**
1. ✅ Check logs for errors: `fly logs -a wcc-backend` or Vercel dashboard
2. ✅ Test login at frontend URL
3. ✅ Verify API connectivity from frontend to backend
4. ✅ Check health endpoints: `/actuator/health`

### Troubleshooting Deployments

**Backend issues:**
- **CORS errors**: Update `APP_CORS_ALLOWED_ORIGINS` in Fly.io secrets
- **Database connection**: Check `SPRING_DATASOURCE_*` secrets
- **500 errors**: Check `fly logs -a wcc-backend` for stack traces

**Frontend issues:**
- **Can't connect to backend**: Verify `NEXT_PUBLIC_API_BASE` is correct
- **401 Unauthorized**: Check `NEXT_PUBLIC_API_KEY` matches backend
- **Deployment failed**: Check build logs in Vercel dashboard
- **Old code deployed**: Use `vercel redeploy` to force rebuild

## API Documentation

- Swagger UI (local): http://localhost:8080/swagger-ui/index.html
- OpenAPI spec: http://localhost:8080/api-docs
- Generate Postman collection: `./gradlew postmanGenerate`

See `docs/resource_api.md` for detailed resource API documentation.
