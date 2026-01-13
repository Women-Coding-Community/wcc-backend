# WCC Platform - MVP Features & Implementation Summary

## 🎯 Project Overview

**Mission**: Transform the Women Coding Community's static website into a dynamic, production-ready
platform that automates administrative tasks while providing hands-on learning opportunities for
community members.

**Timeline**: MVP Launch Target - Q1 2026

**Current Status** (Updated January 2026):

- ✅ Backend foundation established (Spring Boot 3.x, PostgreSQL 15)
- ✅ Platform Private Area (admin-wcc-app) deployed to Vercel
- ✅ Email service integrated (Gmail SMTP)
- ✅ File storage system (Google Drive API)
- 🚧 Public website (wcc-frontend) in development
- 🚧 Mentorship matching system in design phase
- ⚠️ Authentication endpoints incomplete (register, password reset needed)
- ⚠️ Mentee application system not yet implemented

---

## 📊 MVP Feature Breakdown

### ✅ **COMPLETED** - Core Infrastructure

#### 1. Authentication System

**Status**: PARTIALLY COMPLETE ⚠️

**Implemented**:

- ✅ Token-based authentication (Argon2 password hashing)
- ✅ API key authentication for public CMS endpoints
- ✅ POST `/api/auth/login` - User login with token generation
- ✅ GET `/api/auth/me` - Get current user info
- ✅ Token storage in `user_token` table with expiry tracking
- ✅ Dual authentication filters: `TokenAuthFilter` and `ApiKeyFilter`

**Missing for MVP** ❌:

- ❌ POST `/api/auth/register` - User registration endpoint
- ❌ POST `/api/auth/reset-password` - Password reset workflow
- ❌ Email verification on registration
- ❌ Refresh token mechanism

**Technical Details**:

- Spring Security 6 integration
- Argon2 password encryption
- Token TTL: Configurable (default 60 minutes)
- `TokenAuthFilter` for admin/platform authentication
- `ApiKeyFilter` for public CMS API access

**Priority**: HIGH - Registration and password reset are critical for MVP

---

#### 2. WCC Private (admin-wcc-app)

**Status**: DEPLOYED TO VERCEL ✅

**Description**: Private administrative area for Super Admins, Admins, Mentorship Team, Mentors,
Mentees, Volunteers, and Community Leaders.

**Completed Features**:

- User management interface
- Member CRUD operations
- Mentor profile management (basic)
- System configuration
- Responsive Material-UI design
- Purple/Pink WCC brand theming
- Authentication with token-based access

**Missing for MVP** ❌:

- ❌ Mentor approval workflow UI (Mentorship Team)
- ❌ Mentee approval workflow UI (Mentorship Team)
- ❌ Mentor dashboard to view/accept mentee applications
- ❌ Mentee dashboard to view application status
- ❌ Mentorship cycle email management
- ❌ Mentee application priority view
- ❌ Suggested mentor matching UI

**Tech Stack**:

- Next.js 14 (App Router)
- Material-UI 6
- TypeScript
- Token authentication
- Deployed on Vercel

**Access**: https://dev-wcc-admin.vercel.app

**Priority**: HIGH - Mentorship workflows are core MVP requirements

---

#### 3. Database Schema

**Status**: PRODUCTION READY (with gaps) ⚠️

**Implementation**:

- PostgreSQL 15 database
- FlywayDB for migrations (V1-V15 applied)
- Versioned schema management
- TestContainers for integration testing

**Implemented Tables**:

