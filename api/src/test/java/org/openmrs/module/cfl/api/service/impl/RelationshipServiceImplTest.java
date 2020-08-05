package org.openmrs.module.cfl.api.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.module.cfl.api.dto.RelationshipDTO;
import org.openmrs.module.cfl.builder.PersonBuilder;
import org.openmrs.module.cfl.builder.RelationshipBuilder;
import org.openmrs.module.cfl.builder.RelationshipTypeBuilder;
import org.openmrs.module.cfl.mock.MockedPersonService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

public class RelationshipServiceImplTest {

    private static final String PARENT_RELATIONSHIP_TYPE_UUID = "7a2322e0-d2e3-4978-9eab-813f456559d8";
    private static final String PARENT_RELATIONSHIP_TYPE_A_TO_B = "Child";
    private static final String PARENT_RELATIONSHIP_TYPE_B_TO_A = "Parent";
    private static final String PATIENT_UUID = "e02262f6-b965-4b67-b996-30c1178ab434";
    private static final String CAREGIVER_UUID = "54d69fd7-a8ab-495b-8532-a7653e849d13";
    private static final String PARENT_UUID = "9394a1f9-7b2f-4a08-a65f-15e70e5c14c9";
    private static final int EXPECTED_TWO = 2;
    private static final int EXPECTED_ONE = 1;
    private Person patient;
    private Person caregiver;
    private Person parent;
    private Relationship caregiverRelationship;
    private RelationshipType caregiverRelationshipType;
    private Relationship parentRelationship;
    private RelationshipType parentRelationshipType;
    private RelationshipServiceImpl relationshipService;
    private MockedPersonService mockedPersonService;

    @Before
    public void setUp() {
        patient = new PersonBuilder().withUuid(PATIENT_UUID).build();
        caregiver = new PersonBuilder().withUuid(CAREGIVER_UUID).build();
        parent = new PersonBuilder().withUuid(PARENT_UUID).build();
        caregiverRelationshipType = new RelationshipTypeBuilder().build();
        parentRelationshipType = new RelationshipTypeBuilder()
                .withId(null)
                .withUuid(PARENT_RELATIONSHIP_TYPE_UUID)
                .withAIsToB(PARENT_RELATIONSHIP_TYPE_A_TO_B)
                .withBIsToA(PARENT_RELATIONSHIP_TYPE_B_TO_A)
                .build();
        caregiverRelationship = new RelationshipBuilder()
                .withRelationshipType(caregiverRelationshipType)
                .withPersonA(patient)
                .withPersonB(caregiver)
                .build();
        parentRelationship = new RelationshipBuilder()
                .withRelationshipType(parentRelationshipType)
                .withPersonA(patient)
                .withPersonB(parent)
                .build();
        mockedPersonService = new MockedPersonService()
                .withPeople(Arrays.asList(patient, caregiver, parent))
                .withRelationshipTypes(Arrays.asList(caregiverRelationshipType, parentRelationshipType));
        relationshipService = new RelationshipServiceImpl();
        relationshipService.setPersonService(mockedPersonService);
    }

    @Test
    public void shouldUpdateRelationship() {
        List<RelationshipDTO> newRelationship = Arrays.asList(
                new RelationshipDTO()
                        .setType(caregiverRelationshipType.getUuid() + "-B")
                        .setUuid(caregiver.getUuid()),
                new RelationshipDTO()
                        .setType(parentRelationshipType.getUuid() + "-B")
                        .setUuid(parent.getUuid()));
        List<Relationship> actual = relationshipService.updatedRelationships(newRelationship, patient);
        Assert.assertThat(actual.size(), is(EXPECTED_TWO));
    }

    @Test
    public void shouldUpdateRelationshipWithVoiding() {
        mockedPersonService.withRelationships(Arrays.asList(caregiverRelationship, parentRelationship));
        List<RelationshipDTO> newRelationship = Arrays.asList(
                new RelationshipDTO()
                        .setType(caregiverRelationshipType.getUuid() + "-B")
                        .setUuid(caregiver.getUuid()));
        List<Relationship> actual = relationshipService.updatedRelationships(newRelationship, patient);
        Assert.assertThat(actual.size(), is(EXPECTED_ONE));
        Assert.assertThat(actual.get(0).getUuid(), is(caregiverRelationship.getUuid()));
    }
}
