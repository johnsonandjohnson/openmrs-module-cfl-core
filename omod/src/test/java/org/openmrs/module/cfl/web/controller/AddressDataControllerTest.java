package org.openmrs.module.cfl.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.api.context.Context;
import org.openmrs.module.addresshierarchy.AddressHierarchyEntry;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.addresshierarchy.util.AddressHierarchyImportUtil;
import org.openmrs.module.cfl.web.dto.AddressDataDTO;
import org.openmrs.module.cfl.web.model.PageableParams;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {Context.class, AddressHierarchyImportUtil.class} )
public class AddressDataControllerTest {

    @Mock
    private AddressHierarchyService addressHierarchyService;

    @InjectMocks
    private AddressDataController addressDataController;

    @Mock
    private MultipartFile multipartFile;

    @Before
    public void setUp() {
        mockStatic(Context.class);
        mockStatic(AddressHierarchyImportUtil.class);
        when(Context.getService(AddressHierarchyService.class)).thenReturn(addressHierarchyService);
    }

    @Test
    public void getAddressList_shouldReturnEmptyResultsWithDefaultPagination() {
        ResponseEntity<AddressDataDTO> response = addressDataController.getAddressList(new PageableParams());

        verify(addressHierarchyService, times(1)).getAddressHierarchyEntriesAtTopLevel();
        verify(addressHierarchyService, never()).getChildAddressHierarchyEntries(any(AddressHierarchyEntry.class));
        assertEquals(200, response.getStatusCode().value());
        assertEquals(0, response.getBody().getContent().size());
        assertEquals(0, response.getBody().getTotalCount());
        assertEquals(1, response.getBody().getPageNumber());
        assertEquals(0, response.getBody().getPageSize());
    }

    @Test
    public void getAddressList_shouldReturnAllResultsWithDefaultPagination() {
        buildTestData();

        ResponseEntity<AddressDataDTO> response = addressDataController.getAddressList(new PageableParams());

        verify(addressHierarchyService, times(1)).getAddressHierarchyEntriesAtTopLevel();
        assertEquals(200, response.getStatusCode().value());
        assertEquals(5, response.getBody().getContent().size());
        assertEquals(5, response.getBody().getTotalCount());
        assertEquals(1, response.getBody().getPageNumber());
        assertEquals(5, response.getBody().getPageSize());
        for (List<String> list : response.getBody().getContent()) {
            assertEquals(5, list.size());
        }
    }

    @Test
    public void getAddressList_shouldReturnThreeResults() {
        buildTestData();
        PageableParams pageableParams = buildPaginationParams(1, 3);

        ResponseEntity<AddressDataDTO> response = addressDataController.getAddressList(pageableParams);

        verify(addressHierarchyService, times(1)).getAddressHierarchyEntriesAtTopLevel();
        assertEquals(200, response.getStatusCode().value());
        assertEquals(3, response.getBody().getContent().size());
        assertEquals(5, response.getBody().getTotalCount());
        assertEquals(1, response.getBody().getPageNumber());
        assertEquals(3, response.getBody().getPageSize());
    }

    @Test
    public void getAddressList_shouldReturnAllResultsWhenPageSizeIsGreaterThanResultSize() {
        buildTestData();
        PageableParams pageableParams = buildPaginationParams(1, 100);

        ResponseEntity<AddressDataDTO> response = addressDataController.getAddressList(pageableParams);

        verify(addressHierarchyService, times(1)).getAddressHierarchyEntriesAtTopLevel();
        assertEquals(200, response.getStatusCode().value());
        assertEquals(5, response.getBody().getContent().size());
        assertEquals(5, response.getBody().getTotalCount());
        assertEquals(1, response.getBody().getPageNumber());
        assertEquals(100, response.getBody().getPageSize());
    }

    @Test
    public void uploadFile_shouldReturnStatus500WhenFileIsNull() {
        ResponseEntity<String> response = addressDataController.uploadFile(null, ",",
                null, false);

        verifyStatic(times(0));
        AddressHierarchyImportUtil.importAddressHierarchyFile(any(), any(), any());
        assertEquals(500, response.getStatusCode().value());
        assertEquals("Sorry, your file couldn't be uploaded", response.getBody());
    }

    @Test
    public void uploadFile_shouldReturnStatusOKWhenFileSuccessfullyUploaded() {
        ResponseEntity<String> response = addressDataController.uploadFile(multipartFile, ",",
                null, null);

        assertEquals(200, response.getStatusCode().value());
        verifyStatic(times(1));
        AddressHierarchyImportUtil.importAddressHierarchyFile(any(), any(), any());
    }

    @Test
    public void uploadFile_shouldNotRemoveOldEntriesAndReturnStatusOKWhenOverwriteFieldIsFalse() {
        ResponseEntity<String> response = addressDataController.uploadFile(multipartFile, ",",
                null, false);

        assertEquals(200, response.getStatusCode().value());
        verifyStatic(times(1));
        AddressHierarchyImportUtil.importAddressHierarchyFile(any(), any(), any());
        verify(addressHierarchyService, times(0)).deleteAllAddressHierarchyEntries();
    }

    @Test
    public void uploadFile_shouldRemoveOldEntriesAndReturnStatusOKWhenOverwriteFieldIsTrue() {
        ResponseEntity<String> response = addressDataController.uploadFile(multipartFile, ",",
                null, true);

        assertEquals(200, response.getStatusCode().value());
        verifyStatic(times(1));
        AddressHierarchyImportUtil.importAddressHierarchyFile(any(), any(), any());
        verify(addressHierarchyService, times(1)).deleteAllAddressHierarchyEntries();
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
        when(addressHierarchyService.getChildAddressHierarchyEntries(level5Child1)).thenReturn(Collections.emptyList());
    }

    private PageableParams buildPaginationParams(int pageNumber, int pageSize) {
        PageableParams params = new PageableParams();
        params.setPage(pageNumber);
        params.setRows(pageSize);
        return params;
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