- ✅ `user_account` - Authentication (email, hashed password)
- ✅ `user_token` - Session tokens with expiry
- ✅ `members` - User profiles (full_name, email, bio, skills)
- ✅ `member_types` - COLLABORATOR, VOLUNTEER, DIRECTOR, LEAD, EVANGELIST, MEMBER
- ✅ `member_statuses` - ACTIVE, DISABLED, BANNED, PENDING
- ✅ `member_images` - Profile images
- ✅ `member_social_networks` - Social links
- ✅ `mentors` - Mentor profiles (extends members)
- ✅ `mentor_technical_areas` - Backend, Frontend, Data, DevOps, Mobile, QA, Fullstack
- ✅ `mentor_languages` - Programming languages
- ✅ `mentor_mentorship_types` - AD_HOC, LONG_TERM
- ✅ `mentor_mentorship_focus_areas` - Career focus areas
- ✅ `mentor_availability` - Monthly availability tracking
- ✅ `mentorship_cycle` - Mentorship program cycles
- ✅ `pages` - CMS pages (JSONB content)
- ✅ `countries`, `social_network_types`, `permission_types` - Lookup tables

**Missing for MVP** ❌:

- ❌ `mentee_applications` table - Track mentee applications to mentors with priority
- ❌ `mentorship_matches` table - Approved mentor-mentee pairings
- ❌ `feedback` table - Feedback system (proposed but not implemented)

**Priority**: HIGH - Mentee application tables are critical for mentorship workflow

---

#### 4. Email Service

**Status**: PRODUCTION READY ✅

**Implementation**:

- Spring Mail integration with Gmail SMTP
- Single and bulk email sending
- HTML and plain text support
- CC, BCC, Reply-To support
- Comprehensive error handling

**API Endpoints**:

```
POST /api/platform/v1/email/send       - Send single email
POST /api/platform/v1/email/send/bulk  - Send bulk emails
```

**Configuration**:

- SMTP: Gmail (smtp.gmail.com:587)
- Environment variables: `MAIL_USERNAME`, `MAIL_PASSWORD`
- TLS/STARTTLS enabled

**Use Cases**:

- ✅ Transactional emails
- ✅ System notifications
- 🚧 Mentorship cycle emails (not yet implemented)
- 🚧 Mentor/mentee matching notifications (not yet implemented)
- 🚧 Application approval/rejection emails (not yet implemented)

---

#### 5. File Storage Service

**Status**: PRODUCTION READY ✅

**Implementation**:

- Pluggable `FileStorageRepository` interface
- Two implementations:
    - **Google Drive** (`storage.type=google-drive`) - Production
    - **Local Filesystem** (`storage.type=local`) - Development
- Public file sharing with generated links
- Folder-based organization

**Configuration**:

```yaml
storage.type: google-drive
storage.folders.member: <folder-id>
storage.folders.mentor: <folder-id>
```

**Use Cases**:

- ✅ Member profile pictures
- ✅ Mentor resource uploads
- ✅ Event images
- ✅ General file uploads

---

### 🚧 **IN PROGRESS** - Core Features

#### 6. Member Profile Management

**Status**: BACKEND COMPLETE | FRONTEND PARTIAL ✅

**Completed**:

- ✅ Member registration and profile creation
- ✅ Profile CRUD operations via `MemberController`
- ✅ Skills management
- ✅ Profile viewing API
- ✅ Member search and filtering
- ✅ Social network links
- ✅ Profile images

**API Endpoints**:

```
GET /api/platform/v1/members           - Get all members
GET /api/platform/v1/members/{id}      - Get member by ID
POST /api/platform/v1/members          - Create member
PUT /api/platform/v1/members/{id}      - Update member
DELETE /api/platform/v1/members/{id}   - Delete member
```

**Remaining Work**:

- 🚧 Frontend profile pages
- 🚧 Public member directory page
- 🚧 Profile editing interface in admin app

---

#### 7. Mentor Management System

**Status**: BACKEND 80% | FRONTEND 30% 🚧

**Completed**:

- ✅ Mentor profile entity (extends Member)
- ✅ Specialization management (technical areas, languages, focus)
- ✅ Mentor availability tracking (monthly hours)
- ✅ Mentor directory API with filtering
- ✅ Mentorship type support (AD_HOC, LONG_TERM)
- ✅ Mentorship cycle logic (time-based, London timezone)
- ✅ Profile status management (ACTIVE, DISABLED, BANNED, PENDING)

