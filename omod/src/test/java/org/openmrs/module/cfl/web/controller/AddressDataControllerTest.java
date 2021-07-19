package org.openmrs.module.cfl.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.api.context.Context;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.addresshierarchy.util.AddressHierarchyImportUtil;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.dto.AddressDataDTO;
import org.openmrs.module.cfl.api.model.AddressDataContent;
import org.openmrs.module.cfl.api.service.AddressDataService;
import org.openmrs.module.cfl.web.model.PageableParams;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
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

    @Mock
    private AddressDataService addressDataService;

    @Before
    public void setUp() {
        mockStatic(Context.class);
        mockStatic(AddressHierarchyImportUtil.class);
        when(Context.getService(AddressHierarchyService.class)).thenReturn(addressHierarchyService);
        when(Context.getRegisteredComponent(CFLConstants.CFL_ADDRESS_DATA_SERVICE_BEAN_NAME, AddressDataService.class))
                .thenReturn(addressDataService);
    }

    @Test
    public void getAddressList_shouldReturnAllResultsWithDefaultPagination() {
        when(addressDataService.getAddressDataResults()).thenReturn(buildAddressDataContentList(50));
        ResponseEntity<AddressDataDTO> response = addressDataController.getAddressList(new PageableParams());

        verify(addressDataService, times(1)).getAddressDataResults();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getPageNumber());
        assertEquals(50, response.getBody().getPageSize());
        assertEquals(50, response.getBody().getTotalCount());
        assertFalse(response.getBody().isNextPage());
    }

    @Test
    public void getAddressList_shouldReturnAllResultsWithNullPagination() {
        when(addressDataService.getAddressDataResults()).thenReturn(buildAddressDataContentList(10));
        PageableParams pageableParams = buildPaginationParams(null, null);
        ResponseEntity<AddressDataDTO> response = addressDataController.getAddressList(pageableParams);

        verify(addressDataService, times(1)).getAddressDataResults();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(10, response.getBody().getContent().size());
        assertEquals(1, response.getBody().getPageNumber());
        assertEquals(10, response.getBody().getPageSize());
        assertEquals(10, response.getBody().getTotalCount());
        assertFalse(response.getBody().isNextPage());
    }

    @Test
    public void getAddressList_shouldReturnThreeResultsWithSetPagination() {
        when(addressDataService.getAddressDataResults()).thenReturn(buildAddressDataContentList(10));
        PageableParams pageableParams = buildPaginationParams(1, 3);
        ResponseEntity<AddressDataDTO> response = addressDataController.getAddressList(pageableParams);

        verify(addressDataService, times(1)).getAddressDataResults();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(3, response.getBody().getContent().size());
        assertEquals(1, response.getBody().getPageNumber());
        assertEquals(3, response.getBody().getPageSize());
        assertEquals(10, response.getBody().getTotalCount());
        assertTrue(response.getBody().isNextPage());
    }

    @Test
    public void getAddressList_shouldReturnAllResultsWhenPageSizeIsGreaterThanResultSize() {
        when(addressDataService.getAddressDataResults()).thenReturn(buildAddressDataContentList(10));
        PageableParams pageableParams = buildPaginationParams(1, 100);
        ResponseEntity<AddressDataDTO> response = addressDataController.getAddressList(pageableParams);

        verify(addressDataService, times(1)).getAddressDataResults();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(10, response.getBody().getContent().size());
        assertEquals(1, response.getBody().getPageNumber());
        assertEquals(100, response.getBody().getPageSize());
        assertEquals(10, response.getBody().getTotalCount());
        assertFalse(response.getBody().isNextPage());
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

    private List<AddressDataContent> buildAddressDataContentList(int numberOfElements) {
        List<AddressDataContent> addressDataContentList = new ArrayList<>();
        for (int i = 0; i < numberOfElements; i++) {
            addressDataContentList.add(new AddressDataContent(new ArrayList<>()));
        }
        return addressDataContentList;
    }

    private PageableParams buildPaginationParams(Integer pageNumber, Integer pageSize) {
        PageableParams params = new PageableParams();
        params.setPage(pageNumber);
        params.setRows(pageSize);
        return params;
    }
}
