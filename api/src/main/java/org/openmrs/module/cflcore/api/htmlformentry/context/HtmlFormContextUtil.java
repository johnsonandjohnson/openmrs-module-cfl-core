package org.openmrs.module.cflcore.api.htmlformentry.context;

import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.OrderSet;
import org.openmrs.OrderSetMember;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.htmlformentry.model.Regimen;
import org.openmrs.module.cflcore.api.htmlformentry.model.RegimenDrug;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class HtmlFormContextUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(HtmlFormContextUtil.class);

  public static List<Regimen> getRegimensWithDrugs() {
    List<Regimen> regimens = new ArrayList<>();
    Context.getOrderSetService()
        .getOrderSets(false)
        .forEach(
            regimen ->
                regimens.add(new Regimen(regimen.getName(), createDrugsFromRegimen(regimen))));
    return regimens;
  }

  public static List<RegimenDrug> createDrugsFromRegimen(OrderSet regimen) {
    List<RegimenDrug> regimenDrugs = new ArrayList<>();
    ObjectMapper mapper = new ObjectMapper();
    for (OrderSetMember regimenDrug : regimen.getOrderSetMembers()) {
      try {
        regimenDrugs.add(mapper.readValue(regimenDrug.getOrderTemplate(), RegimenDrug.class));
      } catch (IOException ex) {
        LOGGER.error("Error occurred while converting JSON string value to RegimenDrug class");
      }
    }
    return regimenDrugs;
  }

  private HtmlFormContextUtil() {}
}
