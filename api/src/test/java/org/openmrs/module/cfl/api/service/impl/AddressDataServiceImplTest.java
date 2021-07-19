package org.openmrs.module.cfl.api.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.api.context.Context;
import org.openmrs.module.addresshierarchy.AddressHierarchyEntry;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.cfl.api.model.AddressDataContent;
import org.openmrs.module.cfl.api.service.AddressDataService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {Context.class } )
public class AddressDataServiceImplTest {

    private final AddressDataService addressDataService = new AddressDataServiceImpl();

    @Mock
    private AddressHierarchyService addressHierarchyService;

    @Before
    public void setUp() {
        mockStatic(Context.class);
        when(Context.getService(AddressHierarchyService.class)).thenReturn(addressHierarchyService);
    }

    @Test
    public void getAddressList_shouldReturnEmptyResultsListWhenNoDataAvailable() {
        List<AddressDataContent> results = addressDataService.getAddressDataResults();

        assertNotNull(results);
        assertEquals(0, results.size());
    }

    @Test
    public void getAddressList_shouldReturnAllResultsWithDefaultPagination() {
        buildTestData();

        List<AddressDataContent> results = addressDataService.getAddressDataResults();

        assertNotNull(results);
        assertEquals(5, results.size());

        AddressDataContent firstResult = results.get(0);
        assertEquals("Philipines", firstResult.getContent().get(0));
        assertEquals("Manila", firstResult.getContent().get(1));
        assertEquals("Quezon City", firstResult.getContent().get(2));
        assertEquals("Brgy Random", firstResult.getContent().get(3));
        assertEquals("1100", firstResult.getContent().get(4));

        AddressDataContent lastResult = results.get(4);
        assertEquals("Philipines", lastResult.getContent().get(0));
        assertEquals("Manila", lastResult.getContent().get(1));
        assertEquals("Quezon City", lastResult.getContent().get(2));
        assertEquals("Brgy Random", lastResult.getContent().get(3));
        assertEquals("1104", lastResult.getContent().get(4));
    }

    private void buildTestData() {
        AddressHierarchyLevel rootLevel = buildAddressHierarchyLevel(1, null);
        AddressHierarchyLevel level2 = buildAddressHierarchyLevel(2, rootLevel);
        AddressHierarchyLevel level3 = buildAddressHierarchyLevel(3, level2);
        AddressHierarchyLevel level4 = buildAddressHierarchyLevel(4, level3);
        AddressHierarchyLevel level5 = buildAddressHierarchyLevel(5, level4);

        AddressHierarchyEntry rootEntry = buildAddressHierarchyEntry(1, "Philipines", rootLevel, null);
        AddressHierarchyEntry level2Child1 = buildAddressHierarchyEntry(2, "Manila", level2, rootEntry);
        AddressHierarchyEntry level3Child1 =
                buildAddressHierarchyEntry(3, "Quezon City", level3, level2Child1);
        AddressHierarchyEntry level4Child1 =
                buildAddressHierarchyEntry(4, "Brgy Random", level4, level3Child1);
        AddressHierarchyEntry level5Child1 = buildAddressHierarchyEntry(5, "1100", level5, level4Child1);
        AddressHierarchyEntry level5Child2 = buildAddressHierarchyEntry(6, "1101", level5, level4Child1);
        AddressHierarchyEntry level5Child3 = buildAddressHierarchyEntry(7, "1102", level5, level4Child1);
        AddressHierarchyEntry level5Child4 = buildAddressHierarchyEntry(8, "1103", level5, level4Child1);
        AddressHierarchyEntry level5Child5 = buildAddressHierarchyEntry(9, "1104", level5, level4Child1);

        when(addressHierarchyService.getAddressHierarchyEntriesAtTopLevel()).thenReturn(Arrays.asList(rootEntry));
        when(addressHierarchyService.getChildAddressHierarchyEntries(rootEntry))
                .thenReturn(Arrays.asList(level2Child1));
        when(addressHierarchyService.getChildAddressHierarchyEntries(level2Child1))
                .thenReturn(Arrays.asList(level3Child1));
        when(addressHierarchyService.getChildAddressHierarchyEntries(level3Child1))
                .thenReturn(Arrays.asList(level4Child1));
        when(addressHierarchyService.getChildAddressHierarchyEntries(level4Child1))
                .thenReturn(Arrays.asList(level5Child1, level5Child2, level5Child3, level5Child4, level5Child5));
        when(addressHierarchyService.getChildAddressHierarchyEntries(level5Child1))
                .thenReturn(Collections.emptyList());
    }

    private AddressHierarchyEntry buildAddressHierarchyEntry(Integer entryId, String name, AddressHierarchyLevel level,
                                                             AddressHierarchyEntry parent) {
        AddressHierarchyEntry addressHierarchyEntry = new AddressHierarchyEntry();
        addressHierarchyEntry.setAddressHierarchyEntryId(entryId);
        addressHierarchyEntry.setName(name);
        addressHierarchyEntry.setLevel(level);
        addressHierarchyEntry.setParent(parent);
        return addressHierarchyEntry;
    }

    private AddressHierarchyLevel buildAddressHierarchyLevel(Integer levelId, AddressHierarchyLevel parentLevel) {
        AddressHierarchyLevel addressHierarchyLevel = new AddressHierarchyLevel();
        addressHierarchyLevel.setLevelId(levelId);
        addressHierarchyLevel.setParent(parentLevel);
        return addressHierarchyLevel;
    }
}
