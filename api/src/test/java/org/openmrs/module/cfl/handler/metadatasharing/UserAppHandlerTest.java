package org.openmrs.module.cfl.handler.metadatasharing;

import org.hamcrest.CoreMatchers;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.appframework.AppFrameworkActivator;
import org.openmrs.module.appframework.domain.UserApp;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.test.BaseContextMockTest;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class})
public class UserAppHandlerTest extends BaseContextMockTest {
    @Mock
    private DbSessionFactory dbSessionFactory;

    @Mock
    private DbSession dbSession;

    @Mock
    private AppFrameworkActivator appFrameworkActivator;

    @Mock
    private AppFrameworkService appFrameworkService;

    @Before
    public void setupMock() {
        when(dbSessionFactory.getCurrentSession()).thenReturn(dbSession);
        mockStatic(Context.class);
        when(Context.getService(AppFrameworkService.class)).thenReturn(appFrameworkService);
        when(appFrameworkService.getAllApps()).thenReturn(Collections.emptyList());
        when(appFrameworkService.getUserApps()).thenReturn(Collections.emptyList());
    }

    @Test
    public void shouldReturnCorrectUserAppId() {
        // given
        final String userAppName = "userAppName";
        final Integer expectedUserAppId = userAppName.hashCode();
        final UserApp userAppWithId = new UserApp();
        userAppWithId.setAppId(userAppName);

        final UserAppHandler handler = new UserAppHandler();

        // when
        final Integer userAppId = handler.getId(userAppWithId);

        // then
        assertEquals(expectedUserAppId, userAppId);
    }

    @Test
    public void shouldReturnCorrectUserAppUUID() {
        // given
        final String userAppName = "userAppName";
        final UserApp userAppWithName = new UserApp();
        userAppWithName.setAppId(userAppName);

        final UserAppHandler handler = new UserAppHandler();

        // when
        final String userAppUuid = handler.getUuid(userAppWithName);

        // then
        assertEquals(UUID.nameUUIDFromBytes(userAppName.getBytes()).toString(), userAppUuid);
    }

    @Test
    public void shouldReturnCorrectName() {
        // given
        final String userAppName = "userAppName";
        final UserApp userAppWithName = new UserApp();
        userAppWithName.setAppId(userAppName);

        final UserAppHandler handler = new UserAppHandler();

        // when
        final String actualName = handler.getName(userAppWithName);

        // then
        assertEquals(userAppName, actualName);
    }

    @Test
    public void shouldSaveNewUserAppWithFlushAndContextRefresh() {
        // given
        final UserApp newUserApp = new UserApp();
        newUserApp.setAppId("newUserApp");
        newUserApp.setJson("{}");

        final Criteria criteriaMock = mock(Criteria.class);
        when(criteriaMock.add(any(Criterion.class))).thenReturn(criteriaMock);
        when(criteriaMock.uniqueResult()).thenReturn(null);

        when(dbSession.createCriteria(UserApp.class)).thenReturn(criteriaMock);

        final UserAppHandler handler = new UserAppHandler(appFrameworkActivator);
        handler.setSessionFactory(dbSessionFactory);

        // when
        handler.saveItem(newUserApp);

        // then
        final ArgumentCaptor<UserApp> argumentCaptor = ArgumentCaptor.forClass(UserApp.class);
        verify(dbSession).saveOrUpdate(argumentCaptor.capture());
        verify(dbSession, times(1)).flush();
        verify(appFrameworkActivator, times(1)).contextRefreshed();

        final UserApp actualNewUserApp = argumentCaptor.getValue();
        assertEquals(newUserApp.getAppId(), actualNewUserApp.getAppId());
        assertEquals(newUserApp.getJson(), actualNewUserApp.getJson());
    }

    @Test
    public void shouldUpdateExistingUserApp() {
        // given
        final UserApp existingUserApp = new UserApp();
        existingUserApp.setAppId("myUserApp");
        existingUserApp.setJson("{\"a\": 0}");
        final UserApp newUserApp = new UserApp();
        newUserApp.setAppId(existingUserApp.getAppId());
        newUserApp.setJson("{}");

        final Criteria criteriaMock = mock(Criteria.class);
        when(criteriaMock.add(any(Criterion.class))).thenReturn(criteriaMock);
        when(criteriaMock.uniqueResult()).thenReturn(existingUserApp);

        when(dbSession.createCriteria(UserApp.class)).thenReturn(criteriaMock);

        final UserAppHandler handler = new UserAppHandler(appFrameworkActivator);
        handler.setSessionFactory(dbSessionFactory);

        // when
        handler.saveItem(newUserApp);

        // then
        final ArgumentCaptor<UserApp> argumentCaptor = ArgumentCaptor.forClass(UserApp.class);
        verify(dbSession).saveOrUpdate(argumentCaptor.capture());
        verify(dbSession, times(1)).flush();
        verify(appFrameworkActivator, times(1)).contextRefreshed();

        final UserApp actualNewUserApp = argumentCaptor.getValue();
        assertEquals(existingUserApp.getAppId(), actualNewUserApp.getAppId());
        assertEquals(newUserApp.getJson(), actualNewUserApp.getJson());
    }

