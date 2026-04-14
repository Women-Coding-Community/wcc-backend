# API Flow Test Plan — Playwright Integration Tests

## Flow 1: Authentication
**File:** `tests/auth/auth.flow.test.ts`

| ID | Title | Endpoint | Expected Result |
|----|-------|----------|----------------|
| AUTH-01 | Login with valid credentials returns token | `POST /api/auth/login` | 200, body contains `token`, `expiresAt`, `roles` |
| AUTH-02 | Login with empty email and password returns 400 | `POST /api/auth/login` | 400 |
| AUTH-03 | Login with wrong password returns 401 | `POST /api/auth/login` | 401 |
| AUTH-04 | Get current user with valid token returns user info | `GET /api/auth/me` | 200, body contains `roles`, `member` |
| AUTH-05 | Get current user without token returns 401 | `GET /api/auth/me` | 401 |
| AUTH-06 | Get current user with invalid token returns 401 | `GET /api/auth/me` | 401 |
| AUTH-07 | Get users with admin token returns user list | `GET /api/auth/users` | 200, non-empty list |

---

## Flow 2: Mentor — Register and Accept
**File:** `tests/platform/mentor.register.accept.flow.test.ts`

| ID | Title | Endpoint | Expected Result |
|----|-------|----------|----------------|
| MENTOR-A01 | Register mentor creates record with PENDING status | `POST /api/platform/v1/mentors` | 201, `profileStatus = PENDING` |
| MENTOR-A02 | Registered mentor appears in platform list | `GET /api/platform/v1/mentors` | 200, mentor present |
| MENTOR-A03 | Approve mentor changes status to ACTIVE | `PATCH /api/platform/v1/mentors/{id}/accept` | 200, `profileStatus = ACTIVE` |
| MENTOR-A04 | Active mentor appears in public CMS list | `GET /api/cms/v1/mentorship/mentors` | 200, mentor present |
| MENTOR-A05 | Approve already-active mentor returns 409 | `PATCH /api/platform/v1/mentors/{id}/accept` | 409 |

---

## Flow 3: Mentor — Register and Reject
**File:** `tests/platform/mentor.register.reject.flow.test.ts`

| ID | Title | Endpoint | Expected Result |
|----|-------|----------|----------------|
| MENTOR-R01 | Register mentor creates record with PENDING status | `POST /api/platform/v1/mentors` | 201, `profileStatus = PENDING` |
| MENTOR-R02 | Reject mentor changes status to REJECTED | `PATCH /api/platform/v1/mentors/{id}/reject` | 200, `profileStatus = REJECTED` |
| MENTOR-R03 | Rejected mentor does not appear in public CMS list | `GET /api/cms/v1/mentorship/mentors` | 200, mentor absent |
| MENTOR-R04 | Reject already-rejected mentor returns 409 | `PATCH /api/platform/v1/mentors/{id}/reject` | 409 |

---

## Flow 4: Mentor — Re-registration Preserves Fields
**File:** `tests/platform/mentor.reregistration.flow.test.ts`

| ID | Title | Endpoint | Expected Result |
|----|-------|----------|----------------|
| MENTOR-P01 | Re-register with same email preserves member-level fields and applies changed fields | `POST /api/platform/v1/mentors` | 201, `isWomen`, `pronouns`, `pronounCategory`, `calendlyLink`, `acceptMale`, `acceptPromotion` match original registration; updated fields (e.g. `bio`, `city`) reflect new values |

---

## Flow 5: Mentor — Update
**File:** `tests/platform/mentor.update.flow.test.ts`

| ID | Title | Endpoint | Expected Result |
|----|-------|----------|----------------|
| MENTOR-U01 | Register mentor creates record | `POST /api/platform/v1/mentors` | 201 |
| MENTOR-U02 | Update mentor data persists changes | `PUT /api/platform/v1/mentors/{id}` | 200, updated fields returned |

---

## Flow 6: Mentor — Validation and Error Cases
**File:** `tests/platform/mentor.validation.flow.test.ts`

