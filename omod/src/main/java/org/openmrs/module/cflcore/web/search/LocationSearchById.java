/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.web.search;

import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.response.InvalidSearchException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static java.util.Collections.singletonList;
import static org.apache.commons.lang.StringUtils.isNumeric;

/**
 * The LocationSearchById Class.
 *
 * <p>The OpenMRS SearchHandler for Location search by its ID.
 *
 * <p>Regular URL: {@code openmrs/ws/rest/v1/location?s=byId&id=<id>}
 */
@Component
public class LocationSearchById implements SearchHandler {

  private static final String LOCATION_ID_PARAM = "id";

  private final SearchConfig searchConfig =
      new SearchConfig(
          "byId",
          RestConstants.VERSION_1 + "/location",
          Arrays.asList(
              "1.8.*", "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*", "2.2.*", "2.3.*",
              "2.4.*"),
          new SearchQuery.Builder("Allows you to find location by Id")
              .withRequiredParameters(LOCATION_ID_PARAM)
              .build());

  @Autowired private LocationService locationService;

  @Override
  public SearchConfig getSearchConfig() {
    return searchConfig;
  }

  @Override
  public PageableResult search(RequestContext requestContext) throws ResponseException {
    final String locationIdRaw = requestContext.getParameter(LOCATION_ID_PARAM);

    if (!isNumeric(locationIdRaw)) {
      throw new InvalidSearchException(
          "The 'id' query parameter must be a number, but was: " + locationIdRaw);
    }

    final Location location = locationService.getLocation(Integer.parseInt(locationIdRaw));

    if (location == null) {
      return new EmptySearchResult();
    } else {
      return new AlreadyPaged<>(requestContext, singletonList(location), false);
    }
  }
}
