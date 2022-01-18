package org.openmrs.module.cfldistribution.api.metadata;

import org.openmrs.Concept;
import org.openmrs.OrderFrequency;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.bundle.VersionedMetadataBundle;

/** The metadata package which adds entries to OrderFrequency for CIEL Frequency Concepts. */
public class OrderFrequencyMetadata extends VersionedMetadataBundle {

  private static OrderFrequency orderFrequency(
      String conceptUuid, String uuid, Double frequencyPerDay) {
    final Concept concept = Context.getConceptService().getConceptByUuid(conceptUuid);
    final OrderFrequency obj = new OrderFrequency();

    obj.setConcept(concept);
    obj.setUuid(uuid);
    obj.setFrequencyPerDay(frequencyPerDay);

    return obj;
  }

  @Override
  public int getVersion() {
    return 2;
  }

  @Override
  protected void installEveryTime() {}

  @Override
  protected void installNewVersion() {
    install(orderFrequency(CIELConcepts.ONCE, OrderFrequencies.ONCE, 1.0));
    install(orderFrequency(CIELConcepts.EVERY_30_MINS, OrderFrequencies.EVERY_30_MINS, 48.0));
    install(orderFrequency(CIELConcepts.EVERY_HOUR, OrderFrequencies.EVERY_HOUR, 24.0));
    install(orderFrequency(CIELConcepts.EVERY_TWO_HOURS, OrderFrequencies.EVERY_TWO_HOURS, 12.0));
    install(
        orderFrequency(CIELConcepts.EVERY_THREE_HOURS, OrderFrequencies.EVERY_THREE_HOURS, 8.0));
    install(orderFrequency(CIELConcepts.EVERY_FOUR_HOURS, OrderFrequencies.EVERY_FOUR_HOURS, 6.0));
    install(orderFrequency(CIELConcepts.EVERY_FIVE_HOURS, OrderFrequencies.EVERY_FIVE_HOURS, 4.8));
    install(orderFrequency(CIELConcepts.EVERY_SIX_HOURS, OrderFrequencies.EVERY_SIX_HOURS, 4.0));
    install(
        orderFrequency(CIELConcepts.EVERY_EIGHT_HOURS, OrderFrequencies.EVERY_EIGHT_HOURS, 3.0));
    install(
        orderFrequency(CIELConcepts.EVERY_TWELVE_HOURS, OrderFrequencies.EVERY_TWELVE_HOURS, 2.0));
    install(orderFrequency(CIELConcepts.TWICE_DAILY, OrderFrequencies.TWICE_DAILY, 2.0));
    install(
        orderFrequency(
            CIELConcepts.TWICE_DAILY_BEFORE_MEALS, OrderFrequencies.TWICE_DAILY_BEFORE_MEALS, 2.0));
    install(
        orderFrequency(
            CIELConcepts.TWICE_DAILY_AFTER_MEALS, OrderFrequencies.TWICE_DAILY_AFTER_MEALS, 2.0));
    install(
        orderFrequency(
            CIELConcepts.TWICE_DAILY_WITH_MEALS, OrderFrequencies.TWICE_DAILY_WITH_MEALS, 2.0));
    install(orderFrequency(CIELConcepts.EVERY_24_HOURS, OrderFrequencies.EVERY_24_HOURS, 1.0));
    install(orderFrequency(CIELConcepts.ONCE_DAILY, OrderFrequencies.ONCE_DAILY, 1.0));
    install(
        orderFrequency(CIELConcepts.ONCE_DAILY_BEDTIME, OrderFrequencies.ONCE_DAILY_BEDTIME, 1.0));
    install(
        orderFrequency(CIELConcepts.ONCE_DAILY_EVENING, OrderFrequencies.ONCE_DAILY_EVENING, 1.0));
    install(
        orderFrequency(CIELConcepts.ONCE_DAILY_MORNING, OrderFrequencies.ONCE_DAILY_MORNING, 1.0));
    install(orderFrequency(CIELConcepts.THRICE_DAILY, OrderFrequencies.THRICE_DAILY, 3.0));
    install(
        orderFrequency(
            CIELConcepts.THRICE_DAILY_AFTER_MEALS, OrderFrequencies.THRICE_DAILY_AFTER_MEALS, 3.0));
    install(
        orderFrequency(
            CIELConcepts.THRICE_DAILY_BEFORE_MEALS,
            OrderFrequencies.THRICE_DAILY_BEFORE_MEALS,
            3.0));
    install(
        orderFrequency(
            CIELConcepts.THRICE_DAILY_WITH_MEALS, OrderFrequencies.THRICE_DAILY_WITH_MEALS, 3.0));
    install(
        orderFrequency(
            CIELConcepts.FOUR_TIMES__DAILY_WITH_MEALS,
            OrderFrequencies.FOUR_TIMES__DAILY_WITH_MEALS,
            4.0));
    install(
        orderFrequency(
            CIELConcepts.FOUR_TIMES__AFTER_WITH_MEALS_BEDTIME,
            OrderFrequencies.FOUR_TIMES__AFTER_WITH_MEALS_BEDTIME,
            4.0));
    install(
        orderFrequency(
            CIELConcepts.FOUR_TIMES__BEFORE_WITH_MEALS_BEDTIME,
            OrderFrequencies.FOUR_TIMES__BEFORE_WITH_MEALS_BEDTIME,
            4.0));
    install(orderFrequency(CIELConcepts.EVERY_48_HOURS, OrderFrequencies.EVERY_48_HOURS, 0.5));
    install(
        orderFrequency(
            CIELConcepts.EVERY_36_HOURS, OrderFrequencies.EVERY_36_HOURS, (1 / 36) * 24.0));
    install(
        orderFrequency(
            CIELConcepts.EVERY_72_HOURS, OrderFrequencies.EVERY_72_HOURS, (1 / 72) * 24.0));
    install(
        orderFrequency(
            CIELConcepts.MONDAY_WEDNESDAY_FRIDAY,
            OrderFrequencies.MONDAY_WEDNESDAY_FRIDAY,
            (3 / 168) * 24.0));
  }