**API Endpoints**:

```
GET /api/cms/v1/mentorship/mentors              - Public mentor list (filtered)
GET /api/platform/v1/mentors                    - Get all mentors (admin)
POST /api/platform/v1/mentors                   - Create mentor profile
```

**Missing for MVP** ❌:

- ❌ PUT `/api/platform/v1/mentors/{id}` - Update mentor profile
- ❌ PATCH `/api/platform/v1/mentors/{id}/approve` - Approve mentor (Mentorship Team)
- ❌ PATCH `/api/platform/v1/mentors/{id}/reject` - Reject mentor
- ❌ GET `/api/platform/v1/mentors/{id}/applications` - View mentee applications
- ❌ PATCH `/api/platform/v1/mentors/{id}/applications/{appId}/accept` - Accept mentee
- ❌ PATCH `/api/platform/v1/mentors/{id}/applications/{appId}/decline` - Decline mentee
- ❌ Frontend mentor approval workflow
- ❌ Frontend mentor dashboard to manage applications
- ❌ Auto-transition from PENDING to ACTIVE on approval

**Priority**: HIGH - Mentor approval and application management are core MVP

---

#### 8. Mentorship Matching System

**Status**: DESIGN PHASE | NOT IMPLEMENTED ❌

**Planned Capabilities**:

**1. Mentee Application Flow** ❌:

- Mentee submits application (learning goals, career stage, preferences)
- Application stored with PENDING status
- Mentorship Team reviews and approves/rejects
- Approved mentees can browse mentors

**2. Mentor Selection** ❌:

- Option 1: Mentee manually selects multiple mentors with priority order
- Option 2: API suggests matching mentors based on:
    - Skills overlap
    - Focus area alignment
    - Availability
    - Experience level compatibility
    - Mentorship type preference

**3. Application Assignment** ❌:

- System assigns to first priority mentor
- Check mentor availability
- Email mentor to accept/decline
- If declined or unavailable, move to next priority
- If no mentors in list, trigger suggestion API

**4. Matching Confirmation** ❌:

- Mentor accepts/declines via email or platform
- On accept: Create mentorship relationship
- Send cycle emails to both parties
- Track in database

**Required APIs** ❌:

```
POST /api/platform/v1/mentees                          - Create mentee application
PATCH /api/platform/v1/mentees/{id}/approve            - Approve mentee (Team)
PATCH /api/platform/v1/mentees/{id}/reject             - Reject mentee
POST /api/platform/v1/mentees/{id}/apply               - Apply to mentors with priority
GET /api/platform/v1/mentees/{id}/applications         - View application status
GET /api/platform/v1/mentors/suggest?menteeId={id}     - Suggest matching mentors
POST /api/platform/v1/mentorship/matches               - Create match
POST /api/platform/v1/mentorship/cycle-emails          - Send cycle emails
```

**Required Database Tables** ❌:

- `mentee_applications` (mentee_id, mentor_id, priority_order, status, applied_at)
- `mentorship_matches` (mentor_id, mentee_id, cycle_id, start_date, status)

**Required Frontend Views** ❌:

- Mentorship Team: Approve mentors/mentees dashboard
- Mentee: Application form and status dashboard
- Mentor: View/accept applications dashboard
- Admin: View all applications with priority order

**Priority**: CRITICAL - This is the core value proposition of the MVP

---

#### 9. Content Management System (CMS)

**Status**: BACKEND COMPLETE | FRONTEND PARTIAL ✅

**Completed**:

- ✅ Footer content API (`/api/cms/v1/footer`)
- ✅ Page content management (dynamic JSONB storage)
- ✅ Mentorship pages (overview, FAQ, timeline, code of conduct, study groups, resources)
- ✅ Hybrid repository pattern (PostgreSQL + JSON fallback files)
- ✅ Dynamic content delivery

