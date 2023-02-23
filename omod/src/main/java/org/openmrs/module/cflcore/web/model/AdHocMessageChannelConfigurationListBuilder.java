package org.openmrs.module.cflcore.web.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class AdHocMessageChannelConfigurationListBuilder
    implements AdHocMessageChannelConfigurationBuilder {
  private final Collection<AdHocMessageChannelConfigurationBuilder> builders;

  AdHocMessageChannelConfigurationListBuilder(
      Collection<AdHocMessageChannelConfigurationBuilder> builders) {
    this.builders = builders;
  }

  @Override
  public Map<String, String> build() {
    return builders.stream()
        .map(AdHocMessageChannelConfigurationBuilder::build)
        .reduce(
            new HashMap<>(),
            (map1, map2) -> {
              map1.putAll(map2);
              return map1;
            });
  }
}
