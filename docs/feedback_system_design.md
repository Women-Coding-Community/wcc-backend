# Feedback/Review System - Database Design Proposal

## Quick Reference: MVP vs Future Releases

| Feature            | MVP (Release 1)                                                                       | Future Releases                                                              |
|--------------------|---------------------------------------------------------------------------------------|------------------------------------------------------------------------------|
| **Feedback Types** | • MENTOR_REVIEW<br>• COMMUNITY_GENERAL<br>• MENTORSHIP_PROGRAM                        | R2: EVENT_FEEDBACK, STUDY_GROUP<br>R4: Custom types                          |
| **Rating System**  | Integer (0-5 stars)                                                                   | R3: Enum-based, multi-dimensional                                            |
| **Relationships**  | reviewer_id, reviewee_id, mentorship_cycle_id                                         | R2: event_id, study_group_id                                                 |
| **Features**       | • Submit feedback<br>• Admin approval<br>• Public/private toggle<br>• Basic analytics | R4: Responses, categories, attachments<br>R5: Advanced analytics, dashboards |
| **Tables**         | feedback_types, feedback                                                              | R4: feedback_responses, feedback_categories<br>R5: Views                     |
| **Complexity**     | Low ⭐                                                                                 | Medium-High ⭐⭐⭐                                                              |
| **Timeline**       | **Implement Now**                                                                     | Post-MVP                                                                     |

## Overview

This document proposes a comprehensive database schema for a feedback and review system with a
phased implementation approach.

**MVP Focus (Release 1):**

- Mentor reviews (mentees reviewing mentors)
- Community general feedback
- Mentorship program feedback
- Simplified rating system (0-5 stars)
- Admin approval workflow

**Future Releases:**

- Event feedback (R2)
- Study group reviews (R2)
- Enhanced rating system (R3)
- Advanced features: responses, categories, attachments (R4)
- Analytics & reporting dashboards (R5)

## Current State Analysis

### Existing FeedbackItem Class (CMS)

```java
public record FeedbackItem(
    @NotBlank String name,
    @NotBlank String feedback,
    MemberType memberType,
    Year year,
    String date,
    String rating,    // ⚠️ Should be enum/structured
    String type)      // ⚠️ Should be enum
```

**Issues with Current Design:**

- Used only for CMS display (testimonials/feedback section)
- No database persistence
- No relationship tracking (who gave feedback to whom)
- No structured rating system
- No type enforcement

## Proposed Solution

### 1. Java Domain Model

#### 1.1 FeedbackType Enum

```java
package com.wcc.platform.domain.platform.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Types of feedback that can be submitted in the platform. */
@Getter
@AllArgsConstructor
public enum FeedbackType {
    MENTOR_REVIEW(1, "Review of a mentor by a mentee"),
    EVENT_FEEDBACK(2, "Feedback about a community event"),
    COMMUNITY_GENERAL(3, "General feedback about the community"),
    MENTORSHIP_PROGRAM(4, "Feedback about the mentorship program"),
    STUDY_GROUP(5, "Feedback about a study group");

    private final int typeId;
    private final String description;

    public static FeedbackType fromId(final int typeId) {
        for (final FeedbackType type : values()) {
            if (type.getTypeId() == typeId) {
                return type;
            }
        }
        return COMMUNITY_GENERAL;
    }
}
```

#### 1.2 FeedbackRating Enum

```java
package com.wcc.platform.domain.platform.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Star rating system for feedback (0-5 stars). */
@Getter
@AllArgsConstructor
public enum FeedbackRating {
    ZERO_STARS(0, "0 stars - Not applicable or very poor"),
    ONE_STAR(1, "1 star - Poor"),
    TWO_STARS(2, "2 stars - Below average"),
    THREE_STARS(3, "3 stars - Average"),
    FOUR_STARS(4, "4 stars - Good"),
    FIVE_STARS(5, "5 stars - Excellent");

    private final int stars;
    private final String description;

    public static FeedbackRating fromStars(final int stars) {
        for (final FeedbackRating rating : values()) {
            if (rating.getStars() == stars) {
                return rating;
            }
        }
        return THREE_STARS;
    }
}
```

#### 1.3 Feedback Domain Class