**CMS API Endpoints**:

```
GET /api/cms/v1/footer                            - Footer content
GET /api/cms/v1/pages/{slug}                      - Page content
GET /api/cms/v1/events                            - Events list
GET /api/cms/v1/mentorship/overview               - Mentorship overview
GET /api/cms/v1/mentorship/faq                    - Mentorship FAQ
GET /api/cms/v1/mentorship/mentors                - Mentor directory (with filters)
```

**Remaining Work**:

- 🚧 Frontend CMS integration
- 🚧 Admin interface to edit CMS content
- 🚧 Dynamic page rendering

---

## 🎯 MVP Launch Checklist

### Critical Path Items (Must Complete for MVP)

#### Backend APIs ⚠️

- [x] ✅ Authentication system (login, token validation)
- [ ] ❌ **POST /api/auth/register** - User registration
- [ ] ❌ **POST /api/auth/reset-password** - Password reset
- [x] ✅ Database schema and migrations (base tables)
- [ ] ❌ **Mentee application tables** - Add via Flyway migration
- [ ] ❌ **Mentorship match tables** - Add via Flyway migration
- [x] ✅ CMS API (footer, pages, events, mentorship content)
- [x] ✅ Email service (Gmail SMTP)
- [x] ✅ File storage (Google Drive)
- [ ] ❌ **Mentor approval APIs** - PATCH endpoints for approve/reject
- [ ] ❌ **Mentee approval APIs** - PATCH endpoints for approve/reject
- [ ] ❌ **Mentee application APIs** - POST, GET, apply to mentors
- [ ] ❌ **Mentor matching suggestion API** - GET with scoring algorithm
- [ ] ❌ **Mentorship cycle email APIs** - POST to trigger emails
- [x] ✅ Member profile API (CRUD)
- [x] ✅ Mentor profile API (GET, POST)
- [ ] ❌ **Mentor profile update API** - PUT endpoint
- [ ] 🚧 **Integration testing** (90% complete)
- [ ] 🚧 **Performance optimization**
- [ ] 🚧 **Security audit**

**Estimated completion**: 3-4 weeks

---

#### Frontend - WCC Private (admin-wcc-app) ⚠️

- [x] ✅ User authentication (login)
- [ ] ❌ **User registration page** - Connect to /api/auth/register
- [ ] ❌ **Password reset flow** - Connect to /api/auth/reset-password
- [x] ✅ User management interface
- [x] ✅ Member CRUD interface
- [x] ✅ Basic mentor management
- [ ] ❌ **Mentorship Team Dashboard**:
    - [ ] View pending mentors
    - [ ] Approve/reject mentor profiles
    - [ ] View pending mentees
    - [ ] Approve/reject mentee applications
    - [ ] Send cycle emails
- [ ] ❌ **Mentor Dashboard**:
    - [ ] Update own profile
    - [ ] View mentee applications
    - [ ] Accept/decline mentees
    - [ ] Session management
- [ ] ❌ **Mentee Dashboard**:
    - [ ] View application status
    - [ ] See assigned mentor(s)
    - [ ] Track applications with priority
    - [ ] Browse mentor directory
    - [ ] Apply to mentors with priority order
- [ ] 🚧 **Analytics dashboard**
- [ ] 🚧 **Resource upload UI**

**Estimated completion**: 4-6 weeks

---

#### Frontend - Public Website (wcc-frontend) 🚧

- [ ] 🚧 **Home page**
- [ ] 🚧 **About page**
- [ ] 🚧 **Mentor registration form**
- [ ] 🚧 **Mentee registration form**
- [ ] 🚧 **Mentorship pages** (overview, FAQ, timeline)
- [ ] 🚧 **Mentor directory** (public, with filters)
- [ ] 🚧 **Resource library**
- [ ] 🚧 **Contact page**
- [ ] 🚧 **SEO optimization**

