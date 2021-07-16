package org.openmrs.module.cfl.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.addresshierarchy.util.AddressHierarchyImportUtil;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.dto.AddressDataDTO;
import org.openmrs.module.cfl.api.model.AddressDataContent;
import org.openmrs.module.cfl.api.service.AddressDataService;
import org.openmrs.module.cfl.web.model.PageableParams;
import org.openmrs.module.messages.domain.PagingInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * The CFL Address Data REST Controller
 */
@Controller("cfl.addressDataController")
@RequestMapping("/cfl/address-data")
public class AddressDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddressDataController.class);

    private static final String COMMA_DELIMITER = ",";

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<AddressDataDTO> getAddressList(PageableParams pageableParams) {
        List<AddressDataContent> resultList = getAddressDataService().getAddressDataResults();
        PagingInfo pagingInfo = pageableParams.getPagingInfo(resultList.size());

        return new ResponseEntity<>(new AddressDataDTO(
                                        pagingInfo.getPage(),
                                        pagingInfo.getPageSize(),
                                        resultList.size(),
                                        pagingInfo.getPage() * pagingInfo.getPageSize() < resultList.size(),
                                        getPaginatedResults(resultList, pagingInfo)),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> uploadFile(@RequestParam(value = "file") MultipartFile file,
                                     @RequestParam(value = "delimiter", defaultValue = COMMA_DELIMITER) String delimiter,
                                     @RequestParam(value = "userGeneratedIdDelimiter", required = false)
                                       String userGeneratedIdDelimiter,
                                     @RequestParam(value = "overwrite", required = false) Boolean overwrite) {

        try {
            if (Boolean.TRUE.equals(overwrite)) {
                Context.getService(AddressHierarchyService.class).deleteAllAddressHierarchyEntries();
            }

            AddressHierarchyImportUtil.importAddressHierarchyFile(file.getInputStream(), delimiter,
                    userGeneratedIdDelimiter);
            return new ResponseEntity<>(file.getName() + " uploaded successfully", HttpStatus.OK);
        } catch (IOException e) {
            LOGGER.error("Unable to import file. " + e.getMessage());
            return new ResponseEntity<>("Sorry, your file couldn't be uploaded", HttpStatus.BAD_REQUEST);
        }
    }

    private List<AddressDataContent> getPaginatedResults(List<AddressDataContent> resultList, PagingInfo pagingInfo) {
        int startIndex = (pagingInfo.getPage() - 1) * pagingInfo.getPageSize();
        int endIndex = Math.min(pagingInfo.getPage() * pagingInfo.getPageSize(), resultList.size());

        return resultList.subList(startIndex, endIndex);
    }

    private AddressDataService getAddressDataService() {
        return Context.getRegisteredComponent(CFLConstants.CFL_ADDRESS_DATA_SERVICE_BEAN_NAME, AddressDataService.class);
    }
}
