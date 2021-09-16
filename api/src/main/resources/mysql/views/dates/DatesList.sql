CREATE view DATES_LIST AS
SELECT (CURDATE() + INTERVAL (((((t4 * 10000) + (t3 * 1000)) + (t2 * 100)) + (t1 * 10)) + t0) DAY) selected_date
FROM   ((((DIGITS_0 AS t0
JOIN   DIGITS_1     AS t1)
JOIN   DIGITS_2     AS t2)
JOIN   DIGITS_3     AS t3)
JOIN   DIGITS_4     AS t4)
WHERE  (
          CURDATE() + INTERVAL (((((t4 * 10000) + (t3 * 1000)) + (t2 * 100)) + (t1 * 10)) + t0) DAY) <
          (SELECT DATE_ADD(CURDATE(), INTERVAL 2000 DAY));