---

#### Infrastructure ⚠️

- [x] ✅ Backend deployed (Fly.io)
- [x] ✅ Frontend admin deployed (Vercel)
- [ ] 🚧 **Public frontend deployment** (Vercel)
- [x] ✅ Database (PostgreSQL on Fly.io)
- [ ] 🚧 **Choose production database provider** (consider migration from Fly.io)
- [ ] 🚧 **Set up monitoring** (application logs, error tracking)
- [x] ✅ Email service (Gmail SMTP configured)
- [x] ✅ SSL certificates (Fly.io, Vercel)
- [ ] 🚧 **Backup strategy**
- [ ] 🚧 **Disaster recovery plan**

---

#### Testing & Quality 🚧

- [ ] 🚧 **End-to-end testing** (Playwright/Cypress)
- [ ] 🚧 **Load testing** (JMeter/k6)
- [ ] 🚧 **Security testing** (OWASP checks)
- [ ] 🚧 **Accessibility audit** (WCAG 2.1 AA)
- [ ] 🚧 **Browser compatibility testing**
- [x] ✅ Unit tests (70%+ coverage enforced)
- [x] ✅ Integration tests (TestContainers)

---

## 📈 Post-MVP Roadmap

### Phase 2: Enhanced Mentorship Features (Q2 2026)

1. **Session Management**
    - Schedule mentorship sessions
    - Track session notes
    - Progress tracking

2. **Feedback System**
    - Mentor reviews (mentee feedback)
    - Mentorship program feedback
    - Community general feedback
    - Admin approval workflow
    - Public display of approved feedback

3. **Enhanced Notifications**
    - Event reminders
    - Session reminders
    - Mentorship updates
    - Custom email templates

---

### Phase 3: Community Features (Q3-Q4 2026)

1. Events and study groups
2. Partnership program
3. Skill tracking and progression
4. Advanced analytics and reporting
5. Community forums

---

## 🛠 Technical Stack Summary

### Backend

```yaml
Framework: Spring Boot 3.3.x
Language: Java 21
Build Tool: Gradle (Kotlin DSL)
Database: PostgreSQL 15 (Fly.io)
Migration: FlywayDB
Security: Spring Security 6 + Token Auth + API Key
Authentication: Argon2 password hashing
Testing: JUnit 5, Mockito, TestContainers
Documentation: OpenAPI 3.0 (Swagger UI)
Email: Spring Mail + Gmail SMTP
File Storage: Google Drive API (pluggable)
```

### Frontend

```yaml
Public Website (wcc-frontend):
  Framework: Next.js 14 (App Router)
  Language: TypeScript
  Package Manager: pnpm 9+
  Styling: CSS Modules / Tailwind CSS
  Testing: Jest, Playwright

WCC Private (admin-wcc-app):
  Framework: Next.js 14
  Language: TypeScript
  UI Library: Material-UI 6
  Authentication: Token Bearer
  Testing: Jest, React Testing Library
  Deployment: Vercel
```

### Infrastructure

```yaml
Current:
  Backend: Fly.io (Spring Boot container)
  Admin Frontend: Vercel
  Public Frontend: Vercel (planned)
  Database: PostgreSQL 15 on Fly.io
  Email: Gmail SMTP
  File Storage: Google Drive API

Note: No API Gateway - Direct Spring Boot REST APIs with OpenAPI
```

---

## 🎓 Learning Opportunities

The WCC platform serves as a learning platform for community members in:

### Backend Development

- Spring Boot layered architecture
- RESTful API design and OpenAPI documentation
- PostgreSQL database design and Flyway migrations
- Security best practices (token auth, password hashing)
- JUnit 5 and TestContainers integration testing
- Custom repository pattern (JdbcTemplate)

### Frontend Development

