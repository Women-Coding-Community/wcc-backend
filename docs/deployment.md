# Deployment Guide for WCC Backend

## Deploy application

### Locally with docker compose

* Build and create jar

```shell
 ./gradlew clean bootJar
 ```

* Start docker compose

```shell
docker compose -f docker/docker-compose.yml up --build
```

**Note**: This will create two Docker instances in your Docker desktop:

1. postgres
2. springboot-app

* Debug application

To debug the application, STOP the docker container of the application, springboot-app. Do not stop
the container of the postgres. Start the application from your IDE.

* Stop docker compose

```shell
cd docker
docker compose down
```

* List resources in docker container
  docker exec -it wcc-backend ls -al /app/resources

## Deploy remotely or in cloud

### Setup Fly.io

1. Install [fly.io](https://fly.io/docs/flyctl/install)
2. Login `fly auth login` or create account `fly auth signup`
3. build create jar: `./gradlew clean bootJar`
4. First deploy `fly launch`

#### Deploying with Fly.io

1. build create jar: `./gradlew clean bootJar`
2. Update deploy `fly deploy`
3. Check application log during deployment: ```fly logs -a wcc-backend```
4. Access the application [here](https://wcc-backend.fly.dev/swagger-ui/index.html)
5. Check application status: ``fly status -a wcc-backend``

#### Testing Fly.io Deployment

After deploying, verify the authentication endpoint is working correctly:

```bash
curl -X 'POST' \
  'https://wcc-backend.fly.dev/api/auth/login' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "email": "your-email@example.com",
  "password": "your-secure-password"
}'
```

**Note**: Replace `your-email@example.com` and `your-secure-password` with valid admin credentials configured in your fly.io environment.

**Expected response:**
```json
{
  "token": "generated-jwt-token",
  "expiresAt": "2025-11-16T10:48:50.992574288Z",
  "roles": ["ADMIN"]
}
```

A successful response confirms:
- Backend is running on fly.io
- Database connectivity is working
- Authentication system is functioning properly
- CORS configuration is correct