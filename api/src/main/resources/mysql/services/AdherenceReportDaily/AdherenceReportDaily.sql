SELECT cast(CASE
                WHEN mtfv.weekdays LIKE concat('%', DAYNAME(:startDateTime), '%') THEN :startDateTime
                WHEN mtfv.weekdays LIKE concat('%', DAYNAME(DATE_ADD(:startDateTime, INTERVAL 1 DAY)), '%')
                    THEN DATE_ADD(:startDateTime, INTERVAL 1 DAY)
    END AS DATE)         AS EXECUTION_DATE,
       1                 AS MESSAGE_ID,
       mtfv.SERVICE_TYPE AS CHANNEL_ID,
       pmt.patient_id    AS PATIENT_ID,
       pmt.actor_id      AS ACTOR_ID
FROM messages_patient_template pmt
         JOIN (SELECT t.patient_template_id,
                      MAX(CASE WHEN tf.name = 'Service type' THEN t.value ELSE NULL END)                                   AS SERVICE_TYPE,
                      MAX(CASE
                              WHEN tf.name = 'Week day of delivering message' THEN t.value
                              ELSE NULL END)                                                                               AS WEEKDAYS,
                      MAX(
                              CASE WHEN tf.name = 'Start of daily messages' THEN t.value ELSE NULL END)                    AS START_DATE,
                      MAX(CASE
                              WHEN tf.name = 'End of daily messages' THEN SUBSTRING_INDEX(t.value, '|', 1)
                              ELSE NULL END)                                                                               AS END_DATE_TYPE,
                      MAX(CASE
                              WHEN tf.name = 'End of daily messages' THEN SUBSTRING_INDEX(t.value, '|', -1)
                              ELSE NULL END)                                                                               AS END_DATE
               FROM messages_template_field_value t
                        JOIN messages_template_field tf ON tf.messages_template_field_id = t.template_field_id
               GROUP BY t.patient_template_id) mtfv
              ON mtfv.patient_template_id = pmt.messages_patient_template_id
                  AND mtfv.service_type <> 'Deactivate service'
                  AND (mtfv.weekdays LIKE concat('%', DAYNAME(:startDateTime), '%')
                      || mtfv.weekdays LIKE concat('%', DAYNAME(DATE_ADD(:startDateTime, INTERVAL 1 DAY)), '%'))
                  AND mtfv.start_date <= :startDateTime
                  AND (mtfv.end_date = 'EMPTY'
                      || (mtfv.end_date_type = 'DATE_PICKER' && mtfv.end_date >= :startDateTime)
                      || (mtfv.end_date_type = 'AFTER_TIMES' && mtfv.end_date > (select count(*)
                                                                                 from messages_scheduled_service
                                                                                 where patient_template_id = pmt.messages_patient_template_id)))
         JOIN person_attribute pa ON pa.person_id = pmt.patient_id and pa.value = 'ACTIVATED' and pa.voided = 0
         JOIN person_attribute_type pat
              ON pat.person_attribute_type_id = pa.person_attribute_type_id AND pat.name = 'Person status'
         JOIN messages_template mt ON mt.name = 'Adherence report daily' and mt.messages_template_id = pmt.template_id
WHERE pmt.voided = 0