- Next.js 14 and React patterns
- TypeScript development
- Material-UI component library
- Token-based authentication
- API integration and state management

### DevOps

- Docker containerization
- GitHub Actions CI/CD
- Vercel and Fly.io deployments
- Database migrations and versioning

### Open Source Contribution

- Git workflow and branching strategies
- Code review process
- Issue management
- Technical documentation
- Community collaboration

---

## 📊 Success Metrics

### Platform Metrics

- **User Registration**: Track monthly signups
- **Active Mentors**: Active mentors with availability
- **Active Mentees**: Approved mentees in program
- **Successful Matches**: Mentor-mentee pairings
- **Resource Usage**: Downloads and views

### Operational Metrics

- **API Response Time**: < 200ms average
- **Uptime**: 99.5% target
- **Error Rate**: < 1%
- **Email Delivery Rate**: > 95%

### Community Impact

- **Administrative Time Saved**: 70% reduction target (automation)
- **Volunteer Workload**: Reduce manual mentorship coordination
- **Member Engagement**: Increase participation in mentorship
- **Learning Outcomes**: Portfolio-building opportunities for contributors

---

## 🚀 Getting Started

### For Contributors

#### Backend Development

```bash
# Clone repository
git clone https://github.com/Women-Coding-Community/wcc-backend

# Setup Java 21 with SDKMAN
sdk install java 21.0.2-open

# Start PostgreSQL with Docker
docker compose -f docker/docker-compose.yml up postgres

# Run application
./gradlew bootRun

# Run tests
./gradlew test testIntegration

# Open Swagger UI
open http://localhost:8080/swagger-ui/index.html
```

#### WCC Private Development

```bash
# Navigate to admin app
cd admin-wcc-app

# Install dependencies
npm install

# Create environment file
cp .env.example .env

# Configure environment variables
# NEXT_PUBLIC_API_BASE=http://localhost:8080
# NEXT_PUBLIC_API_KEY=your-api-key

# Run development server
npm run dev

# Open admin app
open http://localhost:3000
```

---

## 📞 Support & Resources

### Documentation

- Backend README: `README.md` in root
- Contributing Guidelines: `CONTRIBUTING.md`
- API Documentation: http://localhost:8080/swagger-ui/index.html
- System Diagrams: `docs/wcc-system-diagrams.md`
- Technical Design: `docs/wcc-technical-design.md`
- Claude Code Guide: `CLAUDE.md`

### Community

- Slack Workspace: Women Coding Community
- GitHub Discussions: Enabled on repositories
- Issue Tracking: GitHub Issues

### Key Contacts

- Project Lead: Adriana
- Technical Coordination: via Slack #deployment channel
- Questions: Create GitHub discussion or issue

---

## 📝 Priority Work for MVP

### Immediate Priorities (Next 2-3 Weeks)

1. **Authentication Completion** ⚠️
    - Implement `/api/auth/register` endpoint
    - Implement `/api/auth/reset-password` endpoint
    - Add frontend registration and password reset pages

2. **Mentee Application System** ⚠️
    - Create Flyway migration for `mentee_applications` table
    - Implement mentee application APIs
    - Create mentee application submission form

3. **Mentor/Mentee Approval Workflow** ⚠️
    - Add approval/rejection APIs for mentors
    - Add approval/rejection APIs for mentees
    - Build Mentorship Team dashboard UI
    - Connect approval actions to email notifications

4. **Mentorship Matching** ⚠️
    - Implement mentor suggestion algorithm API
    - Create mentee priority selection UI
    - Build mentor application acceptance UI
    - Implement automatic assignment logic

### Secondary Priorities (Weeks 4-6)

1. **Frontend public website** (wcc-frontend)
2. **Resource integration UI**
3. **Testing and QA**
4. **Performance optimization**
5. **Documentation updates**

---

**Last Updated**: January 12, 2026
**Document Version**: 2.0
**Status**: Active Development - MVP in Progress