```java
package com.wcc.platform.domain.platform.feedback;

import com.wcc.platform.domain.platform.type.FeedbackRating;
import com.wcc.platform.domain.platform.type.FeedbackType;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Feedback/Review entity for tracking member feedback on mentors, events, programs, etc. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    private Long id;

    // Who gave the feedback
    private Long reviewerId;
    private String reviewerName;

    // What/who was reviewed (nullable - depends on type)
    private Long revieweeId;           // For MENTOR_REVIEW
    private String revieweeName;       // For display
    private Long eventId;              // For EVENT_FEEDBACK
    private Long mentorshipCycleId;    // For MENTORSHIP_PROGRAM
    private Long studyGroupId;         // For STUDY_GROUP

    // Feedback content
    private FeedbackType feedbackType;
    private FeedbackRating rating;
    private String feedbackText;

    // Metadata
    private Integer year;              // Year the feedback was given
    private String submissionDate;     // Optional: specific date description
    private Boolean isPublic;          // Whether to display publicly
    private Boolean isApproved;        // Admin approval for public display

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
```

### 2. Database Schema

#### 2.1 Lookup Tables

```sql
-- Table for feedback types
CREATE TABLE IF NOT EXISTS feedback_types
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(100) UNIQUE NOT NULL,
    description TEXT
);

-- Seed data for feedback types
INSERT INTO feedback_types (id, name, description)
VALUES (1, 'MENTOR_REVIEW', 'Review of a mentor by a mentee'),
       (2, 'EVENT_FEEDBACK', 'Feedback about a community event'),
       (3, 'COMMUNITY_GENERAL', 'General feedback about the community'),
       (4, 'MENTORSHIP_PROGRAM', 'Feedback about the mentorship program'),
       (5, 'STUDY_GROUP', 'Feedback about a study group');

-- Table for feedback ratings
CREATE TABLE IF NOT EXISTS feedback_ratings
(
    id          SERIAL PRIMARY KEY,
    stars       INTEGER UNIQUE NOT NULL CHECK (stars >= 0 AND stars <= 5),
    description VARCHAR(100)   NOT NULL
);

-- Seed data for ratings
INSERT INTO feedback_ratings (id, stars, description)
VALUES (1, 0, '0 stars - Not applicable or very poor'),
       (2, 1, '1 star - Poor'),
       (3, 2, '2 stars - Below average'),
       (4, 3, '3 stars - Average'),
       (5, 4, '4 stars - Good'),
       (6, 5, '5 stars - Excellent');
```

#### 2.2 Main Feedback Table

```sql
-- Main feedback table with flexible relationships
CREATE TABLE IF NOT EXISTS feedback
(
    id                  BIGSERIAL PRIMARY KEY,

    -- Who gave the feedback (required)
    reviewer_id         INTEGER NOT NULL REFERENCES members (id) ON DELETE CASCADE,

    -- What/who was reviewed (nullable - one of these based on feedback_type)
    reviewee_id         INTEGER REFERENCES members (id) ON DELETE SET NULL,
    event_id            INTEGER REFERENCES events (id) ON DELETE SET NULL,
    mentorship_cycle_id INTEGER REFERENCES mentorship_cycle (id) ON DELETE SET NULL,
    study_group_id      INTEGER,                                          -- TODO: Reference study_groups table when created

    -- Feedback details
    feedback_type_id    INTEGER NOT NULL REFERENCES feedback_types (id),
    rating_id           INTEGER REFERENCES feedback_ratings (id),
    feedback_text       TEXT    NOT NULL,

    -- Metadata
    feedback_year       INTEGER,
    submission_date     VARCHAR(100),                                     -- Optional: "March 2024", "Q1 2024", etc.
    is_public           BOOLEAN                  DEFAULT FALSE,
    is_approved         BOOLEAN                  DEFAULT FALSE,

    created_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- Constraints to ensure proper relationships
    CONSTRAINT feedback_type_relationship_check CHECK (
        (feedback_type_id = 1 AND reviewee_id IS NOT NULL)                -- MENTOR_REVIEW requires reviewee
            OR (feedback_type_id = 2 AND event_id IS NOT NULL)            -- EVENT_FEEDBACK requires event
            OR (feedback_type_id = 3)                                     -- COMMUNITY_GENERAL requires nothing
            OR (feedback_type_id = 4 AND mentorship_cycle_id IS NOT NULL) -- MENTORSHIP_PROGRAM
            OR (feedback_type_id = 5 AND study_group_id IS NOT NULL)      -- STUDY_GROUP
        )
);

-- Indexes for performance
CREATE INDEX idx_feedback_reviewer ON feedback (reviewer_id);
CREATE INDEX idx_feedback_reviewee ON feedback (reviewee_id);
CREATE INDEX idx_feedback_event ON feedback (event_id);
CREATE INDEX idx_feedback_type ON feedback (feedback_type_id);
CREATE INDEX idx_feedback_public ON feedback (is_public, is_approved);
CREATE INDEX idx_feedback_year ON feedback (feedback_year);
```

