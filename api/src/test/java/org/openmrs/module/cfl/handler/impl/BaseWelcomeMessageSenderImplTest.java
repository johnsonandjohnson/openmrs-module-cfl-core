package org.openmrs.module.cfl.handler.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.RelationshipType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.api.contract.CountrySetting;
import org.openmrs.module.cfl.api.util.DateUtil;
import org.openmrs.module.messages.api.model.PatientTemplate;
import org.openmrs.module.messages.api.model.ScheduledExecutionContext;
import org.openmrs.module.messages.api.model.ScheduledService;
import org.openmrs.module.messages.api.model.ScheduledServiceGroup;
import org.openmrs.module.messages.api.model.Template;
import org.openmrs.module.messages.api.service.MessagesDeliveryService;
import org.openmrs.module.messages.api.service.MessagingGroupService;
import org.openmrs.module.messages.api.service.PatientTemplateService;
import org.openmrs.module.messages.api.service.TemplateService;
import org.openmrs.module.messages.api.util.BestContactTimeHelper;
import org.openmrs.module.messages.domain.criteria.BaseCriteria;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class, DateUtil.class, BestContactTimeHelper.class})
public class BaseWelcomeMessageSenderImplTest {
    private final TimeZone testTimeZone = TimeZone.getTimeZone("Europe/Warsaw");
    private final Date now = new Date(1620743880000L); // 2021-05-11T14:38:00UTC
    private final Date tomorrow = new Date(1620830280000L); // 2021-05-12T14:38:00UTC
    private final String bestContactTime = "10:00";
    private final Date tomorrowBestContactTime = new Date(1620806400000L); // 2021-05-12T10:00:00UTC
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @Mock
    private AdministrationService administrationService;

    @Mock
    private MessagesDeliveryService messagesDeliveryService;

    @Mock
    private MessagingGroupService messagingGroupService;

    @Mock
    private PatientTemplateService patientTemplateService;

    @Mock
    private PersonService personService;

    @Mock
    private TemplateService templateService;

    @Before
    public void setupMocks() {
        mockStatic(Context.class);

        PowerMockito.stub(PowerMockito.method(DateUtil.class, "getDefaultUserTimezone")).toReturn(testTimeZone);
        PowerMockito.stub(PowerMockito.method(DateUtil.class, "now")).toReturn(now);
        PowerMockito.stub(PowerMockito.method(DateUtil.class, "getTomorrow", TimeZone.class)).toReturn(tomorrow);

        PowerMockito.stub(PowerMockito.method(BestContactTimeHelper.class, "getBestContactTime", Person.class,
                RelationshipType.class)).toReturn(bestContactTime);

        when(Context.getAdministrationService()).thenReturn(administrationService);
        when(messagingGroupService.saveGroup(Mockito.any(ScheduledServiceGroup.class))).then(
                new Answer<ScheduledServiceGroup>() {
                    @Override
                    public ScheduledServiceGroup answer(InvocationOnMock invocation) {
                        final ScheduledServiceGroup scheduledServiceGroup =
                                (ScheduledServiceGroup) invocation.getArguments()[0];
                        scheduledServiceGroup.setId(idGenerator.incrementAndGet());

                        for (final ScheduledService scheduledService : scheduledServiceGroup.getScheduledServices()) {
                            scheduledService.setId(idGenerator.incrementAndGet());
                        }

                        return scheduledServiceGroup;
                    }
                });

        final Template welcomeMessageTemplate = new Template();
        welcomeMessageTemplate.setId(1);
        when(templateService.findOneByCriteria(Mockito.any(BaseCriteria.class))).thenReturn(welcomeMessageTemplate);

        final PatientTemplate welcomeMessagePatientTemplate = new PatientTemplate();
        welcomeMessagePatientTemplate.setTemplate(welcomeMessageTemplate);
        when(patientTemplateService.findOneByCriteria(Mockito.any(BaseCriteria.class))).thenReturn(
                welcomeMessagePatientTemplate);
    }

