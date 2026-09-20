# Tiny runner for the seed scripts in scripts/ (see docker-compose.qa.yml).
# bash + curl + jq drive the API; psql seeds mentorship cycles and sets the QA
# account passwords directly; argon2 hashes those passwords the way the backend
# (Argon2PasswordEncoder) expects.
FROM alpine:3.20
RUN apk add --no-cache bash curl jq postgresql16-client argon2
WORKDIR /seed
