package org.openmrs.module.cfldistribution.api.metadata;

import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.bundle.VersionedMetadataBundle;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.Priority;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.patientflags.evaluator.SQLFlagEvaluator;

import java.util.HashSet;

import static java.util.Collections.singleton;

/** Adds Patient flags. */
@SuppressWarnings("PMD.ExcessiveMethodLength")
public class PatientFlagsMetadata extends VersionedMetadataBundle {
  private Tag savedCflTag;
  private Priority savedCflPriority;

  @Override
  public int getVersion() {
    return 1;
  }

  @Override
  protected void installEveryTime() throws Exception {
    // nothing to do
  }

  @Override
  protected void installNewVersion() throws Exception {
    savedCflTag = install(newCFLTag());
    savedCflPriority = install(newCFLFlagPriority());
    installCFLFlags();
  }

  private Tag newCFLTag() {
    final Tag cflTag = new Tag();
    cflTag.setName("CFL tag");
    cflTag.setUuid("ef1a1b2d-c7a5-44d7-a518-ef78087abb23");
    cflTag.setRoles(new HashSet<>(Context.getUserService().getAllRoles()));
    return cflTag;
  }

  private Priority newCFLFlagPriority() {
    final Priority cflPriority = new Priority();
    cflPriority.setName("CFL flag priority");
    cflPriority.setStyle(
        "border: 1px solid #51a351; color: #51a351; padding: 1px 2px; border-radius: 4px;");
    cflPriority.setRank(1);
    cflPriority.setUuid("15d23b9b-1dc1-448c-81ce-8c5d191b0fff");
    return cflPriority;
  }

