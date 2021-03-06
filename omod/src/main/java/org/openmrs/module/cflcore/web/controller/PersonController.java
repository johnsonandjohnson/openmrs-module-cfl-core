/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.dto.PersonOverviewEntryDTO;
import org.openmrs.module.cflcore.api.dto.ResultsWrapperDTO;
import org.openmrs.module.cflcore.api.mapper.PersonOverviewMapper;
import org.openmrs.module.cflcore.api.service.ConfigService;
import org.openmrs.module.cflcore.api.strategy.FindPersonFilterStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

@Api(value = "Person data", tags = {"REST API to get Person data"})
@Controller(value = "cfl.personController")
public class PersonController extends BaseCflModuleRestController {

    @Autowired
    @Qualifier("cfl.personOverviewMapper")
    private PersonOverviewMapper personOverviewMapper;

    @Autowired
    @Qualifier("cfl.configService")
    private ConfigService configService;

    @ApiOperation(value = "Get person data", notes = "Get person data")
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful getting person data"),
            @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failure to get person data"),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Error in getting person data")})
    @RequestMapping(value = "/people", method = RequestMethod.GET)
    @ResponseBody
    public ResultsWrapperDTO<PersonOverviewEntryDTO> getPeople(
            @ApiParam(name = "query", value = "Query to get people") @RequestParam("query") String query) {
        ArrayList<PersonOverviewEntryDTO> selectedPeople = new ArrayList<PersonOverviewEntryDTO>();

        if (StringUtils.isNotBlank(query)) {
            List<Person> foundPeople = Context.getPersonService().getPeople(query, false);
            for (Person person : foundPeople) {
                if (shouldBeReturned(person)) {
                    selectedPeople.add(personOverviewMapper.toDto(person));
                }
            }
        }

        return new ResultsWrapperDTO<PersonOverviewEntryDTO>(selectedPeople);
    }

    private boolean shouldBeReturned(Person person) {
        FindPersonFilterStrategy filterStrategy = configService.getPersonFilterStrategy();
        return filterStrategy != null ? filterStrategy.shouldBeReturned(person) : true;
    }
}
