/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.handler.metadatasharing;

import org.hamcrest.CoreMatchers;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projection;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.appframework.AppFrameworkActivator;
import org.openmrs.module.appframework.domain.ComponentState;
import org.openmrs.module.appframework.domain.ComponentType;
import org.openmrs.module.appframework.domain.UserApp;
import org.openmrs.test.BaseContextMockTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ComponentStateHandlerTest extends BaseContextMockTest {
  @Mock
  private DbSessionFactory dbSessionFactory;

  @Mock
  private DbSession dbSession;

  @Mock
  private AppFrameworkActivator appFrameworkActivator;

  @Before
  public void setupMock() {
    when(dbSessionFactory.getCurrentSession()).thenReturn(dbSession);
  }

  @Test
  public void shouldReturnCorrectComponentStateId() {
    // given
    final Integer expectedComponentStateId = 999;
    final ComponentState componentStateWithId = new ComponentState();
    componentStateWithId.setComponentStateId(expectedComponentStateId);

    final ComponentStateHandler handler = new ComponentStateHandler();

    // when
    final Integer actualComponentStateId = handler.getId(componentStateWithId);

    // then
    assertEquals(expectedComponentStateId, actualComponentStateId);
  }

  @Test
  public void shouldReturnCorrectComponentStateUUID() {
    // given
    final String componentStateUUID = "componentStateUUID";
    final ComponentState componentStateWithName = new ComponentState();
    componentStateWithName.setUuid(componentStateUUID);

    final ComponentStateHandler handler = new ComponentStateHandler();

    // when
    final String componentStateUuid = handler.getUuid(componentStateWithName);

    // then
    assertEquals(componentStateUUID, componentStateUuid);
  }

  @Test
  public void shouldReturnCorrectName() {
    // given
    final String componentStateName = "componentStateName";
    final ComponentState componentStateWithName = new ComponentState();
    componentStateWithName.setComponentId(componentStateName);

    final ComponentStateHandler handler = new ComponentStateHandler();

    // when
    final String actualName = handler.getName(componentStateWithName);

    // then
    assertEquals(componentStateName, actualName);
  }

  @Test
  public void shouldSaveNewComponentStateWithFlushAndContextRefresh() {
    // given
    final ComponentState newComponentState = new ComponentState();
    newComponentState.setComponentId("newComponentState");
    newComponentState.setEnabled(true);
    newComponentState.setComponentType(ComponentType.APP);
    newComponentState.setUuid("UUID");

    final Criteria criteriaMock = mock(Criteria.class);
    when(criteriaMock.add(any(Criterion.class))).thenReturn(criteriaMock);
    when(criteriaMock.uniqueResult()).thenReturn(null);

    when(dbSession.createCriteria(ComponentState.class)).thenReturn(criteriaMock);

    final ComponentStateHandler handler = new ComponentStateHandler(appFrameworkActivator);
    handler.setSessionFactory(dbSessionFactory);

    // when
    handler.saveItem(newComponentState);

    // then
    final ArgumentCaptor<ComponentState> argumentCaptor = ArgumentCaptor.forClass(ComponentState.class);
    verify(dbSession).saveOrUpdate(argumentCaptor.capture());
    verify(dbSession, times(1)).flush();
    verify(appFrameworkActivator, times(1)).contextRefreshed();

    final ComponentState actualNewComponentState = argumentCaptor.getValue();
    assertEquals(newComponentState.getComponentId(), actualNewComponentState.getComponentId());
    assertEquals(newComponentState.getEnabled(), actualNewComponentState.getEnabled());
    assertEquals(newComponentState.getComponentType(), actualNewComponentState.getComponentType());
    assertEquals(newComponentState.getUuid(), actualNewComponentState.getUuid());
  }

  @Test
  public void shouldUpdateExistingComponentState() {
    // given
    final ComponentState existingComponentState = new ComponentState();
    existingComponentState.setComponentStateId(1);
    existingComponentState.setComponentId("myComponentState");
    existingComponentState.setEnabled(false);
    final ComponentState newComponentState = new ComponentState();
    newComponentState.setComponentId("myComponentState");
    newComponentState.setEnabled(true);
    newComponentState.setComponentType(ComponentType.APP);
    newComponentState.setUuid("UUID");

    final Criteria criteriaMock = mock(Criteria.class);
    when(criteriaMock.add(any(Criterion.class))).thenReturn(criteriaMock);
    when(criteriaMock.uniqueResult()).thenReturn(existingComponentState);

    when(dbSession.createCriteria(ComponentState.class)).thenReturn(criteriaMock);

    final ComponentStateHandler handler = new ComponentStateHandler(appFrameworkActivator);
    handler.setSessionFactory(dbSessionFactory);

    // when
    handler.saveItem(newComponentState);

    // then
    final ArgumentCaptor<ComponentState> argumentCaptor = ArgumentCaptor.forClass(ComponentState.class);
    verify(dbSession).saveOrUpdate(argumentCaptor.capture());
    verify(dbSession, times(1)).flush();
    verify(appFrameworkActivator, times(1)).contextRefreshed();

    final ComponentState actualNewComponentState = argumentCaptor.getValue();
    assertEquals(existingComponentState.getComponentStateId(), actualNewComponentState.getComponentStateId());
    assertEquals(existingComponentState.getComponentId(), actualNewComponentState.getComponentId());
    assertEquals(newComponentState.getEnabled(), actualNewComponentState.getEnabled());
    assertEquals(newComponentState.getComponentType(), actualNewComponentState.getComponentType());
    assertEquals(newComponentState.getUuid(), actualNewComponentState.getUuid());
  }

  @Test
  public void shouldReturnCorrectCountOfComponentStates() {
    // given
    final int expectedCount = 12;

    final Criteria criteriaMock = mock(Criteria.class);
    when(criteriaMock.setProjection(any(Projection.class))).thenReturn(criteriaMock);
    when(criteriaMock.uniqueResult()).thenReturn(expectedCount);

    when(dbSession.createCriteria(ComponentState.class)).thenReturn(criteriaMock);

    final ComponentStateHandler handler = new ComponentStateHandler();
    handler.setSessionFactory(dbSessionFactory);

    // when
    final int actualCount = handler.getItemsCount(ComponentState.class, false, null);

    // then
    assertEquals(expectedCount, actualCount);
  }

  @Test
  public void shouldReturnAllComponentStates() {
    // given
    final ComponentState componentStateA = new ComponentState();
    componentStateA.setComponentStateId(1);
    componentStateA.setComponentId("A");
    final ComponentState componentStateB = new ComponentState();
    componentStateB.setComponentStateId(2);
    componentStateB.setComponentId("B");
    final List<ComponentState> allComponentStates = Arrays.asList(componentStateA, componentStateB);

    final Criteria criteriaMock = mock(Criteria.class);
    when(criteriaMock.list()).thenReturn(allComponentStates);

    when(dbSession.createCriteria(ComponentState.class)).thenReturn(criteriaMock);

    final ComponentStateHandler handler = new ComponentStateHandler();
    handler.setSessionFactory(dbSessionFactory);

    // when
    final List<ComponentState> actualAllItems = handler.getItems(ComponentState.class, false, null, null, null);

    // then
    assertEquals(allComponentStates.size(), actualAllItems.size());
    assertThat(actualAllItems, CoreMatchers.hasItems(allComponentStates.toArray(new ComponentState[0])));
  }

  @Test
  public void shouldReturnPaginatedComponentStates() {
    // given
    final ComponentState componentStateA = new ComponentState();
    componentStateA.setComponentStateId(1);
    componentStateA.setComponentId("A");
    final ComponentState componentStateB = new ComponentState();
    componentStateB.setComponentStateId(2);
    componentStateB.setComponentId("B");
    final List<ComponentState> allComponentStates = Arrays.asList(componentStateA, componentStateB);

    final Criteria criteriaMock = mock(Criteria.class);
    when(criteriaMock.list()).thenReturn(allComponentStates);
    when(criteriaMock.setFirstResult(any(Integer.class))).thenReturn(criteriaMock);

    when(dbSession.createCriteria(ComponentState.class)).thenReturn(criteriaMock);

    final ComponentStateHandler handler = new ComponentStateHandler();
    handler.setSessionFactory(dbSessionFactory);

    // when
    final List<ComponentState> paginatedItems = handler.getItems(ComponentState.class, false, null, 1, 20);

    // then
    assertEquals(2, paginatedItems.size());
    verify(criteriaMock, times(1)).setFirstResult(1);
    verify(criteriaMock, times(1)).setMaxResults(20);
  }

  @Test
  public void shouldReturnComponentStateById() {
    // given
    final ComponentState componentStateA = new ComponentState();
    componentStateA.setComponentStateId(1);
    componentStateA.setComponentId("A");
    final ComponentState componentStateB = new ComponentState();
    componentStateB.setComponentStateId(2);
    componentStateB.setComponentId("B");
    final List<ComponentState> allComponentStates = Arrays.asList(componentStateA, componentStateB);

    final Criteria criteriaMock = mock(Criteria.class);
    when(criteriaMock.list()).thenReturn(allComponentStates);
    when(criteriaMock.uniqueResult()).thenReturn(componentStateA);
    when(criteriaMock.add(any(Criterion.class))).thenReturn(criteriaMock);

    when(dbSession.createCriteria(ComponentState.class)).thenReturn(criteriaMock);

    final ComponentStateHandler handler = new ComponentStateHandler();
    handler.setSessionFactory(dbSessionFactory);

    // when
    final ComponentState actualById = handler.getItemById(ComponentState.class, componentStateA.getComponentStateId());

    // then
    assertEquals(componentStateA, actualById);
    final ArgumentCaptor<Criterion> argumentCaptor = ArgumentCaptor.forClass(Criterion.class);
    verify(criteriaMock).add(argumentCaptor.capture());
    final Criterion actualCriterion = argumentCaptor.getValue();
    assertEquals("componentStateId=1", actualCriterion.toString());
  }

  @Test
  public void shouldReturnComponentStateByUUID() {
    // given
    final ComponentState componentStateA = new ComponentState();
    componentStateA.setUuid("UUID_A");
    final ComponentState componentStateB = new ComponentState();
    componentStateB.setComponentId("UUID_B");
    final List<ComponentState> allComponentStates = Arrays.asList(componentStateA, componentStateB);

    final Criteria criteriaMock = mock(Criteria.class);
    when(criteriaMock.list()).thenReturn(allComponentStates);
    when(criteriaMock.uniqueResult()).thenReturn(componentStateA);
    when(criteriaMock.add(any(Criterion.class))).thenReturn(criteriaMock);

    when(dbSession.createCriteria(ComponentState.class)).thenReturn(criteriaMock);

    final ComponentStateHandler handler = new ComponentStateHandler();
    handler.setSessionFactory(dbSessionFactory);

    // when
    final ComponentState actualById = handler.getItemByUuid(ComponentState.class, componentStateA.getUuid());

    // then
    assertEquals(componentStateA, actualById);
    final ArgumentCaptor<Criterion> argumentCaptor = ArgumentCaptor.forClass(Criterion.class);
    verify(criteriaMock).add(argumentCaptor.capture());
    final Criterion actualCriterion = argumentCaptor.getValue();
    assertEquals("uuid=UUID_A", actualCriterion.toString());
  }

  @Test
  public void shouldCorrectlyDependOnComponentState() {
    // given
    final ComponentState componentState = new ComponentState();
    componentState.setComponentId("myApp");
    final UserApp dependency = new UserApp();
    dependency.setAppId(componentState.getComponentId());

    final Criteria criteriaMock = mock(Criteria.class);
    when(criteriaMock.list()).thenReturn(Collections.singletonList(dependency));
    when(criteriaMock.add(any(Criterion.class))).thenReturn(criteriaMock);

    // UserApp criteria
    when(dbSession.createCriteria(UserApp.class)).thenReturn(criteriaMock);

    final ComponentStateHandler handler = new ComponentStateHandler();
    handler.setSessionFactory(dbSessionFactory);

    // when
    final List<Object> dependencies = handler.getPriorityDependencies(componentState);

    // then
    assertThat(dependencies, CoreMatchers.hasItems((Object) dependency));
    final ArgumentCaptor<Criterion> argumentCaptor = ArgumentCaptor.forClass(Criterion.class);
    verify(criteriaMock).add(argumentCaptor.capture());
    final Criterion actualCriterion = argumentCaptor.getValue();
    assertEquals("appId=myApp", actualCriterion.toString());
  }
}