  private void installCFLFlags() {
    install(
        newSQLFlag(
            "CFL HIV encounter",
            "Last CFL HIV encounter on ${1}",
            "SELECT\n"
                + "\te.patient_id,\n"
                + "\tDATE_FORMAT(e.encounter_datetime, '%d.%m.%Y')\n"
                + "FROM\n"
                + "\t(\n"
                + "\tSELECT\n"
                + "\t\t*\n"
                + "\tFROM\n"
                + "\t\tencounter\n"
                + "\tORDER BY\n"
                + "\t\tencounter_datetime DESC) e\n"
                + "WHERE\n"
                + "\te.encounter_type IN\n"
                + "                (\n"
                + "\tSELECT\n"
                + "\t\tencounter_type_id\n"
                + "\tFROM\n"
                + "\t\tencounter_type\n"
                + "\tWHERE\n"
                + "\t\tuuid IN ('c1c99d40-3039-4ff2-8213-fdf4b5ece4d5', '645ce7ac-3714-4cb4-922a-9d5b4b164fa8', "
                + "'6932803d-f0a3-44e5-90cd-e08d86f98d70'));"));
    install(
        newSQLFlag(
            "CFL flag - missed last 3 visits",
            "Missed last 3 visits",
            "SELECT\n"
                + "\ti.patient_id\n"
                + "FROM\n"
                + "\t(\n"
                + "\tSELECT\n"
                + "\t\tst.patient_id,\n"
                + "\t\tst.status,\n"
                + "\t\tCount(st.status) AS missed_count\n"
                + "\tFROM\n"
                + "\t\t(\n"
                + "\t\tSELECT\n"
                + "\t\t\t*\n"
                + "\t\tFROM\n"
                + "\t\t\t(\n"
                + "\t\t\tSELECT\n"
                + "\t\t\t\ta.patient_id,\n"
                + "\t\t\t\ta.status,\n"
                + "\t\t\t\t@rn := IF(@prev = a.patient_id,\n"
                + "\t\t\t\t@rn + 1,\n"
                + "\t\t\t\t1) as rn,\n"
                + "\t\t\t\t@prev := a.patient_id\n"
                + "\t\t\tFROM\n"
                + "\t\t\t\t(\n"
                + "\t\t\t\tSELECT\n"
                + "\t\t\t\t\tv.visit_id,\n"
                + "\t\t\t\t\tv.patient_id,\n"
                + "\t\t\t\t\tva.value_reference as status,\n"
                + "\t\t\t\t\tv.date_started as scheduled_date\n"
                + "\t\t\t\tFROM\n"
                + "\t\t\t\t\topenmrs.visit v\n"
                + "\t\t\t\tINNER JOIN (\n"
                + "\t\t\t\t\tSELECT\n"
                + "\t\t\t\t\t\t*\n"
                + "\t\t\t\t\tFROM\n"
                + "\t\t\t\t\t\topenmrs.visit_attribute\n"
                + "\t\t\t\t\tWHERE\n"
                + "\t\t\t\t\t\tattribute_type_id = (\n"
                + "\t\t\t\t\t\tSELECT\n"
                + "\t\t\t\t\t\t\tvat.visit_attribute_type_id\n"
                + "\t\t\t\t\t\tFROM\n"
                + "\t\t\t\t\t\t\topenmrs.visit_attribute_type vat\n"
                + "\t\t\t\t\t\tWHERE\n"
                + "\t\t\t\t\t\t\tvat.uuid = \"70ca70ac-53fd-49e4-9abe-663d4785fe62\"\n"
                + "                            )\n"
                + "\t\t\t\t\t\tAND voided = 0\n"
                + "\t\t\t\t\t\tAND value_reference <> \"SCHEDULED\"\n"
                + "\t\t\t\t\tORDER BY\n"
                + "\t\t\t\t\t\tdate_created DESC\n"
                + "                            ) va ON\n"
                + "\t\t\t\t\tv.visit_id = va.visit_id\n"
                + "\t\t\t\tORDER BY\n"
                + "\t\t\t\t\tv.patient_id DESC,\n"
                + "\t\t\t\t\tv.date_started DESC\n"
                + "                            ) a\n"
                + "\t\t\tJOIN (\n"
                + "\t\t\t\tSELECT\n"
                + "\t\t\t\t\t@prev := NULL,\n"
                + "\t\t\t\t\t@rn := 0\n"
                + "                            ) AS vars\n"
                + "\t\t\tORDER BY\n"
                + "\t\t\t\ta.patient_id,\n"
                + "\t\t\t\ta.scheduled_date DESC\n"
                + "                            ) test\n"
                + "\t\tWHERE\n"
                + "\t\t\ttest.rn < 4\n"
                + "                            ) st\n"
                + "\tGROUP BY\n"
                + "\t\tst.patient_id,\n"
                + "\t\tst.status\n"
                + "                            ) i\n"
                + "WHERE\n"
                + "\ti.status = \"MISSED\"\n"
                + "\tAND missed_count > 2;"));
    install(
        newSQLFlag(
            "Consent Pending",
            "Consent Pending",
            "SELECT\n"
                + "\ta.patient_id\n"
                + "FROM\n"
                + "\tpatient a\n"
                + "WHERE\n"
                + "\ta.patient_id in (\n"
                + "\tSELECT\n"
                + "\t\tperson_id\n"
                + "\tFROM\n"
                + "\t\tperson_attribute\n"
                + "\tWHERE\n"
                + "\t\tperson_attribute_type_id IN (\n"
                + "\t\tSELECT\n"
                + "\t\t\tpat.person_attribute_type_id\n"
                + "\t\tFROM\n"
                + "\t\t\tperson_attribute_type pat\n"
                + "\t\tWHERE\n"
                + "\t\t\tpat.name = 'Person status')\n"
                + "\t\tAND voided = 0\n"
                + "\t\tAND value = 'NO_CONSENT');"));
    install(
        newSQLFlag(
            "Decreasing adherence",
            "Decreasing adherence of a patient",
            "SELECT\n"
                + "\tadhTrend.patient_id\n"
                + "FROM\n"
                + "\t(\n"
                + "\tSELECT\n"
                + "\t\tadhLev1.patient_id,\n"
                + "\t\tadhLev1.adh - adhLev2.adh AS adhLevDiff\n"
                + "\tFROM\n"
                + "\t\t(\n"
                + "\t\tSELECT\n"
                + "\t\t\tsum(text_response = 'YES') / sum(text_response = 'YES' OR text_response = 'NO') adh,\n"
                + "\t\t\tmar.patient_id\n"
                + "\t\tFROM\n"
                + "\t\t\t(\n"
                + "\t\t\tSELECT\n"
                + "\t\t\t\t*\n"
                + "\t\t\tFROM\n"
                + "\t\t\t\tmessages_actor_response mar\n"
                + "\t\t\tWHERE\n"
                + "\t\t\t\tsource_type LIKE 'SCHEDULED_SERVICE_GROUP') mar\n"
                + "\t\tJOIN messages_scheduled_service_group mssg ON\n"
                + "\t\t\tmar.source_id = mssg.messages_scheduled_service_group_id\n"
                + "\t\tJOIN messages_scheduled_service mss ON\n"
                + "\t\t\tmssg.messages_scheduled_service_group_id = mss.group_id\n"
                + "\t\tJOIN messages_patient_template mpt ON\n"
                + "\t\t\tmss.patient_template_id = mpt.messages_patient_template_id\n"
                + "\t\tJOIN messages_template mt ON\n"
                + "\t\t\tmpt.template_id = mt.messages_template_id\n"
                + "\t\tWHERE\n"
                + "\t\t\tmt.NAME = 'Adherence report daily'\n"
                + "\t\t\tAND mar.answered_time >= (\n"
                + "\t\t\tSELECT\n"
                + "\t\t\t\tdate_add(now(), interval - (\n"
                + "            SELECT property_value\n"
                + "            FROM global_property\n"
                + "            WHERE property = 'messages.benchmarkPeriod') DAY))\n"
                + "\t\t\tAND mar.answered_time <= now()\n"
                + "\t\t\tOR mt.NAME = 'Adherence report weekly'\n"
                + "\t\t\tAND mar.answered_time >= (\n"
                + "\t\t\tSELECT\n"
                + "\t\t\t\tdate_add(now(), interval - (\n"
                + "            SELECT property_value\n"
                + "            FROM global_property\n"
                + "            WHERE property = 'messages.benchmarkPeriod') DAY))\n"
                + "\t\t\tAND mar.answered_time <= now()\n"
                + "\t\tGROUP BY\n"
                + "\t\t\tmar.patient_id) adhLev1\n"
                + "\tLEFT JOIN (\n"
                + "\t\tSELECT\n"
                + "\t\t\tsum(text_response = 'YES') / sum(text_response = 'YES' OR text_response = 'NO') AS adh,\n"
                + "\t\t\tmar.patient_id\n"
                + "\t\tFROM\n"
                + "\t\t\t(\n"
                + "\t\t\tSELECT\n"
                + "\t\t\t\t*\n"
                + "\t\t\tFROM\n"
                + "\t\t\t\tmessages_actor_response mar\n"
                + "\t\t\tWHERE\n"
                + "\t\t\t\tsource_type LIKE 'SCHEDULED_SERVICE_GROUP') mar\n"
                + "\t\tJOIN messages_scheduled_service_group mssg ON\n"
                + "\t\t\tmar.source_id = mssg.messages_scheduled_service_group_id\n"
                + "\t\tJOIN messages_scheduled_service mss ON\n"
                + "\t\t\tmssg.messages_scheduled_service_group_id = mss.group_id\n"
                + "\t\tJOIN messages_patient_template mpt ON\n"
                + "\t\t\tmss.patient_template_id = mpt.messages_patient_template_id\n"
                + "\t\tJOIN messages_template mt ON\n"
                + "\t\t\tmpt.template_id = mt.messages_template_id\n"
                + "\t\tWHERE\n"
                + "\t\t\tmt.NAME = 'Adherence report daily'\n"
                + "\t\t\tAND mar.answered_time >= (\n"
                + "\t\t\tSELECT\n"
                + "\t\t\t\tdate_add(now(), interval - (\n"
                + "            SELECT 2 * (\n"
                + "            SELECT property_value\n"
                + "            FROM global_property\n"
                + "            WHERE property = 'messages.benchmarkPeriod')) DAY))\n"
                + "\t\t\tAND mar.answered_time <= (\n"
                + "\t\t\tSELECT\n"
                + "\t\t\t\tdate_add(now(), interval - (\n"
                + "            SELECT property_value\n"
                + "            FROM global_property\n"
                + "            WHERE property = 'messages.benchmarkPeriod') DAY))\n"
                + "\t\t\tOR mt.NAME = 'Adherence report weekly'\n"
                + "\t\t\tAND mar.answered_time >= (\n"
                + "\t\t\tSELECT\n"
                + "\t\t\t\tdate_add(now(), interval - (\n"
                + "            SELECT 2 * (\n"
                + "            SELECT property_value\n"
                + "            FROM global_property\n"
                + "            WHERE property = 'messages.benchmarkPeriod')) DAY))\n"
                + "\t\t\tAND mar.answered_time <= (\n"
                + "\t\t\tSELECT\n"
                + "\t\t\t\tdate_add(now(), interval - (\n"
                + "            SELECT property_value\n"
                + "            FROM global_property\n"
                + "            WHERE property = 'messages.benchmarkPeriod') DAY))\n"
                + "\t\tGROUP BY\n"
                + "\t\t\tmar.patient_id) adhLev2\n"
                + "            ON\n"
                + "\t\tadhLev1.patient_id = adhLev2.patient_id) adhTrend\n"
                + "WHERE\n"
                + "\tadhLevDiff < -1 * (\n"
                + "\tSELECT\n"
                + "\t\t(\n"
                + "\t\tSELECT\n"
                + "\t\t\tproperty_value\n"
                + "\t\tFROM\n"
                + "\t\t\tglobal_property\n"
                + "\t\tWHERE\n"
                + "\t\t\tproperty = 'messages.cutOffScoreForAdherenceTrend') / 100)"));
    install(
        newSQLFlag(
            "Incorrect pincode",
            "Incorrect pincode entered last 3 times",
            "SELECT\n"
                + "\tq.patient_id\n"
                + "FROM\n"
                + "\t(\n"
                + "\tSELECT\n"
                + "\t\tactor_id as patient_id,\n"
                + "\t\ttext_response,\n"
                + "\t\tcount(*) rn\n"
                + "\tFROM\n"
                + "\t\tmessages_actor_response mar\n"
                + "\tWHERE\n"
                + "\t\tmar.messages_actor_response_id IN (\n"
                + "\t\tSELECT\n"
                + "\t\t\tm.messages_actor_response_id\n"
                + "\t\tFROM\n"
                + "\t\t\t(\n"
                + "\t\t\tSELECT\n"
                + "\t\t\t\tm.messages_actor_response_id,\n"
                + "\t\t\t\tm.actor_id,\n"
                + "\t\t\t\t@rn := IF(@prev = m.actor_id,\n"
                + "\t\t\t\t@rn + 1,\n"
                + "\t\t\t\t1) rn,\n"
                + "\t\t\t\t@prev := m.actor_id\n"
                + "\t\t\tFROM\n"
                + "\t\t\t\tmessages_actor_response m\n"
                + "\t\t\tJOIN (\n"
                + "\t\t\t\tSELECT\n"
                + "\t\t\t\t\t@prev := NULL,\n"
                + "\t\t\t\t\t@rn := 0 ) AS vars\n"
                + "\t\t\tWHERE\n"
                + "\t\t\t\tm.text_question = \"PIN\"\n"
                + "\t\t\tORDER BY\n"
                + "\t\t\t\tm.actor_id ,\n"
                + "\t\t\t\tm.answered_time DESC) m\n"
                + "\t\tWHERE\n"
                + "\t\t\tm.actor_id = mar.actor_id\n"
                + "\t\t\tAND m.rn < 4)\n"
                + "\tGROUP BY\n"
                + "\t\tmar.actor_id,\n"
                + "\t\tmar.text_response) q\n"
                + "WHERE\n"
                + "\tq.rn >= 3\n"
                + "\tAND q.text_response = \"INVALID\""));
  }

  private Flag newSQLFlag(String name, String message, String sql) {
    final Flag sqlFlag = new Flag();
    sqlFlag.setEnabled(true);
    sqlFlag.setEvaluator(SQLFlagEvaluator.class.getName());
    sqlFlag.setPriority(savedCflPriority);
    sqlFlag.setTags(singleton(savedCflTag));
    sqlFlag.setName(name);
    sqlFlag.setCriteria(sql);
    sqlFlag.setMessage(message);
    return sqlFlag;
  }
}
