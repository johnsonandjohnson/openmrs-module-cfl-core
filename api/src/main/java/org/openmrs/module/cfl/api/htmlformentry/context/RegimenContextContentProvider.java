package org.openmrs.module.cfl.api.htmlformentry.context;

import org.apache.velocity.VelocityContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.OrderSet;
import org.openmrs.OrderSetMember;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.api.htmlformentry.model.Regimen;
import org.openmrs.module.cfl.api.htmlformentry.model.RegimenDrug;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.velocity.VelocityContextContentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegimenContextContentProvider implements VelocityContextContentProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(RegimenContextContentProvider.class);

  private static final String REGIMENS_PROPERTY_NAME = "regimens";

  @Override
  public void populateContext(FormEntrySession formEntrySession, VelocityContext velocityContext) {
    formEntrySession.addToVelocityContext(REGIMENS_PROPERTY_NAME, getRegimensWithDrugs());
  }

  private List<Regimen> getRegimensWithDrugs() {
    List<Regimen> regimens = new ArrayList<>();
    Context.getOrderSetService()
        .getOrderSets(false)
        .forEach(
            regimen ->
                regimens.add(new Regimen(regimen.getName(), createDrugsFromRegimen(regimen))));
    return regimens;
  }

  private List<RegimenDrug> createDrugsFromRegimen(OrderSet regimen) {
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
}
