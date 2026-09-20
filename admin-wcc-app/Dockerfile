# Admin portal image used by docker/docker-compose.qa.yml.
#
# NEXT_PUBLIC_* variables are inlined into the browser bundle at build time, so
# they are passed as build args. The portal calls the backend from the browser,
# which is why NEXT_PUBLIC_API_BASE must be the host-reachable URL
# (http://localhost:8080), not the compose service name.

# Stage 1: install dependencies
FROM node:20-alpine AS deps
WORKDIR /app
COPY package.json package-lock.json ./
# --ignore-scripts skips the `prepare` hook, which runs husky from the parent
# repository and does not exist inside the build context.
RUN npm ci --ignore-scripts

# Stage 2: build the standalone bundle (next.config.mjs sets output: 'standalone')
FROM node:20-alpine AS build
WORKDIR /app
ARG NEXT_PUBLIC_API_BASE=http://localhost:8080
ARG NEXT_PUBLIC_API_KEY=local
ARG NEXT_PUBLIC_APP_URL=http://localhost:3000
ENV NEXT_PUBLIC_API_BASE=$NEXT_PUBLIC_API_BASE \
    NEXT_PUBLIC_API_KEY=$NEXT_PUBLIC_API_KEY \
    NEXT_PUBLIC_APP_URL=$NEXT_PUBLIC_APP_URL \
    NEXT_TELEMETRY_DISABLED=1
COPY --from=deps /app/node_modules ./node_modules
COPY . .
RUN npm run build

# Stage 3: minimal runtime
FROM node:20-alpine AS runner
WORKDIR /app
ENV NODE_ENV=production \
    NEXT_TELEMETRY_DISABLED=1 \
    PORT=3000 \
    HOSTNAME=0.0.0.0
COPY --from=build /app/public ./public
COPY --from=build /app/.next/standalone ./
COPY --from=build /app/.next/static ./.next/static
EXPOSE 3000
CMD ["node", "server.js"]
