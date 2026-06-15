-- Disable users that is not allowed to login to the system.
UPDATE user_accounts
SET enabled = false
WHERE email = 'sonali.learn.ai@gmail.com';