### 3. Alternative Simpler Approach (Optional)

If you want to keep it simpler initially and avoid complex constraints:

```sql
-- Simplified version - use rating as integer directly
CREATE TABLE IF NOT EXISTS feedback
(
    id               BIGSERIAL PRIMARY KEY,
    reviewer_id      INTEGER NOT NULL REFERENCES members (id) ON DELETE CASCADE,
    reviewee_id      INTEGER REFERENCES members (id) ON DELETE SET NULL,
    feedback_type_id INTEGER NOT NULL REFERENCES feedback_types (id),
    rating           INTEGER CHECK (rating >= 0 AND rating <= 5),
    feedback_text    TEXT    NOT NULL,
    feedback_year    INTEGER,
    is_public        BOOLEAN                  DEFAULT FALSE,
    is_approved      BOOLEAN                  DEFAULT FALSE,
    created_at       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
```

### 4. Use Cases & Queries

#### 4.1 Get all approved public feedback for a mentor

```sql
SELECT f.id,
       f.feedback_text,
       f.rating,
       f.feedback_year,
       reviewer.full_name as reviewer_name,
       reviewee.full_name as mentor_name
FROM feedback f
         JOIN members reviewer ON f.reviewer_id = reviewer.id
         JOIN members reviewee ON f.reviewee_id = reviewee.id
WHERE f.reviewee_id = ?
  AND f.feedback_type_id = 1 -- MENTOR_REVIEW
  AND f.is_public = TRUE
  AND f.is_approved = TRUE
ORDER BY f.created_at DESC;
```

#### 4.2 Get average rating for a mentor

```sql
SELECT AVG(fr.stars) as avg_rating,
       COUNT(*)      as total_reviews
FROM feedback f
         JOIN feedback_ratings fr ON f.rating_id = fr.id
WHERE f.reviewee_id = ?
  AND f.feedback_type_id = 1 -- MENTOR_REVIEW
  AND f.is_approved = TRUE;
```

#### 4.3 Get event feedback summary

```sql
SELECT e.name                            as event_name,
       COUNT(f.id)                       as total_feedback,
       AVG(fr.stars)                     as avg_rating,
       STRING_AGG(f.feedback_text, '; ') as feedback_summary
FROM feedback f
         JOIN events e ON f.event_id = e.id
         JOIN feedback_ratings fr ON f.rating_id = fr.id
WHERE f.event_id = ?
  AND f.feedback_type_id = 2 -- EVENT_FEEDBACK
GROUP BY e.name;
```

### 5. Migration File Structure

**File:** `V16__20260112__create_feedback_system.sql`

```sql
-- See section 2.1 and 2.2 above for complete SQL
```

### 6. Repository Implementation

```java
public interface FeedbackRepository extends CrudRepository<Feedback> {

    List<Feedback> findByRevieweeId(Long revieweeId);

    List<Feedback> findByReviewerId(Long reviewerId);

    List<Feedback> findByEventId(Long eventId);

    List<Feedback> findPublicApproved();

    List<Feedback> findByFeedbackType(FeedbackType type);

    Double getAverageRatingForMentor(Long mentorId);
}
```

## Comparison with Current FeedbackItem

| Aspect            | Current (CMS)        | Proposed (Platform)   |
|-------------------|----------------------|-----------------------|
| Purpose           | Display testimonials | Track all feedback    |
| Storage           | JSON in page table   | Dedicated tables      |
| Relationships     | None                 | Full relational       |
| Rating            | String               | Enum (0-5 stars)      |
| Type              | String               | Enum with validation  |
| Reviewer tracking | Name only            | Full member reference |
| Reviewee tracking | None                 | Full reference        |
| Approval flow     | None                 | Admin approval        |
| Queries           | N/A                  | Rich analytics        |

