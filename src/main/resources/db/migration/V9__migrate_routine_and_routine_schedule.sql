-- 1. routine 테이블에 schedule 관련 컬럼 추가
ALTER TABLE routine
    ADD COLUMN week_days SET('MON','TUE','WED','THU','FRI','SAT','SUN') NULL,
    ADD COLUMN month_days TEXT NULL,
    ADD COLUMN days TEXT NULL;

-- 2. routine_schedule 데이터를 routine 테이블로 병합
--   routine_schedule 에 값이 있으면 routine 의 새 컬럼에 넣음
UPDATE routine r
    JOIN routine_schedule rs ON r.id = rs.routine_id
SET
    r.week_days = rs.week_days,
    r.month_days = rs.month_days,
    r.days = rs.days;

-- 3. routine_schedule 테이블 삭제
DROP TABLE routine_schedule;
