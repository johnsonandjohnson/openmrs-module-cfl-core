package org.openmrs.module.cfl.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.api.dto.PersonOverviewEntryDTO;
import org.openmrs.module.cfl.api.dto.ResultsWrapperDTO;
import org.openmrs.module.cfl.api.mapper.PersonOverviewMapper;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.strategy.FindPersonFilterStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller(value = "cfl.personController")
public class PersonController extends BaseCflModuleRestController {

    @Autowired
    @Qualifier("cfl.personOverviewMapper")
    private PersonOverviewMapper personOverviewMapper;

    @Autowired
    @Qualifier("cfl.configService")
    private ConfigService configService;

    @RequestMapping(value = "/people", method = RequestMethod.GET)
    @ResponseBody
    public ResultsWrapperDTO<PersonOverviewEntryDTO> getPeople(@RequestParam("query") String query) {
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