## Migration Path

1. **Phase 1:** Create new tables (feedback_types, feedback_ratings, feedback)
2. **Phase 2:** Implement repository and service layers
3. **Phase 3:** Create REST endpoints for CRUD operations
4. **Phase 4:** Add admin approval workflow

## Implementation Strategy

### MVP (Minimum Viable Product) - Release 1

**Scope:**

- **Feedback Types:** MENTOR_REVIEW, COMMUNITY_GENERAL, MENTORSHIP_PROGRAM (3 types)
- **Schema:** Simplified with rating as integer (0-5)
- **Relationships:** Basic reviewer/reviewee tracking
- **Features:** Admin approval, public/private toggle

**Why These Three Types:**

1. **MENTOR_REVIEW:** Core feature - allows mentees to review their mentors
2. **COMMUNITY_GENERAL:** Captures overall community satisfaction
3. **MENTORSHIP_PROGRAM:** Tracks program effectiveness and improvements needed

**MVP Database Schema:**

```sql
-- Lookup table for feedback types (MVP: 3 types)
CREATE TABLE IF NOT EXISTS feedback_types
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(100) UNIQUE NOT NULL,
    description TEXT
);

-- MVP seed data (3 types only)
INSERT INTO feedback_types (id, name, description)
VALUES (1, 'MENTOR_REVIEW', 'Review of a mentor by a mentee'),
       (2, 'COMMUNITY_GENERAL', 'General feedback about the community'),
       (3, 'MENTORSHIP_PROGRAM', 'Feedback about the mentorship program');

-- Main feedback table (simplified for MVP)
CREATE TABLE IF NOT EXISTS feedback
(
    id                  BIGSERIAL PRIMARY KEY,

    -- Who gave the feedback (required)
    reviewer_id         INTEGER NOT NULL REFERENCES members (id) ON DELETE CASCADE,

    -- Who/what was reviewed (nullable - depends on type)
    reviewee_id         INTEGER REFERENCES members (id) ON DELETE SET NULL,          -- For MENTOR_REVIEW
    mentorship_cycle_id INTEGER REFERENCES mentorship_cycle (id) ON DELETE SET NULL, -- For MENTORSHIP_PROGRAM

    -- Feedback details
    feedback_type_id    INTEGER NOT NULL REFERENCES feedback_types (id),
    rating              INTEGER CHECK (rating >= 0 AND rating <= 5),
    feedback_text       TEXT    NOT NULL,

    -- Metadata
    feedback_year       INTEGER,
    submission_date     VARCHAR(100),                                                -- Optional: "March 2024", "Q1 2024", etc.
    is_public           BOOLEAN                  DEFAULT FALSE,
    is_approved         BOOLEAN                  DEFAULT FALSE,

    created_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- Constraint: MENTOR_REVIEW requires reviewee_id
    CONSTRAINT feedback_mentor_review_check CHECK (
        (feedback_type_id = 1 AND reviewee_id IS NOT NULL)                           -- MENTOR_REVIEW
            OR feedback_type_id IN (2, 3)                                            -- COMMUNITY_GENERAL or MENTORSHIP_PROGRAM
        )
);

-- Indexes for performance
CREATE INDEX idx_feedback_reviewer ON feedback (reviewer_id);
CREATE INDEX idx_feedback_reviewee ON feedback (reviewee_id);
CREATE INDEX idx_feedback_type ON feedback (feedback_type_id);
CREATE INDEX idx_feedback_public_approved ON feedback (is_public, is_approved);
CREATE INDEX idx_feedback_year ON feedback (feedback_year);
CREATE INDEX idx_feedback_cycle ON feedback (mentorship_cycle_id);
```

**MVP Java Domain:**

