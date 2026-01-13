# Women Coding Community (WCC) Platform

## Technical Design Document

**Version:** 2.0
**Date:** January 12, 2026
**Status:** Active Development - MVP in Progress

---

## Table of Contents

1. [Executive Summary](#executive-summary)
2. [System Architecture](#system-architecture)
3. [Tech Stack](#tech-stack)
4. [Core Features](#core-features)
5. [MVP Scope & Current Status](#mvp-scope--current-status)
6. [Database Design](#database-design)
7. [API Architecture](#api-architecture)
8. [Security & Authentication](#security--authentication)
9. [Deployment Architecture](#deployment-architecture)
10. [Integration Points](#integration-points)
11. [Critical Gaps for MVP](#critical-gaps-for-mvp)

---

## Executive Summary

### Project Vision

Transform the Women Coding Community's static GitHub Pages website into a dynamic, scalable platform
that serves dual purposes:

1. **Functional Infrastructure**: Automate manual administrative tasks and enable efficient
   management of community operations, particularly mentorship coordination
2. **Learning Platform**: Provide hands-on development opportunities for members to build real-world
   experience with modern tech stacks

### Success Criteria

- Production-ready platform reducing volunteer workload by 70% through automation
- **Automated mentorship matching and approval workflow** - Core MVP feature
- Comprehensive mentor-mentee application and assignment system
- Real-world portfolio-building opportunities for 100+ community members
- Sustainable, cost-effective infrastructure (Vercel + Fly.io)

---

## System Architecture

### High-Level Architecture

```
┌────────────────────────────────────────────────────────────────┐
│                        PRESENTATION LAYER                      │
├─────────────────┬─────────────────────┬────────────────────────┤
│  Public Website │   Admin Dashboard   │   Mobile/Responsive    │
│   (Next.js)     │    (Next.js + MUI)  │      Interface         │
└────────┬────────┴──────────┬──────────┴──────────┬─────────────┘
         │                   │                     │
         └───────────────────┼─────────────────────┘
                             │
                             │
         ┌───────────────────▼─────────────────-────┐
         │         APPLICATION LAYER                │
         │      Spring Boot 3.x Backend API         │
         ├──────────────────────────────────────────┤
         │  • REST API Controllers                  │
         │  • Service Layer (Business Logic)        │
         │  • Repository Layer (Data Access)        │
         │  • Security & Auth (JWT)                 │
         │  • Email Service (Transactional)         │
         │  • Resource Management Service           │
         └───────────────────┬──────────────────-───┘
                             │
         ┌───────────────────▼─────────────────────┐
         │           DATA LAYER                    │
         ├─────────────────┬───────────────────────┤
         │   PostgreSQL    │   File Storage        │
         │   Database      │   (Images, PDFs)      │
         └─────────────────┴───────────────────────┘
                             │
         ┌───────────────────▼─────────────────────┐
         │      EXTERNAL INTEGRATIONS              │
         ├─────────────────────────────────────────┤
         │  • Email Service (SMTP/SendGrid)        │
         │  • Google Drive API                     │
         │  • Analytics & Monitoring               │
         └─────────────────────────────────────────┘
```

### Component Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                     WCC BACKEND SERVICE                      │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌────────────────────────────────────────────────────────┐  │
│  │              REST API CONTROLLERS                      │  │
│  ├──────────────┬──────────────┬──────────────┬───────────┤  │
│  │ Auth API     │ Member API   │ Mentor API   │ CMS API   │  │
│  │ /api/auth    │ /api/members │ /api/mentors │ /api/cms  │  │
│  └──────┬───────┴──────┬───────┴──────┬───────┴─────┬─────┘  │
│         │              │              │             │        │
│  ┌──────▼──────────────▼──────────────▼─────────────▼─────┐  │
│  │                   SERVICE LAYER                        │  │
│  ├──────────────┬──────────────┬──────────────────────────┤  │
│  │ UserService  │ MemberService│ MentorshipService        │  │
│  │ AuthService  │ ResourceServ │ CmsService               │  │
│  │ EmailService │ EventService │ NotificationService      │  │
│  └──────┬───────┴──────┬───────┴──────────────┬───────────┘  │
│         │              │                      │              │
│  ┌──────▼──────────────▼──────────────────────▼───────────┐  │
│  │               REPOSITORY LAYER                         │  │
│  ├──────────────┬──────────────┬──────────────────────────┤  │
│  │ UserRepo     │ MemberRepo   │ MentorshipRepo           │  │
│  │ ResourceRepo │ PageRepo     │ EventRepo                │  │
│  └──────┬───────┴──────┬───────┴──────────────┬───────────┘  │
│         │              │                       │             │
│  ┌──────▼──────────────▼───────────────────────▼───────────┐ │
│  │            FLYWAY DB MIGRATIONS                         │ │
│  │  V1__initial_schema.sql                                 │ │
│  │  V2__add_mentorship_tables.sql                          │ │
│  │  V3__add_resources_tables.sql                           │ │
│  └─────────────────────────────────────────────────────────┘ │
│                                                              │
└──────────────────────────────────────────────────────────────┘
                             │
                    ┌────────▼──────────┐
                    │   PostgreSQL 15   │
                    │   Database        │
                    └───────────────────┘
```

---

## Tech Stack

### Backend Stack

```yaml
Core Framework:
  - Spring Boot: 3.3.x
  - Java: 21 (LTS)
  - Build Tool: Gradle (Kotlin DSL)

Database:
  - PostgreSQL: 15
  - Migration: FlywayDB
  - Connection Pool: HikariCP

Security:
  - Spring Security 6
  - JWT Authentication
  - BCrypt Password Encoding
  - API Key Authentication

Testing:
  - JUnit 5
  - Mockito
  - TestContainers (PostgreSQL)
  - REST Assured (API Testing)

Code Quality:
  - Lombok
  - Google Java Format
  - Checkstyle
  - PMD
  - SonarQube

API Documentation:
  - OpenAPI 3.0 (Swagger)
  - Springdoc OpenAPI

DevOps:
  - Docker & Docker Compose
  - GitHub Actions (CI/CD)
```

### Frontend Stack

```yaml
Public Website (wcc-frontend):
  - Framework: Next.js 14 (App Router)
  - Language: TypeScript 5.x
  - Runtime: Node.js 20+
  - Package Manager: pnpm 9+
  - UI Framework: React 18
  - Styling: CSS Modules / Tailwind CSS
  - Testing: Jest, React Testing Library, Playwright
  - Linting: ESLint, Prettier
  - Git Hooks: Husky

Admin Dashboard (admin-wcc-app):
  - Framework: Next.js 14
  - Language: TypeScript
  - UI Library: Material-UI (MUI) 6
  - State Management: React Context/Hooks
  - Forms: React Hook Form
  - Authentication: JWT Bearer Token
  - Testing: Jest, React Testing Library
```

### Infrastructure & Deployment

```yaml
Current Hosting:
  - Backend: Fly.io
  - Frontend: Vercel
  - Admin Dashboard: Vercel
  - Database: Fly.io

Target Infrastructure:
  - Database: TBD (cost-effective alternative to Fly.io)
  - Backend: TBD (cost-effective alternative to Fly.io)
  - Frontend/Admin: Vercel or AWS
  - CDN: Vercel Edge Network
  - File Storage: Google Drive API / Cloud Storage

Monitoring & Analytics:
  - Application Monitoring: TBD
  - Error Tracking: TBD
  - Logs: Application logs
```

---

## 11. Critical Gaps for MVP

### Authentication APIs (HIGH PRIORITY)

- ❌ `POST /api/auth/register` - User registration endpoint needed
- ❌ `POST /api/auth/reset-password` - Password reset workflow needed
- ✅ `POST /api/auth/login` - Implemented
- ✅ `GET /api/auth/me` - Implemented

### Mentorship Workflow APIs (CRITICAL)

- ❌ Mentor approval: `PATCH /api/platform/v1/mentors/{id}/approve`
- ❌ Mentor rejection: `PATCH /api/platform/v1/mentors/{id}/reject`
- ❌ Mentee creation: `POST /api/platform/v1/mentees`
- ❌ Mentee approval: `PATCH /api/platform/v1/mentees/{id}/approve`
- ❌ Mentee application: `POST /api/platform/v1/mentees/{id}/apply`
- ❌ View applications: `GET /api/platform/v1/mentees/{id}/applications`
- ❌ Mentor suggest: `GET /api/platform/v1/mentors/suggest?menteeId={id}`
- ❌ Accept/decline: `PATCH /api/platform/v1/mentors/{id}/applications/{appId}/accept`

### Database Tables (CRITICAL)

- ❌ `mentee_applications` table - Tracks applications with priority order
- ❌ `mentorship_matches` table - Confirmed mentor-mentee pairs

### Frontend Dashboards (CRITICAL)

- ❌ Mentorship Team dashboard (approve mentors/mentees)
- ❌ Mentor dashboard (view/accept applications, update profile)
- ❌ Mentee dashboard (view status, apply to mentors with priority)

### Email Workflows (HIGH PRIORITY)

- ❌ Mentorship cycle emails
- ❌ Application approval/rejection notifications
- ❌ Mentor acceptance request emails

---

**Document Version Control:**

- v1.0 - January 2026 - Initial comprehensive design document