    @Test
    public void shouldReturnCorrectCountOfUserApps() {
        // given
        final int expectedCount = 12;

        final Criteria criteriaMock = mock(Criteria.class);
        when(criteriaMock.setProjection(any(Projection.class))).thenReturn(criteriaMock);
        when(criteriaMock.uniqueResult()).thenReturn(expectedCount);

        when(dbSession.createCriteria(UserApp.class)).thenReturn(criteriaMock);

        final UserAppHandler handler = new UserAppHandler();
        handler.setSessionFactory(dbSessionFactory);

        // when
        final int actualCount = handler.getItemsCount(UserApp.class, false, null);

        // then
        assertEquals(expectedCount, actualCount);
    }

    @Test
    public void shouldReturnAllUserApps() {
        // given
        final UserApp userAppA = new UserApp();
        userAppA.setAppId("A");
        final UserApp userAppB = new UserApp();
        userAppB.setAppId("B");
        final List<UserApp> allUserApps = Arrays.asList(userAppA, userAppB);

        final Criteria criteriaMock = mock(Criteria.class);
        when(criteriaMock.list()).thenReturn(allUserApps);

        when(dbSession.createCriteria(UserApp.class)).thenReturn(criteriaMock);

        final UserAppHandler handler = new UserAppHandler();
        handler.setSessionFactory(dbSessionFactory);

        // when
        final List<UserApp> actualAllItems = handler.getItems(UserApp.class, false, null, null, null);

        // then
        assertEquals(allUserApps.size(), actualAllItems.size());
        assertThat(actualAllItems, CoreMatchers.hasItems(allUserApps.toArray(new UserApp[0])));
    }

    @Test
    public void shouldReturnPaginatedUserApps() {
        // given
        final UserApp userAppA = new UserApp();
        userAppA.setAppId("A");
        final UserApp userAppB = new UserApp();
        userAppB.setAppId("B");
        final List<UserApp> allUserApps = Arrays.asList(userAppA, userAppB);

        final Criteria criteriaMock = mock(Criteria.class);
        when(criteriaMock.list()).thenReturn(allUserApps);
        when(criteriaMock.setFirstResult(any(Integer.class))).thenReturn(criteriaMock);

        when(dbSession.createCriteria(UserApp.class)).thenReturn(criteriaMock);

        final UserAppHandler handler = new UserAppHandler();
        handler.setSessionFactory(dbSessionFactory);

        // when
        final List<UserApp> paginatedItems = handler.getItems(UserApp.class, false, null, 1, 20);

        // then
        assertEquals(2, paginatedItems.size());
        verify(criteriaMock, times(1)).setFirstResult(1);
        verify(criteriaMock, times(1)).setMaxResults(20);
    }

    @Test
    public void shouldReturnUserAppById() {
        // given
        final UserApp userAppA = new UserApp();
        userAppA.setAppId("A");
        final UserApp userAppB = new UserApp();
        userAppB.setAppId("B");
        final List<UserApp> allUserApps = Arrays.asList(userAppA, userAppB);

        final Criteria criteriaMock = mock(Criteria.class);
        when(criteriaMock.list()).thenReturn(allUserApps);

        when(dbSession.createCriteria(UserApp.class)).thenReturn(criteriaMock);

        final UserAppHandler handler = new UserAppHandler();
        handler.setSessionFactory(dbSessionFactory);

        // when
        final UserApp actualById = handler.getItemById(UserApp.class, userAppA.getAppId().hashCode());

        // then
        assertEquals(userAppA, actualById);
    }

    @Test
    public void shouldReturnUserAppByUUID() {
        // given
        final String userAppAUUID = UUID.nameUUIDFromBytes("A".getBytes()).toString();
        final UserApp userAppA = new UserApp();
        userAppA.setAppId("A");
        final UserApp userAppB = new UserApp();
        userAppB.setAppId("B");
        final List<UserApp> allUserApps = Arrays.asList(userAppA, userAppB);

        final Criteria criteriaMock = mock(Criteria.class);
        when(criteriaMock.list()).thenReturn(allUserApps);
        when(criteriaMock.uniqueResult()).thenReturn(userAppA);
        when(criteriaMock.add(any(Criterion.class))).thenReturn(criteriaMock);

        when(dbSession.createCriteria(UserApp.class)).thenReturn(criteriaMock);

        final UserAppHandler handler = new UserAppHandler();
        handler.setSessionFactory(dbSessionFactory);

        // when
        final UserApp actualByUuid = handler.getItemByUuid(UserApp.class, userAppAUUID);

        // then
        assertEquals(userAppA, actualByUuid);
    }
}
