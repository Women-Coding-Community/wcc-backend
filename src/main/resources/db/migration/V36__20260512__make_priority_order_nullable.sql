-- Allow admin-assigned applications to have no priority order.
-- The original constraint was designed for mentee-submitted applications (priorities 1–5).
-- Manual matches created by admins do not carry a priority ranking.

ALTER TABLE mentee_applications
    DROP CONSTRAINT mentee_applications_priority_order_check;
ALTER TABLE mentee_applications
    DROP CONSTRAINT unique_mentee_cycle_priority;

ALTER table mentee_applications
    ALTER column priority_order set default 0;

comment on table mentee_applications is 'Tracks mentee applications to mentors with priority ranking and workflow status. Supports priority-based mentor selection (0-5 ranking where 0 is highest priority).';