| ID | Title | Endpoint | Expected Result |
|----|-------|----------|----------------|
| MENTOR-E01 | Register mentor with missing required field returns 400 | `POST /api/platform/v1/mentors` | 400 |
| MENTOR-E02 | Register mentor with invalid pronounCategory casing returns 400 | `POST /api/platform/v1/mentors` | 400 |
| MENTOR-E03 | Register mentor without API key returns 401 | `POST /api/platform/v1/mentors` | 401 |

---

## Flow 7: Mentee — Register and Accept
**File:** `tests/platform/mentee.register.accept.flow.test.ts`

| ID | Title | Endpoint | Expected Result |
|----|-------|----------|----------------|
| MENTEE-A01 | Register new mentee returns 201 with PENDING status | `POST /api/platform/v1/mentees` | 201, `profileStatus = PENDING` |
| MENTEE-A02 | Registered mentee appears in list | `GET /api/platform/v1/mentees` | 200, mentee present |
| MENTEE-A03 | Accept mentee changes status to ACTIVE | `PATCH /api/platform/v1/mentees/{menteeId}/accept` | 200, `profileStatus = ACTIVE` |
| MENTEE-A04 | Accepted mentee is listed to mentor waiting for approval | `GET /api/platform/v1/mentees` | 200, mentee present with `profileStatus = ACTIVE` |
| MENTEE-A05 | Accept already-active mentee returns 409 | `PATCH /api/platform/v1/mentees/{menteeId}/accept` | 409 |
| MENTEE-A06 | Accept mentee without API key returns 401 | `PATCH /api/platform/v1/mentees/{menteeId}/accept` | 401 |

---

## Flow 8: Mentee — Register and Reject
**File:** `tests/platform/mentee.register.reject.flow.test.ts`

| ID | Title | Endpoint | Expected Result |
|----|-------|----------|----------------|
| MENTEE-R01 | Register new mentee returns 201 with PENDING status | `POST /api/platform/v1/mentees` | 201, `profileStatus = PENDING` |
| MENTEE-R02 | Reject mentee changes status to REJECTED | `PATCH /api/platform/v1/mentees/{menteeId}/reject` | 200, `profileStatus = REJECTED` |
| MENTEE-R03 | Rejection feedback is stored on reject | `PATCH /api/platform/v1/mentees/{menteeId}/reject` | 200, rejection reason persisted |
| MENTEE-R04 | Reject already-rejected mentee returns 409 | `PATCH /api/platform/v1/mentees/{menteeId}/reject` | 409 |
| MENTEE-R05 | Reject mentee without API key returns 401 | `PATCH /api/platform/v1/mentees/{menteeId}/reject` | 401 |

---

## Flow 9: Mentee — Validation and Error Cases
**File:** `tests/platform/mentee.validation.flow.test.ts`

| ID | Title | Endpoint | Expected Result |
|----|-------|----------|----------------|
| MENTEE-E01 | Register mentee with missing required field returns 400 | `POST /api/platform/v1/mentees` | 400 |
| MENTEE-E02 | Register mentee without API key returns 401 | `POST /api/platform/v1/mentees` | 401 |
| MENTEE-E03 | Register mentee with existing member email reuses member | `POST /api/platform/v1/mentees` | 201, linked to existing member id |
| MENTEE-E04 | Get all mentees returns 200 with valid schema | `GET /api/platform/v1/mentees` | 200, schema valid |
| MENTEE-E05 | Reject mentee without reason returns 400 | `PATCH /api/platform/v1/mentees/{menteeId}/reject` | 400 |

---