```java
// Simplified enum for MVP
@Getter
@AllArgsConstructor
public enum FeedbackType {
    MENTOR_REVIEW(1, "Review of a mentor by a mentee"),
    COMMUNITY_GENERAL(2, "General feedback about the community"),
    MENTORSHIP_PROGRAM(3, "Feedback about the mentorship program");

    private final int typeId;
    private final String description;
}

// Simplified Feedback domain class
@Data
@Builder
public class Feedback {

    private Long id;
    private Long reviewerId;
    private Long revieweeId;              // For MENTOR_REVIEW
    private Long mentorshipCycleId;       // For MENTORSHIP_PROGRAM
    private FeedbackType feedbackType;
    private Integer rating;               // 0-5 stars (nullable)
    private String feedbackText;
    private Integer year;
    private String submissionDate;
    private Boolean isPublic;
    private Boolean isApproved;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
```

**MVP REST Endpoints:**

```
POST   /api/platform/v1/feedback           - Create feedback
GET    /api/platform/v1/feedback/{id}      - Get feedback by ID
GET    /api/platform/v1/feedback/mentor/{mentorId}  - Get mentor reviews
GET    /api/platform/v1/feedback/public    - Get approved public feedback
PATCH  /api/platform/v1/feedback/{id}/approve  - Admin: Approve feedback
DELETE /api/platform/v1/feedback/{id}      - Delete own feedback
```

**MVP Features:**

- ✅ Submit feedback for mentors, community, or mentorship program
- ✅ Optional 0-5 star rating
- ✅ Admin approval workflow
- ✅ Public/private visibility control
- ✅ View mentor's average rating and reviews
- ✅ Basic analytics (average ratings, counts)

---

### Future Releases - Post-MVP

#### Release 2: Events & Study Groups

**New Types:**

- EVENT_FEEDBACK (4) - Feedback about community events
- STUDY_GROUP (5) - Feedback about study groups

**Schema Changes:**

```sql
-- Add new feedback types
INSERT INTO feedback_types (id, name, description)
VALUES (4, 'EVENT_FEEDBACK', 'Feedback about a community event'),
       (5, 'STUDY_GROUP', 'Feedback about a study group');

-- Add new relationship columns
ALTER TABLE feedback
    ADD COLUMN event_id INTEGER REFERENCES events (id) ON DELETE SET NULL;
ALTER TABLE feedback
    ADD COLUMN study_group_id INTEGER;
-- Add study_groups table first

-- Update constraint
ALTER TABLE feedback
    DROP CONSTRAINT feedback_mentor_review_check;
ALTER TABLE feedback
    ADD CONSTRAINT feedback_type_relationship_check CHECK (
        (feedback_type_id = 1 AND reviewee_id IS NOT NULL) -- MENTOR_REVIEW
            OR (feedback_type_id = 2) -- COMMUNITY_GENERAL
            OR (feedback_type_id = 3 AND mentorship_cycle_id IS NOT NULL) -- MENTORSHIP_PROGRAM
            OR (feedback_type_id = 4 AND event_id IS NOT NULL) -- EVENT_FEEDBACK
            OR (feedback_type_id = 5 AND study_group_id IS NOT NULL) -- STUDY_GROUP
        );

CREATE INDEX idx_feedback_event ON feedback (event_id);
CREATE INDEX idx_feedback_study_group ON feedback (study_group_id);
```

**New Features:**

- ✅ Event feedback collection
- ✅ Study group reviews
- ✅ Event analytics dashboard
- ✅ Study group effectiveness metrics

#### Release 3: Enhanced Rating System

**Improvements:**

- Replace `rating` integer with `rating_id` foreign key
- Create `feedback_ratings` lookup table
- Add rating descriptions and metadata
- Enable multi-dimensional ratings (e.g., communication, knowledge, helpfulness)

**Schema Changes:**

```sql
CREATE TABLE IF NOT EXISTS feedback_ratings
(
    id          SERIAL PRIMARY KEY,
    stars       INTEGER UNIQUE NOT NULL CHECK (stars >= 0 AND stars <= 5),
    description VARCHAR(100)   NOT NULL
);

-- Migrate existing ratings
ALTER TABLE feedback
    ADD COLUMN rating_id INTEGER REFERENCES feedback_ratings (id);
UPDATE feedback
SET rating_id = (SELECT id FROM feedback_ratings WHERE stars = feedback.rating);
ALTER TABLE feedback
    DROP COLUMN rating;
```

#### Release 4: Advanced Features

**Enhancements:**

