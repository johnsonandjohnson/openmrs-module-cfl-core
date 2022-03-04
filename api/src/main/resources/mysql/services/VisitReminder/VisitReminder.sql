SELECT cast(CASE
                WHEN (SELECT concat(',', property_value, ',')
                      FROM global_property
                      WHERE property = 'message.daysToCallBeforeVisit.default'
                      LIMIT 1) LIKE concat('%,', DATEDIFF(v.date_started, :startDateTime), ',%')
                    THEN :startDateTime
                WHEN (SELECT concat(',', property_value, ',')
                      FROM global_property
                      WHERE property = 'message.daysToCallBeforeVisit.default'
                      LIMIT 1) LIKE concat('%,', DATEDIFF(v.date_started, DATE_ADD(@startDateTime, INTERVAL 1 DAY)), ',%')
                    THEN DATE_ADD(v.date_started, INTERVAL 1 DAY)
    END AS DATE)             AS EXECUTION_DATE,
       1                     AS MESSAGE_ID,
       mtfv.SERVICE_TYPE     AS CHANNEL_ID,
       pmt.patient_id        AS PATIENT_ID,
       pmt.actor_id          AS ACTOR_ID,
       v.visit_type_id       AS visitTypeId,
       v.location_id         AS locationId,
       v.date_started        AS dateStarted,
       v.visit_id            AS visitId,
       (SELECT va.value_reference
        FROM visit_attribute va
                 JOIN visit_attribute_type vat ON va.attribute_type_id = vat.visit_attribute_type_id
        WHERE vat.name = 'Visit Time'
          AND va.visit_id = v.visit_id
          AND va.voided = 0) AS timeStarted
FROM messages_patient_template pmt
         JOIN (SELECT t.patient_template_id,
                      MAX(CASE WHEN tf.name = 'Service type' THEN t.value ELSE NULL END) AS      SERVICE_TYPE,
                      MAX(CASE WHEN tf.name = 'Start of messages' THEN t.value ELSE NULL END) AS START_DATE,
                      MAX(CASE
                              WHEN tf.name = 'End of messages' THEN SUBSTRING_INDEX(t.value, '|', 1)
                              ELSE NULL END) AS                                                  END_DATE_TYPE,
                      MAX(CASE
                              WHEN tf.name = 'End of messages' THEN SUBSTRING_INDEX(t.value, '|', -1)
                              ELSE NULL END) AS                                                  END_DATE
               FROM messages_template_field_value t
                        JOIN messages_template_field tf ON tf.messages_template_field_id = t.template_field_id
               GROUP BY t.patient_template_id) mtfv
              ON mtfv.patient_template_id = pmt.messages_patient_template_id
                  AND mtfv.service_type <> 'Deactivate service'
                  AND mtfv.start_date <= :startDateTime
                  AND (mtfv.end_date = 'EMPTY'
                      || (mtfv.end_date_type = 'DATE_PICKER' && mtfv.end_date >= :startDateTime)
                      || (mtfv.end_date_type = 'AFTER_TIMES' && mtfv.end_date > (select count(*)
                                                                                 from messages_scheduled_service
                                                                                 where patient_template_id = pmt.messages_patient_template_id)))
         JOIN person_attribute pa ON pa.person_id = pmt.patient_id and pa.value = 'ACTIVATED' and pa.voided = 0
         JOIN person_attribute_type pat
              ON pat.person_attribute_type_id = pa.person_attribute_type_id AND pat.name = 'Person status'
         JOIN messages_template mt ON mt.name = 'Visit reminder' and mt.messages_template_id = pmt.template_id
         JOIN visit v ON v.patient_id = pmt.patient_id AND v.voided = 0
    AND ((SELECT concat(',', property_value, ',')
          FROM global_property
          WHERE property = 'message.daysToCallBeforeVisit.default'
          LIMIT 1) LIKE concat('%,', DATEDIFF(v.date_started, :startDateTime), ',%')
        OR (SELECT concat(',', property_value, ',')
            FROM global_property
            WHERE property = 'message.daysToCallBeforeVisit.default'
            LIMIT 1) LIKE concat('%,', DATEDIFF(v.date_started, DATE_ADD(@startDateTime, INTERVAL 1 DAY)), ',%'))
WHERE pmt.voided = 0