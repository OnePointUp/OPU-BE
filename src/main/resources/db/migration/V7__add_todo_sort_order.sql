ALTER TABLE todo
    ADD COLUMN sort_order INT NOT NULL DEFAULT 0;

-- 기존 데이터가 있을 경우
-- 모든 기존 Todo는 날짜별 max + 1 기준으로 순서 지정 가능
SET @prev_member := NULL, @prev_date := NULL, @rn := 0;

SELECT t2.id,
       @rn := IF(@prev_member = t2.member_id AND @prev_date = t2.scheduled_date, @rn + 1, 0) AS new_order,
       @prev_member := t2.member_id,
       @prev_date := t2.scheduled_date
FROM todo t2
ORDER BY t2.member_id, t2.scheduled_date, t2.created_at;