  private static class OrderFrequencies {
    static final String ONCE = "162135OFAAAAAAAAAAAAAAA";
    static final String TWICE_DAILY = "160858OFAAAAAAAAAAAAAAA";
    static final String TWICE_DAILY_BEFORE_MEALS = "160859OFAAAAAAAAAAAAAAA";
    static final String TWICE_DAILY_AFTER_MEALS = "160860OFAAAAAAAAAAAAAAA";
    static final String TWICE_DAILY_WITH_MEALS = "160861OFAAAAAAAAAAAAAAA";
    static final String ONCE_DAILY = "160862OFAAAAAAAAAAAAAAA";
    static final String ONCE_DAILY_BEDTIME = "160863OFAAAAAAAAAAAAAAA";
    static final String ONCE_DAILY_EVENING = "160864OFAAAAAAAAAAAAAAA";
    static final String ONCE_DAILY_MORNING = "160865OFAAAAAAAAAAAAAAA";
    static final String THRICE_DAILY = "160866OFAAAAAAAAAAAAAAA";
    static final String THRICE_DAILY_AFTER_MEALS = "160867OFAAAAAAAAAAAAAAA";
    static final String THRICE_DAILY_BEFORE_MEALS = "160868OFAAAAAAAAAAAAAAA";
    static final String THRICE_DAILY_WITH_MEALS = "160869OFAAAAAAAAAAAAAAA";
    static final String FOUR_TIMES__DAILY_WITH_MEALS = "160870OFAAAAAAAAAAAAAAA";
    static final String FOUR_TIMES__AFTER_WITH_MEALS_BEDTIME = "160871OFAAAAAAAAAAAAAAA";
    static final String FOUR_TIMES__BEFORE_WITH_MEALS_BEDTIME = "160872OFAAAAAAAAAAAAAAA";
    static final String EVERY_30_MINS = "162243OFAAAAAAAAAAAAAAA";
    static final String EVERY_HOUR = "162244OFAAAAAAAAAAAAAAA";
    static final String EVERY_TWO_HOURS = "162245OFAAAAAAAAAAAAAAA";
    static final String EVERY_THREE_HOURS = "162246OFAAAAAAAAAAAAAAA";
    static final String EVERY_FOUR_HOURS = "162247OFAAAAAAAAAAAAAAA";
    static final String EVERY_FIVE_HOURS = "162248OFAAAAAAAAAAAAAAA";
    static final String EVERY_SIX_HOURS = "162249OFAAAAAAAAAAAAAAA";
    static final String EVERY_EIGHT_HOURS = "162250OFAAAAAAAAAAAAAAA";
    static final String EVERY_TWELVE_HOURS = "162251OFAAAAAAAAAAAAAAA";
    static final String EVERY_24_HOURS = "162252OFAAAAAAAAAAAAAAA";
    static final String EVERY_48_HOURS = "162253OFAAAAAAAAAAAAAAA";
    static final String EVERY_36_HOURS = "162254OFAAAAAAAAAAAAAAA";
    static final String EVERY_72_HOURS = "162255OFAAAAAAAAAAAAAAA";
    static final String MONDAY_WEDNESDAY_FRIDAY = "162256OFAAAAAAAAAAAAAAA";
  }

  private static class CIELConcepts {
    static final String ONCE = "162135AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String TWICE_DAILY = "160858AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String TWICE_DAILY_BEFORE_MEALS = "160859AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String TWICE_DAILY_AFTER_MEALS = "160860AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String TWICE_DAILY_WITH_MEALS = "160861AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String ONCE_DAILY = "160862AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String ONCE_DAILY_BEDTIME = "160863AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String ONCE_DAILY_EVENING = "160864AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String ONCE_DAILY_MORNING = "160865AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String THRICE_DAILY = "160866AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String THRICE_DAILY_AFTER_MEALS = "160867AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String THRICE_DAILY_BEFORE_MEALS = "160868AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String THRICE_DAILY_WITH_MEALS = "160869AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String FOUR_TIMES__DAILY_WITH_MEALS = "160870AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String FOUR_TIMES__AFTER_WITH_MEALS_BEDTIME = "160871AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String FOUR_TIMES__BEFORE_WITH_MEALS_BEDTIME = "160872AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String EVERY_30_MINS = "162243AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String EVERY_HOUR = "162244AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String EVERY_TWO_HOURS = "162245AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String EVERY_THREE_HOURS = "162246AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String EVERY_FOUR_HOURS = "162247AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String EVERY_FIVE_HOURS = "162248AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String EVERY_SIX_HOURS = "162249AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String EVERY_EIGHT_HOURS = "162250AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String EVERY_TWELVE_HOURS = "162251AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String EVERY_24_HOURS = "162252AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String EVERY_48_HOURS = "162253AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String EVERY_36_HOURS = "162254AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String EVERY_72_HOURS = "162255AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    static final String MONDAY_WEDNESDAY_FRIDAY = "162256AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
  }
}