- **Mentor Responses:** Allow mentors to respond to reviews
- **Feedback Categories:** Tag feedback (communication, technical skills, availability)
- **Rich Text:** Support markdown in feedback_text
- **Attachments:** Link to resources/screenshots
- **Anonymous Feedback:** Optional anonymous submission
- **Feedback Templates:** Pre-defined questions for structured feedback
- **Sentiment Analysis:** Auto-categorize feedback sentiment
- **Trending Topics:** Extract common themes from feedback

**New Tables:**

```sql
-- Feedback responses (mentors responding to reviews)
CREATE TABLE feedback_responses
(
    id            BIGSERIAL PRIMARY KEY,
    feedback_id   BIGINT  NOT NULL REFERENCES feedback (id) ON DELETE CASCADE,
    responder_id  INTEGER NOT NULL REFERENCES members (id),
    response_text TEXT    NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Feedback categories/tags
CREATE TABLE feedback_categories
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(100) UNIQUE NOT NULL,
    description TEXT
);

CREATE TABLE feedback_category_mapping
(
    feedback_id BIGINT REFERENCES feedback (id) ON DELETE CASCADE,
    category_id INTEGER REFERENCES feedback_categories (id),
    PRIMARY KEY (feedback_id, category_id)
);
```

#### Release 5: Analytics & Reporting

**Features:**

- **Mentor Dashboard:** Personal feedback analytics
- **Admin Dashboard:** Platform-wide insights
- **Trend Reports:** Feedback over time
- **Export:** CSV/PDF reports
- **Notifications:** Email alerts for new feedback
- **Leaderboards:** Top-rated mentors (opt-in)

**Database Views:**

```sql
-- View: Mentor performance summary
CREATE VIEW mentor_feedback_summary AS
SELECT m.id                                      as mentor_id,
       m.full_name                               as mentor_name,
       COUNT(f.id)                               as total_reviews,
       AVG(f.rating)                             as avg_rating,
       COUNT(CASE WHEN f.rating >= 4 THEN 1 END) as positive_reviews,
       COUNT(CASE WHEN f.rating <= 2 THEN 1 END) as negative_reviews,
       MAX(f.created_at)                         as last_review_date
FROM members m
         LEFT JOIN feedback f ON m.id = f.reviewee_id
WHERE f.feedback_type_id = 1
  AND f.is_approved = true
GROUP BY m.id, m.full_name;
```

---

### Migration Roadmap

| Release      | Version | Focus             | Tables Added                            | Complexity |
|--------------|---------|-------------------|-----------------------------------------|------------|
| **MVP (R1)** | V16     | Core feedback     | feedback_types, feedback                | Low        |
| R2           | V17     | Events & Groups   | event_id, study_group_id columns        | Low        |
| R3           | V18     | Rating System     | feedback_ratings                        | Medium     |
| R4           | V19     | Advanced Features | feedback_responses, feedback_categories | High       |
| R5           | V20     | Analytics         | Views, materialized views               | Medium     |

### Additional Considerations

#### Privacy & Compliance:

1. **GDPR:** Allow reviewers to delete/edit their feedback
2. **Anonymization:** Option to hide reviewer identity
3. **Data Retention:** Auto-archive old feedback (e.g., > 3 years)

#### Moderation:

1. **Approval Queue:** Admin dashboard for pending feedback
2. **Flagging:** Report inappropriate feedback
3. **Edit History:** Track changes to approved feedback

#### Quality:

1. **Min Length:** Require minimum feedback text length (e.g., 50 chars)
2. **Spam Detection:** Rate limiting, duplicate detection
3. **Guidelines:** Display feedback guidelines before submission

## MVP Implementation Checklist

### Phase 1: Database Schema ✅

- [ ] Create migration file `V16__20260112__create_feedback_system.sql`
- [ ] Add `feedback_types` table with 3 types
- [ ] Add `feedback` table with simplified schema
- [ ] Add indexes for performance
- [ ] Test migration locally

### Phase 2: Java Domain Layer

- [ ] Create `FeedbackType` enum in `domain.platform.type`
- [ ] Create `Feedback` domain class in `domain.platform.feedback`
- [ ] Add validation annotations
- [ ] Create unit tests for domain classes

### Phase 3: Repository Layer

