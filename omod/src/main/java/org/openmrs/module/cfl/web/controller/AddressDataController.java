package org.openmrs.module.cfl.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
import java.net.HttpURLConnection;

/** The CFL Address Data REST Controller */
@Api(value = "Address data", tags = {"REST API to handle CFL Address data"})
@Controller("cfl.addressDataController")
@RequestMapping("/cfl/address-data")
public class AddressDataController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AddressDataController.class);

  private static final String DEFAULT_DELIMITER = ",";

  @ApiOperation(value = "Get address list", notes = "Get address list")
  @ApiResponses(value = {
          @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful returning address list"),
          @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failure to return address list"),
          @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Error in returning address list")})
  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<AddressDataDTO> getAddressList(
          @ApiParam(name = "pageableParams", value = "The pageable params") PageableParams pageableParams) {
    AddressDataDTO resultList =
        Context.getService(CFLAddressHierarchyService.class)
            .getAddressDataResults(pageableParams.getPage(), pageableParams.getRows());

    return new ResponseEntity<>(resultList, HttpStatus.OK);
  }

  @ApiOperation(value = "Upload file", notes = "Upload file")
  @ApiResponses(value = {
          @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful uploading file"),
          @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failure to upload file"),
          @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Error in uploading file")})
  @RequestMapping(value = "/upload", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<ImportDataResultDTO> uploadFile(
          @ApiParam(name = "file", value = "The multipart file") @RequestParam(value = "file") MultipartFile file,
          @ApiParam(name = "delimiter", value = "The delimiter used in file")
          @RequestParam(value = "delimiter", defaultValue = DEFAULT_DELIMITER) String delimiter,
          @ApiParam(name = "userGeneratedIdDelimiter", value = "The user generated ID delimiter")
          @RequestParam(value = "userGeneratedIdDelimiter", required = false) String userGeneratedIdDelimiter,
          @ApiParam(name = "overwrite", value = "Overwrite boolean value")
          @RequestParam(value = "overwrite", required = false) Boolean overwrite) {
    try {
      ImportDataResultDTO result =
          Context.getService(CFLAddressHierarchyService.class)
              .importAddressHierarchyEntriesAndReturnInvalidRows(
                  file.getInputStream(), delimiter, overwrite);

      return new ResponseEntity<>(result, HttpStatus.OK);
    } catch (IOException ex) {
      LOGGER.error("Unable to import file. " + ex.getMessage());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
