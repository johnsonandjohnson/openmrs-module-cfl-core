package org.openmrs.module.cfl.fragment.controller.field;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.dto.RelationshipDTO;
import org.openmrs.module.cfl.api.dto.RelationshipDTOsBuilder;
import org.openmrs.module.cfl.api.service.RelationshipService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.openmrs.module.cfl.CFLRegisterPersonConstants.COMMA;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.INITIAL_RELATIONSHIPS_PROP;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.OTHER_PERSON_UUID_PROP;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.PERSON_PROP;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.RELATIONSHIP_TYPES_PROP;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.RELATIONSHIP_TYPE_PROP;

public class PersonRelationshipFragmentController {

    private static final String PERSON_A_SUFFIX = "-A";
    
    private static final String PERSON_B_SUFFIX = "-B";
    
    public void controller(
            FragmentModel model,
            UiUtils uiUtils,
            PageModel pageModel) {
        model.addAttribute(RELATIONSHIP_TYPES_PROP, sortRelationshipNamesAlphabetically(getRelationshipTypes()));
        Person person = getPerson(pageModel);
        model.addAttribute(PERSON_PROP, person);
        model.addAttribute(INITIAL_RELATIONSHIPS_PROP, buildInitialRelationships(person, uiUtils));
    }

    /**
     * Action updates the person's relationships.
     *
     * @param person  - a person whose relationships will be updated
     * @param request - an action request, used to get the relationship data
     */
    public void updateRelationships(
            @ModelAttribute("person") @BindParams Person person,
            HttpServletRequest request) {
        if (request.getParameterMap().containsKey(RELATIONSHIP_TYPE_PROP)
                && request.getParameterMap().containsKey(OTHER_PERSON_UUID_PROP)) {
            String[] relationshipsTypes = request.getParameterValues(RELATIONSHIP_TYPE_PROP);
            String[] otherPeopleUUIDs = request.getParameterValues(OTHER_PERSON_UUID_PROP);
            getCflRelationshipService().updatedRelationships(new RelationshipDTOsBuilder()
                    .withRelationshipsTypes(relationshipsTypes)
                    .withOtherPeopleUUIDs(otherPeopleUUIDs).build(), person);
        }
    }

    /**
     * Gets the person from the page model. Choose `person` if attribute exists or `patient` in other case.
     *
     * @param pageModel - the requests page model
     * @return - chosen person
     */
    private Person getPerson(PageModel pageModel) {
        Object person = pageModel.get("person");
        if (person == null) {
            person = pageModel.get("patient");
        }
        return (Person) person;
    }

    /**
     * Gets actual person's relationships and returns as initial relationships as a list of {@link RelationshipDTO}.
     * If a provided person is null or hasn't any relationships
     * then a list with single empty {@link RelationshipDTO} will be returned.
     *
     * @param person  - provided person
     * @param uiUtils - util used to create display for related person
     * @return - the list of initial relationships
     */
    private List<RelationshipDTO> buildInitialRelationships(Person person, UiUtils uiUtils) {
        if (person == null || person.getId() == null) {
            return Collections.singletonList(new RelationshipDTO());
        }
        List<RelationshipDTO> result = new ArrayList<RelationshipDTO>();
        for (Relationship relationship : getPersonService().getRelationshipsByPerson(person)) {
            result.add(buildRelationshipDTO(person, relationship, uiUtils));
        }
        return (result.isEmpty()) ? Collections.singletonList(new RelationshipDTO()) : result;
    }

    /**
     * Builds the {@link RelationshipDTO} based on provided parameters, where
     * - name - its name of the other person
     * - uuid - its UUID of the other person
     * - type - its {@link RelationshipType} UUID with suffix determined based on relationship direction (`-A` or `-B`)
     *
     * @param person       - related person
     * @param relationship - related relationship
     * @param uiUtils      - util used to build the display for the other person
     * @return - a {@link RelationshipDTO} representation
     */
    private RelationshipDTO buildRelationshipDTO(Person person, Relationship relationship, UiUtils uiUtils) {
        Person otherPerson = relationship.getPersonA().equals(person) ?
                relationship.getPersonB() : relationship.getPersonA();
        String relationshipTypeUuid = relationship.getPersonA().equals(person) ?
                relationship.getRelationshipType().getUuid() + "-B" : relationship.getRelationshipType().getUuid() + "-A";
        return new RelationshipDTO()
                .setName(uiUtils.format(otherPerson))
                .setUuid(otherPerson.getUuid())
                .setType(relationshipTypeUuid);
    }

    /**
     * Returns the CFL relationship service based on the actual application context.
     *
     * @return - person service
     */
    private RelationshipService getCflRelationshipService() {
        return Context.getRegisteredComponent("cfl.relationshipService", RelationshipService.class);
    }

    /**
     * Returns the person service based on the actual application context.
     *
     * @return - person service
     */
    private PersonService getPersonService() {
        return Context.getPersonService();
    }

    /**
     * Returns supported relationship types
     * @return - list of types
     */
    private List<RelationshipType> getRelationshipTypes() {
        List<RelationshipType> result = new ArrayList<RelationshipType>();
        String config = getPossibleRelationshipTypesGP();
        if (StringUtils.isNotBlank(config)) {
            for (String type : config.split(COMMA)) {
                result.add(getPersonService().getRelationshipTypeByUuid(type));
            }
        } else {
            result = getPersonService().getAllRelationshipTypes();
        }
        return result;
    }
    
    /**
     * Returns map of relationship types names and its relationship types uuids with appropriate suffixes
     * @param relationshipTypes - supported relationship types
     * @return - map of relationship types names and uuids
     */
    private Map<String, String> sortRelationshipNamesAlphabetically(List<RelationshipType> relationshipTypes) {
        TreeMap<String, String> relationshipTypesMap = new TreeMap<String, String>();
        for (RelationshipType relationshipType : relationshipTypes) {
            relationshipTypesMap.put(relationshipType.getaIsToB(), relationshipType.getUuid() + PERSON_A_SUFFIX);
            if (!relationshipType.getbIsToA().equals(relationshipType.getaIsToB())) {
                relationshipTypesMap.put(relationshipType.getbIsToA(), relationshipType.getUuid() + PERSON_B_SUFFIX);
            }
        }
        return relationshipTypesMap;
    }

    /**
     * Returns the value of global property
     * @return - relationship types setting
     */
    private String getPossibleRelationshipTypesGP() {
        return Context.getAdministrationService().getGlobalProperty(CFLConstants.POSSIBLE_RELATIONSHIP_TYPES_KEY);
    }
}
