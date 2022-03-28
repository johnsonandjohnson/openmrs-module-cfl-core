package org.openmrs.module.cfl.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.api.dto.AddressDataDTO;
import org.openmrs.module.cfl.api.dto.ImportDataResultDTO;
import org.openmrs.module.cfl.api.service.CFLAddressHierarchyService;
import org.openmrs.module.cfl.web.model.PageableParams;
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

/** The CFL Address Data REST Controller */
@Controller("cfl.addressDataController")
@RequestMapping("/cfl/address-data")
public class AddressDataController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AddressDataController.class);

  private static final String DEFAULT_DELIMITER = ",";

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<AddressDataDTO> getAddressList(PageableParams pageableParams) {
    AddressDataDTO resultList =
        Context.getService(CFLAddressHierarchyService.class)
            .getAddressDataResults(pageableParams.getPage(), pageableParams.getRows());

    return new ResponseEntity<>(resultList, HttpStatus.OK);
  }

  @RequestMapping(value = "/upload", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<ImportDataResultDTO> uploadFile(
      @RequestParam(value = "file") MultipartFile file,
      @RequestParam(value = "delimiter", defaultValue = DEFAULT_DELIMITER) String delimiter,
      @RequestParam(value = "userGeneratedIdDelimiter", required = false)
          String userGeneratedIdDelimiter,
      @RequestParam(value = "overwrite", required = false) Boolean overwrite) {

    try {
      ImportDataResultDTO result =
          Context.getService(CFLAddressHierarchyService.class)
              .importAddressHierarchyEntriesAndReturnInvalidRows(
                  file.getInputStream(), delimiter, overwrite);

      return new ResponseEntity<>(result, HttpStatus.OK);
    } catch (IOException ex) {
      LOGGER.error("Unable to import file. " + ex.getMessage());
      return new ResponseEntity<>(new ImportDataResultDTO(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
