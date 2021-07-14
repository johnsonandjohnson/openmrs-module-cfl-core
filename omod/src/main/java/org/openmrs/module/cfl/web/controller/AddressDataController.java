package org.openmrs.module.cfl.web.controller;

import org.apache.commons.collections.CollectionUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.addresshierarchy.AddressHierarchyEntry;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.addresshierarchy.util.AddressHierarchyImportUtil;
import org.openmrs.module.cfl.web.dto.AddressDataDTO;
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

import java.util.ArrayList;
import java.util.List;

/**
 * The CFL Address Data REST Controller
 */
@Controller("cfl.addressDataController")
@RequestMapping("/cfl/address-data")
public class AddressDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddressDataController.class);

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<AddressDataDTO> getAddressList(PageableParams pageableParams) {
        List<List<String>> resultList = getAddressDataResults();

        PagingInfo pagingInfo = getPagingInfo(pageableParams, resultList.size());

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
                                     @RequestParam(value = "delimiter", defaultValue = ",") String delimiter,
                                     @RequestParam(value = "userGeneratedIdDelimiter", required = false)
                                       String userGeneratedIdDelimiter,
                                     @RequestParam(value = "overwrite", required = false) Boolean overwrite) {

        try {
            if (overwrite != null && overwrite) {
                Context.getService(AddressHierarchyService.class).deleteAllAddressHierarchyEntries();
            }

            AddressHierarchyImportUtil.importAddressHierarchyFile(file.getInputStream(), delimiter,
                    userGeneratedIdDelimiter);
            return new ResponseEntity<>(file.getName() + " uploaded successfully", HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Unable to import file. " + e.getMessage());
            return new ResponseEntity<>("Sorry, your file couldn't be uploaded", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private List<List<String>> getAddressDataResults() {
        AddressHierarchyService addressService = Context.getService(AddressHierarchyService.class);
        List<List<String>> resultList = new ArrayList<>();
        List<AddressHierarchyEntry> rootEntries = addressService.getAddressHierarchyEntriesAtTopLevel();
        for (AddressHierarchyEntry entry : rootEntries) {
            processData(addressService, entry, new ArrayList<>(), resultList);
        }

        return resultList;
    }

    private void processData(AddressHierarchyService addressService, AddressHierarchyEntry entry,
                             List<String> stack, List<List<String>> resultList) {
        List<AddressHierarchyEntry> entryChildren = addressService.getChildAddressHierarchyEntries(entry);
        if (CollectionUtils.isEmpty(entryChildren)) {
            List<String> singleElem = new ArrayList<>(stack);
            singleElem.add(entry.getName());
            List<String> singleElemCopy = new ArrayList<>(singleElem);
            resultList.add(singleElemCopy);
            singleElem.clear();
        } else {
            stack.add(entry.getName());
            for (AddressHierarchyEntry child : entryChildren) {
                processData(addressService, child, stack, resultList);
            }
            removeLastElementFromStack(stack);
        }
    }

    private List<List<String>> getPaginatedResults(List<List<String>> resultList, PagingInfo pagingInfo) {
        int startIndex = (pagingInfo.getPage() - 1) * pagingInfo.getPageSize();
        int endIndex = Math.min(pagingInfo.getPage() * pagingInfo.getPageSize(), resultList.size());

        return resultList.subList(startIndex, endIndex);
    }

    private PagingInfo getPagingInfo(PageableParams pageableParams, int allRecordsPageSize) {
        PagingInfo pagingInfo;
        if (pageableParams.hasPageInfo()) {
            pagingInfo = pageableParams.getPagingInfo();
        } else {
            pagingInfo = new PagingInfo(1, allRecordsPageSize);
        }

        return pagingInfo;
    }

    private void removeLastElementFromStack(List<String> stack) {
        stack.remove(stack.size() - 1);
    }

}