## Flow 10: Mentee — Registration Guards
**File:** `tests/platform/mentee.registration.guards.flow.test.ts`
> Covers: [#544](https://github.com/Women-Coding-Community/wcc-backend/issues/544)

| ID | Title | Endpoint | Expected Result |
|----|-------|----------|----------------|
| MENTEE-RG01 | Register mentee with PENDING profile status returns error | `POST /api/platform/v1/mentees` | 400, "membership not yet approved" |
| MENTEE-RG02 | Register mentee with REJECTED profile status returns error | `POST /api/platform/v1/mentees` | 400, "membership application rejected" |
| MENTEE-RG03 | Register mentee with BANNED profile status returns error | `POST /api/platform/v1/mentees` | 400, "permanently banned from the community" |
| MENTEE-RG04 | Register mentee with DISABLED profile status returns error | `POST /api/platform/v1/mentees` | 400, "account temporarily disabled" |
| MENTEE-RG05 | Register mentee with ACTIVE profile status succeeds | `POST /api/platform/v1/mentees` | 201 |

---

## Flow 11: Mentee — Application Pairing Rejection and Forwarding
**File:** `tests/platform/mentee.pairing.forwarding.flow.test.ts`
> Covers: [#545](https://github.com/Women-Coding-Community/wcc-backend/issues/545), [#546](https://github.com/Women-Coding-Community/wcc-backend/issues/546)

| ID | Title | Endpoint | Expected Result |
|----|-------|----------|----------------|
| MENTEE-PF01 | Admin rejects pairing changes application status to REJECTED | `PATCH /api/platform/v1/mentees/applications/{id}/reject-pairing` | 200, `status = REJECTED` |
| MENTEE-PF02 | Rejected pairing forwards application to next priority mentor | `PATCH /api/platform/v1/mentees/applications/{id}/reject-pairing` | 200, next priority mentor notified |
| MENTEE-PF03 | Rejected pairing with no remaining priorities transitions to PENDING_MANUAL_MATCH | `PATCH /api/platform/v1/mentees/applications/{id}/reject-pairing` | 200, `status = PENDING_MANUAL_MATCH` |
| MENTEE-PF04 | Mark mentor unavailable changes status to MENTOR_UNAVAILABLE | `PATCH /api/platform/v1/mentors/applications/{id}/mark-unavailable` | 200, `status = MENTOR_UNAVAILABLE` |
| MENTEE-PF05 | Marking mentor unavailable forwards application to next priority | `PATCH /api/platform/v1/mentors/applications/{id}/mark-unavailable` | 200, next priority mentor notified |
| MENTEE-PF06 | Admin forward application changes status to ADMIN_FORWARDED | `PATCH /api/platform/v1/mentees/applications/{id}/forward` | 200, `status = ADMIN_FORWARDED` |
| MENTEE-PF07 | Reject pairing without reason returns 400 | `PATCH /api/platform/v1/mentees/applications/{id}/reject-pairing` | 400 |
| MENTEE-PF08 | Mark mentor unavailable without reason returns 400 | `PATCH /api/platform/v1/mentors/applications/{id}/mark-unavailable` | 400 |

---

## Flow 12: Mentee — Admin Management Actions
**File:** `tests/platform/mentee.admin.management.flow.test.ts`
> Covers: [#547](https://github.com/Women-Coding-Community/wcc-backend/issues/547)

| ID | Title | Endpoint | Expected Result |
|----|-------|----------|----------------|
| MENTEE-AM01 | Exclude mentee from cycle terminates all open applications with CYCLE_EXCLUDED | `POST /api/platform/v1/mentees/{id}/cycles/{cycleId}/exclude` | 200, all open apps `status = CYCLE_EXCLUDED` |
| MENTEE-AM02 | Mark mentee not eligible closes applications with NOT_ELIGIBLE | `POST /api/platform/v1/mentees/{id}/cycles/{cycleId}/not-eligible` | 200, all open apps `status = NOT_ELIGIBLE` |
| MENTEE-AM03 | Ban mentee sets profileStatus to BANNED and closes all applications | `POST /api/platform/v1/mentees/{id}/ban` | 200, `profileStatus = BANNED`, all open apps closed |
| MENTEE-AM04 | Banned mentee cannot register for mentorship | `POST /api/platform/v1/mentees` | 400, "permanently banned from the community" |
| MENTEE-AM05 | Exclude without reason returns 400 | `POST /api/platform/v1/mentees/{id}/cycles/{cycleId}/exclude` | 400 |
| MENTEE-AM06 | Ban without reason returns 400 | `POST /api/platform/v1/mentees/{id}/ban` | 400 |
| MENTEE-AM07 | Admin management endpoints without API key return 401 | `POST /api/platform/v1/mentees/{id}/ban` | 401 |

---

## Flow 13: Mentee — Manual Matching Workflow
**File:** `tests/platform/mentee.manual.matching.flow.test.ts`
> Covers: [#548](https://github.com/Women-Coding-Community/wcc-backend/issues/548), [#549](https://github.com/Women-Coding-Community/wcc-backend/issues/549)

| ID | Title | Endpoint | Expected Result |
|----|-------|----------|----------------|
| MENTEE-MM01 | All 5 priority applications exhausted transitions mentee to PENDING_MANUAL_MATCH | `PATCH /api/platform/v1/mentors/applications/{id}/decline` (×5) | final status = `PENDING_MANUAL_MATCH` |
| MENTEE-MM02 | Get pending manual match mentees returns list for cycle | `GET /api/platform/v1/mentees/pending-manual-match?cycleId={id}` | 200, list contains mentee |
| MENTEE-MM03 | Assign mentor manually creates new PENDING application | `POST /api/platform/v1/mentees/{id}/cycles/{cycleId}/assign-mentor` | 201, new application `status = PENDING` |
| MENTEE-MM04 | Confirm no match changes status to NO_MATCH_FOUND | `POST /api/platform/v1/mentees/{id}/cycles/{cycleId}/no-match` | 200, `status = NO_MATCH_FOUND` |
| MENTEE-MM05 | NO_MATCH_FOUND is terminal — assign mentor after confirmation returns 409 | `POST /api/platform/v1/mentees/{id}/cycles/{cycleId}/assign-mentor` | 409 |
| MENTEE-MM06 | Get pending manual match without API key returns 401 | `GET /api/platform/v1/mentees/pending-manual-match` | 401 |

---

## Flow 14: Mentorship Application Workflow
**File:** `tests/platform/mentorship.application.flow.test.ts`

| ID | Title | Endpoint | Expected Result |
|----|-------|----------|----------------|
| APPL-01 | Get mentor applications with valid token returns list | `GET /api/platform/v1/mentors/{mentorId}/applications` | 200, applications list |
| APPL-02 | Get mentor applications without token returns 403 | `GET /api/platform/v1/mentors/{mentorId}/applications` | 403 |
| APPL-03 | Mentor accepts application changes status to ACCEPTED | `PATCH /api/platform/v1/mentors/applications/{id}/accept` | 200, `status = ACCEPTED` |
| APPL-04 | Mentor declines application changes status to DECLINED | `PATCH /api/platform/v1/mentors/applications/{id}/decline` | 200, `status = DECLINED` |
| APPL-05 | Mentee withdraws application changes status to WITHDRAWN | `PATCH /api/platform/v1/mentees/applications/{id}/withdraw` | 200, `status = WITHDRAWN` |
| APPL-06 | Get mentee applications for cycle returns list | `GET /api/platform/v1/mentees/{menteeId}/applications` | 200, list for cycle |
| APPL-07 | Filter mentor applications by status=pending returns only pending | `GET /api/platform/v1/mentors/{mentorId}/applications?status=pending` | 200, all items `status = PENDING` |
| APPL-08 | Get all applications by status returns filtered list | `GET /api/platform/v1/applications?status=pending` | 200, all items `status = PENDING` |
| APPL-09 | Accept application with missing mentorResponse returns 400 | `PATCH /api/platform/v1/mentors/applications/{id}/accept` | 400 |
| APPL-10 | Withdraw application without reason returns 400 | `PATCH /api/platform/v1/mentees/applications/{id}/withdraw` | 400 |

---

## Flow 15: Mentorship Match Admin
**File:** `tests/platform/mentorship.match.flow.test.ts`

| ID | Title | Endpoint | Expected Result |
|----|-------|----------|----------------|
| MATCH-01 | Confirm match from application creates match | `POST /api/platform/v1/admin/mentorship/matches/confirm/{applicationId}` | 201, match created |
| MATCH-02 | Confirmed match appears in matches list for cycle | `GET /api/platform/v1/admin/mentorship/matches` | 200, match present |
| MATCH-03 | Increment session count increases counter by 1 | `PATCH /api/platform/v1/admin/mentorship/matches/{id}/increment-session` | 200, `sessionCount` incremented |
| MATCH-04 | Complete match changes status to COMPLETED | `PATCH /api/platform/v1/admin/mentorship/matches/{id}/complete` | 200, `status = COMPLETED` |
| MATCH-05 | Cancel match changes status to CANCELLED | `PATCH /api/platform/v1/admin/mentorship/matches/{id}/cancel` | 200, `status = CANCELLED` |
| MATCH-06 | Cancel match with missing reason returns 400 | `PATCH /api/platform/v1/admin/mentorship/matches/{id}/cancel` | 400 |
| MATCH-07 | Get matches without API key returns 401 | `GET /api/platform/v1/admin/mentorship/matches` | 401 |

---

## Flow 16: Mentorship Cycle Management
**File:** `tests/platform/mentorship.cycle.flow.test.ts`

| ID | Title | Endpoint | Expected Result |
|----|-------|----------|----------------|
| CYCLE-01 | Get current open cycle returns cycle or 404 | `GET /api/platform/v1/admin/mentorship/cycles/current` | 200 or 404 |
| CYCLE-02 | Get all cycles returns list | `GET /api/platform/v1/admin/mentorship/cycles/all` | 200, array |
| CYCLE-03 | Get cycle by valid ID returns correct cycle | `GET /api/platform/v1/admin/mentorship/cycles/{id}` | 200, correct cycle |
| CYCLE-04 | Get cycle by non-existent ID returns 404 | `GET /api/platform/v1/admin/mentorship/cycles/{id}` | 404 |
| CYCLE-05 | Get cycles filtered by status returns only matching | `GET /api/platform/v1/admin/mentorship/cycles?status=OPEN` | 200, all items `status = OPEN` |
| CYCLE-06 | Get cycles without API key returns 401 | `GET /api/platform/v1/admin/mentorship/cycles/all` | 401 |

---

## Flow 17: Member Management
**File:** `tests/platform/member.management.flow.test.ts`

| ID | Title | Endpoint | Expected Result |
|----|-------|----------|----------------|
| MEMBER-01 | Create member returns 201 | `POST /api/platform/v1/members` | 201 |
| MEMBER-02 | Created member appears in list | `GET /api/platform/v1/members` | 200, member present |
| MEMBER-03 | Update member persists changes | `PUT /api/platform/v1/members/{id}` | 200, updated fields returned |
| MEMBER-04 | Updated member reflects changes in list | `GET /api/platform/v1/members` | 200, updated fields visible |
| MEMBER-05 | Delete member returns 204 | `DELETE /api/platform/v1/members/{id}` | 204 |
| MEMBER-06 | Deleted member no longer appears in list | `GET /api/platform/v1/members` | 200, member absent |
| MEMBER-07 | Create member with duplicate email returns 409 | `POST /api/platform/v1/members` | 409 |
| MEMBER-08 | Delete non-existent member returns 404 | `DELETE /api/platform/v1/members/{id}` | 404 |
| MEMBER-09 | Create member without API key returns 401 | `POST /api/platform/v1/members` | 401 |

---

## Flow 18: CMS Mentorship Pages
**File:** `tests/cms/mentorship.pages.flow.test.ts`

| ID | Title | Endpoint | Expected Result |
|----|-------|----------|----------------|
| CMS-01 | Get ad-hoc timeline returns 200 with valid schema | `GET /api/cms/v1/mentorship/ad-hoc-timeline` | 200, schema valid |
| CMS-02 | Get ad-hoc timeline without API key returns 401 | `GET /api/cms/v1/mentorship/ad-hoc-timeline` | 401 |
| CMS-03 | Get long-term timeline returns 200 with valid schema | `GET /api/cms/v1/mentorship/long-term-timeline` | 200, schema valid |
| CMS-04 | Get long-term timeline without API key returns 401 | `GET /api/cms/v1/mentorship/long-term-timeline` | 401 |
| CMS-05 | Get study groups returns 200 with valid schema | `GET /api/cms/v1/mentorship/study-groups` | 200, schema valid |
| CMS-06 | Get study groups without API key returns 401 | `GET /api/cms/v1/mentorship/study-groups` | 401 |
| CMS-07 | Get mentorship resources returns 200 with valid schema | `GET /api/cms/v1/mentorship/resources` | 200, schema valid |
| CMS-08 | Get mentorship resources without API key returns 401 | `GET /api/cms/v1/mentorship/resources` | 401 |
| CMS-09 | Get mentors page returns 200 with valid schema | `GET /api/cms/v1/mentorship/mentors` | 200, schema valid |
| CMS-10 | Get mentors page filtered by keyword returns matching results | `GET /api/cms/v1/mentorship/mentors?keyword=Diana` | 200, results match keyword |
| CMS-11 | Get mentors page filtered by yearsExperience returns results | `GET /api/cms/v1/mentorship/mentors?yearsExperience=5` | 200 |
| CMS-12 | Get mentors page without API key returns 401 | `GET /api/cms/v1/mentorship/mentors` | 401 |

---

## Flow 19: Email
**File:** `tests/platform/email.flow.test.ts`

| ID | Title | Endpoint | Expected Result |
|----|-------|----------|----------------|
| EMAIL-01 | Preview MENTOR_APPROVED template returns rendered subject and body | `POST /api/platform/v1/email/template/preview` | 201, `subject` and `body` present |
| EMAIL-02 | Preview template with missing required param returns 400 | `POST /api/platform/v1/email/template/preview` | 400 |
| EMAIL-03 | Preview template with invalid template type returns 400 | `POST /api/platform/v1/email/template/preview` | 400 |
| EMAIL-04 | Preview template without API key returns 401 | `POST /api/platform/v1/email/template/preview` | 401 |

---

## Summary

| Flow | File | Test Cases | Priority |
|------|------|-----------|----------|
| 1. Auth | `tests/auth/auth.flow.test.ts` | AUTH-01 → AUTH-07 | 🔴 High |
| 2. Mentor — Register and Accept | `tests/platform/mentor.register.accept.flow.test.ts` | MENTOR-A01 → MENTOR-A05 | 🔴 High |
| 3. Mentor — Register and Reject | `tests/platform/mentor.register.reject.flow.test.ts` | MENTOR-R01 → MENTOR-R04 | 🔴 High |
| 4. Mentor — Re-registration | `tests/platform/mentor.reregistration.flow.test.ts` | MENTOR-P01 | 🔴 High |
| 5. Mentor — Update | `tests/platform/mentor.update.flow.test.ts` | MENTOR-U01 → MENTOR-U02 | 🟡 Medium |
| 6. Mentor — Validation | `tests/platform/mentor.validation.flow.test.ts` | MENTOR-E01 → MENTOR-E03 | 🔴 High |
| 7. Mentee — Register and Accept | `tests/platform/mentee.register.accept.flow.test.ts` | MENTEE-A01 → MENTEE-A06 | 🔴 High |
| 8. Mentee — Register and Reject | `tests/platform/mentee.register.reject.flow.test.ts` | MENTEE-R01 → MENTEE-R05 | 🔴 High |
| 9. Mentee — Validation | `tests/platform/mentee.validation.flow.test.ts` | MENTEE-E01 → MENTEE-E05 | 🔴 High |
| 10. Mentee — Registration Guards | `tests/platform/mentee.registration.guards.flow.test.ts` | MENTEE-RG01 → MENTEE-RG05 | 🔴 High |
| 11. Mentee — Pairing and Forwarding | `tests/platform/mentee.pairing.forwarding.flow.test.ts` | MENTEE-PF01 → MENTEE-PF08 | 🔴 High |
| 12. Mentee — Admin Management | `tests/platform/mentee.admin.management.flow.test.ts` | MENTEE-AM01 → MENTEE-AM07 | 🔴 High |
| 13. Mentee — Manual Matching | `tests/platform/mentee.manual.matching.flow.test.ts` | MENTEE-MM01 → MENTEE-MM06 | 🟡 Medium |
| 14. Mentorship application | `tests/platform/mentorship.application.flow.test.ts` | APPL-01 → APPL-10 | 🔴 High |
| 15. Match admin | `tests/platform/mentorship.match.flow.test.ts` | MATCH-01 → MATCH-07 | 🟡 Medium |
| 16. Cycle management | `tests/platform/mentorship.cycle.flow.test.ts` | CYCLE-01 → CYCLE-06 | 🟡 Medium |
| 17. Member management | `tests/platform/member.management.flow.test.ts` | MEMBER-01 → MEMBER-09 | 🟡 Medium |
| 18. CMS pages | `tests/cms/mentorship.pages.flow.test.ts` | CMS-01 → CMS-12 | 🟢 Low |
| 19. Email | `tests/platform/email.flow.test.ts` | EMAIL-01 → EMAIL-04 | 🟢 Low |

**Total: 112 test cases**
