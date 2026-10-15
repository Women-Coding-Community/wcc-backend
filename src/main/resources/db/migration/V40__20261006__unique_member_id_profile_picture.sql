-- Enforce one profile picture per member at the schema level.
--
-- Concurrent uploads previously raced between findByMemberId and create, leaving a member
-- with two member_profile_picture rows. Every later read then threw
-- IncorrectResultSizeDataAccessException, which was swallowed to Optional.empty(), so the
-- member's picture silently and permanently vanished from all responses. Combined with the
-- ON CONFLICT upsert in PostgresMemberProfilePictureRepository.create, this guarantees at
-- most one row per member.
ALTER TABLE public.member_profile_picture
    ADD CONSTRAINT member_profile_picture_member_id_unique UNIQUE (member_id);
