-- Local development seed data.
--
-- This file is executed on every startup because spring.sql.init.mode=always.
-- Keep statements idempotent (INSERT ... ON DUPLICATE KEY UPDATE / INSERT IGNORE)
-- or guard them with existence checks.
--
-- Spring's script runner rejects a file that contains only comments, so the
-- no-op statement below keeps an unseeded database startable.
SELECT 1;
