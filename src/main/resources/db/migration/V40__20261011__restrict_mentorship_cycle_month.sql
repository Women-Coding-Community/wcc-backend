-- Restrict new mentorship cycles to February through November.
-- Existing January and December cycles remain valid historical data.
ALTER TABLE mentorship_cycles
    ADD CONSTRAINT valid_cycle_month
        CHECK (cycle_month >= 2 AND cycle_month <= 11)
        NOT VALID;