- [ ] Create `FeedbackRepository` interface in `repository.spi`
- [ ] Implement `PostgresFeedbackRepository`
- [ ] Implement CRUD operations:
    - `create(Feedback)` - Submit new feedback
    - `findById(Long)` - Get feedback by ID
    - `findByRevieweeId(Long)` - Get mentor reviews
    - `findPublicApproved()` - Get approved public feedback
    - `updateApprovalStatus(Long, Boolean)` - Approve/reject feedback
    - `deleteById(Long)` - Delete feedback
    - `getAverageRating(Long)` - Calculate mentor average rating
- [ ] Write integration tests

### Phase 4: Service Layer

- [ ] Create `FeedbackService` with business logic
- [ ] Implement validation rules:
    - MENTOR_REVIEW requires reviewee_id
    - Rating must be 0-5 if provided
    - Feedback text minimum length (50 chars)
- [ ] Add authorization checks (users can only delete their own feedback)
- [ ] Create unit tests with Mockito

### Phase 5: REST API

- [ ] Create `FeedbackController` in `controller`
- [ ] Implement endpoints:
    - `POST /api/platform/v1/feedback` - Submit feedback
    - `GET /api/platform/v1/feedback/{id}` - Get by ID
    - `GET /api/platform/v1/feedback/mentor/{mentorId}` - Mentor reviews
    - `GET /api/platform/v1/feedback/public` - Public approved feedback
    - `PATCH /api/platform/v1/feedback/{id}/approve` - Admin approve
    - `DELETE /api/platform/v1/feedback/{id}` - Delete own feedback
- [ ] Add OpenAPI/Swagger documentation
- [ ] Add API key authentication
- [ ] Write controller tests

### Phase 6: Testing & Documentation

- [ ] Write integration tests for full flow
- [ ] Test admin approval workflow
- [ ] Test authorization rules
- [ ] Update API documentation
- [ ] Create Postman collection
- [ ] Add example requests/responses to docs

## Files to Create/Modify

### New Files:

```
src/main/resources/db/migration/
  └── V16__20260112__create_feedback_system.sql

src/main/java/com/wcc/platform/domain/platform/
  ├── type/FeedbackType.java
  └── feedback/Feedback.java

src/main/java/com/wcc/platform/repository/
  ├── spi/FeedbackRepository.java
  └── postgres/PostgresFeedbackRepository.java

src/main/java/com/wcc/platform/service/
  └── FeedbackService.java

src/main/java/com/wcc/platform/controller/
  └── FeedbackController.java

src/test/java/com/wcc/platform/
  ├── domain/platform/feedback/FeedbackTest.java
  ├── service/FeedbackServiceTest.java
  └── controller/FeedbackControllerTest.java

src/testInt/java/com/wcc/platform/
  ├── repository/postgres/PostgresFeedbackRepositoryIntegrationTest.java
  └── service/FeedbackServiceIntegrationTest.java
```

## Estimated Effort

| Phase               | Estimated Time  | Complexity |
|---------------------|-----------------|------------|
| Phase 1: Database   | 1-2 hours       | Low        |
| Phase 2: Domain     | 1 hour          | Low        |
| Phase 3: Repository | 3-4 hours       | Medium     |
| Phase 4: Service    | 2-3 hours       | Medium     |
| Phase 5: REST API   | 2-3 hours       | Low-Medium |
| Phase 6: Testing    | 3-4 hours       | Medium     |
| **Total MVP**       | **12-17 hours** | **Medium** |

## Success Criteria (MVP)

- ✅ Mentees can submit reviews for their mentors
- ✅ Members can submit general community feedback
- ✅ Members can submit feedback about mentorship programs
- ✅ Feedback includes optional 0-5 star rating
- ✅ Admins can approve/reject feedback for public display
- ✅ Public API endpoint shows approved feedback
- ✅ Mentors can view their average rating and reviews
- ✅ All tests passing with >70% coverage
- ✅ API documented in Swagger
- ✅ Migration runs successfully on test environment

## Next Steps

1. ✅ Review and approve this design document
2. ⏭️ **START HERE:** Create migration file `V16__20260112__create_feedback_system.sql`
3. ⏭️ Implement Java domain classes (FeedbackType enum, Feedback class)
4. ⏭️ Create repository layer
5. ⏭️ Implement service layer
6. ⏭️ Add REST API endpoints
7. ⏭️ Write comprehensive tests
8. ⏭️ Deploy to test environment
