ALTER TABLE storybook
    ADD COLUMN recommendation_phrase VARCHAR(100) NULL AFTER title;

-- dev 및 prod에서 이관으로 처리 불가능한 데이터로 직접 삽입
UPDATE storybook
SET recommendation_phrase = '부모님의 지난 시간을 알고 싶다면'
WHERE title = '오래 전 당신';
UPDATE storybook
SET recommendation_phrase = '부모님을 더 잘 이해하고 싶다면'
WHERE title = '당신 사용설명서';
UPDATE storybook
SET recommendation_phrase = '가족의 마음을 다시 느끼고 싶다면'
WHERE title = '가족의 온도';
UPDATE storybook
SET recommendation_phrase = '함께할 취향을 찾고 싶다면'
WHERE title = '취향이 닿는 날';
UPDATE storybook
SET recommendation_phrase = '함께하는 시간을 늘리고 싶다면'
WHERE title = '나란히 걷는 날';
UPDATE storybook
SET recommendation_phrase = '먼저 마음을 표현하고 싶다면'
WHERE title = '오늘은 내가 먼저';
UPDATE storybook
SET recommendation_phrase = '특별한 생신을 준비하고 싶다면'
WHERE title = '생신까지 열 장';
UPDATE storybook
SET recommendation_phrase = '가족의 순간을 남기고 싶다면'
WHERE title = '한 장의 가족사진';
UPDATE storybook
SET recommendation_phrase = '어색한 관계에 다가가고 싶다면'
WHERE title = '손을 내미는 연습';
UPDATE storybook
SET recommendation_phrase = '부모님을 응원하고 싶다면'
WHERE title = '당신의 1호 팬';