    @Test
    public void send_shouldScheduleSendingForNow() {
        // Given
        final Patient testPatient = new Patient();
        testPatient.setId(1);

        final CountrySetting testCountrySetting = new CountrySetting();
        testCountrySetting.setAllowOnPatientRegistrationTimeFrom("10:00");
        testCountrySetting.setAllowOnPatientRegistrationTimeTo("18:00");

        final TestWelcomeMessageSenderImpl testWelcomeMessageSender = prepareTestWelcomeMessageSenderImpl();

        // When
        testWelcomeMessageSender.send(testPatient, testCountrySetting);

        // Then
        ArgumentCaptor<ScheduledExecutionContext> argumentCaptor = ArgumentCaptor.forClass(ScheduledExecutionContext.class);
        verify(messagesDeliveryService).scheduleDelivery(argumentCaptor.capture());

        final ScheduledExecutionContext resultScheduledExecutionContext = argumentCaptor.getValue();
        assertThat(resultScheduledExecutionContext.getChannelType(), is("TEST"));
        assertThat(resultScheduledExecutionContext.getActorId(), is(testPatient.getId()));
        assertThat(resultScheduledExecutionContext.getActorType(), is("Patient"));
        assertThat(resultScheduledExecutionContext.getPatientId(), is(testPatient.getId()));
        assertNotNull(resultScheduledExecutionContext.getServiceIdsToExecute());
        assertThat(resultScheduledExecutionContext.getServiceIdsToExecute().size(), is(1));
        assertThat(resultScheduledExecutionContext.getExecutionDate(), is(now));
    }

    @Test
    public void send_shouldScheduleSendingForTomorrowBestContactTime() {
        // Given
        final Patient testPatient = new Patient();
        testPatient.setId(1);

        final CountrySetting testCountrySetting = new CountrySetting();
        testCountrySetting.setAllowOnPatientRegistrationTimeFrom("00:00");
        testCountrySetting.setAllowOnPatientRegistrationTimeTo("00:00");

        final TestWelcomeMessageSenderImpl testWelcomeMessageSender = prepareTestWelcomeMessageSenderImpl();

        // When
        testWelcomeMessageSender.send(testPatient, testCountrySetting);

        // Then
        ArgumentCaptor<ScheduledExecutionContext> argumentCaptor = ArgumentCaptor.forClass(ScheduledExecutionContext.class);
        verify(messagesDeliveryService).scheduleDelivery(argumentCaptor.capture());

        final ScheduledExecutionContext resultScheduledExecutionContext = argumentCaptor.getValue();
        assertThat(resultScheduledExecutionContext.getChannelType(), is("TEST"));
        assertThat(resultScheduledExecutionContext.getActorId(), is(testPatient.getId()));
        assertThat(resultScheduledExecutionContext.getActorType(), is("Patient"));
        assertThat(resultScheduledExecutionContext.getPatientId(), is(testPatient.getId()));
        assertNotNull(resultScheduledExecutionContext.getServiceIdsToExecute());
        assertThat(resultScheduledExecutionContext.getServiceIdsToExecute().size(), is(1));
        assertThat(resultScheduledExecutionContext.getExecutionDate(), is(tomorrowBestContactTime));
    }

    private TestWelcomeMessageSenderImpl prepareTestWelcomeMessageSenderImpl() {
        final TestWelcomeMessageSenderImpl testWelcomeMessageSender = new TestWelcomeMessageSenderImpl("TEST", true);
        testWelcomeMessageSender.setMessagesDeliveryService(messagesDeliveryService);
        testWelcomeMessageSender.setMessagingGroupService(messagingGroupService);
        testWelcomeMessageSender.setPatientTemplateService(patientTemplateService);
        testWelcomeMessageSender.setPersonService(personService);
        testWelcomeMessageSender.setTemplateService(templateService);
        return testWelcomeMessageSender;
    }
}
