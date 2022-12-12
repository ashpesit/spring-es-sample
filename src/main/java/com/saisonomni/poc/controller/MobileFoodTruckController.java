package com.saisonomni.poc.controller;

import com.saisonomni.poc.elastic.entity.MobileFoodFacility;
import com.saisonomni.poc.exception.RecordNotFoundException;
import com.saisonomni.poc.manager.CrudManager;
import com.saisonomni.poc.manager.SearchManager;
import com.saisonomni.poc.request.ElasticRequestQueries;
import com.saisonomni.poc.response.BaseResponse;
import com.saisonomni.poc.utils.Constants;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/mobile-food-facility/v1")
public class MobileFoodTruckController {

    @Autowired
    public SearchManager searchManager;

    @Autowired
    public CrudManager crudManager;
    @PostMapping
    @ApiOperation(value = "Create and save new records in elastic search", response = BaseResponse.class)
    public ResponseEntity<BaseResponse> saveDocument(@RequestBody MobileFoodFacility mobileFoodFacility){
        try {
            return new ResponseEntity<>(crudManager.createDocument(mobileFoodFacility), HttpStatus.OK);
        } catch (Exception e) {
            log.error(Constants.ERROR_OCCURRED, ExceptionUtils.getStackTrace(e));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse(1,Constants.FAILURE_MESSAGE));
        }
    }
    @GetMapping("/{id}")
    @ApiOperation(value = "Read or get document from elastic search", response = BaseResponse.class)
    public ResponseEntity<BaseResponse> readDocument(@PathVariable("id")String id){
        try {
            return new ResponseEntity<>(crudManager.readDocument(id), HttpStatus.OK);
        }
        catch (RecordNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse(1,e.getMessage()));
        }
        catch (Exception e) {
            log.error(Constants.ERROR_OCCURRED, ExceptionUtils.getStackTrace(e));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse(1,Constants.FAILURE_MESSAGE));
        }
    }
    @PatchMapping
    @ApiOperation(value = "Update a document in elasticsearch database", response = BaseResponse.class)
    public ResponseEntity<BaseResponse> updateDocument(@RequestBody MobileFoodFacility mobileFoodFacility){
        try {
            return new ResponseEntity<>(crudManager.updateDocument(mobileFoodFacility), HttpStatus.OK);
        } catch (Exception e) {
            log.error(Constants.ERROR_OCCURRED, ExceptionUtils.getStackTrace(e));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse(1,Constants.FAILURE_MESSAGE));
        }
    }
    @DeleteMapping("/{id}")
    @ApiOperation(value = "Deletes a record in database", response = BaseResponse.class)
    public ResponseEntity<BaseResponse> deleteDocument(@PathVariable("id")String id){
        try {
            return new ResponseEntity<>(crudManager.deleteDocument(id), HttpStatus.OK);
        } catch (Exception e) {
            log.error(Constants.ERROR_OCCURRED, ExceptionUtils.getStackTrace(e));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse(1,Constants.FAILURE_MESSAGE));
        }
    }

    @PostMapping("/search")
    @ApiOperation(value = "Request for generic search on elasticsearch", response = BaseResponse.class)
    public ResponseEntity<BaseResponse> search(@RequestBody ElasticRequestQueries requestQueries){
        try {
            return new ResponseEntity<>(searchManager.searchMobileFoodFacility(requestQueries), HttpStatus.OK);
        } catch (Exception e) {
            log.error(Constants.ERROR_OCCURRED, ExceptionUtils.getStackTrace(e));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse(1,Constants.FAILURE_MESSAGE));
        }
    }

    @GetMapping("/search/applicant/{applicantName}")
    @ApiOperation("Search by name of applicant")
    public ResponseEntity<BaseResponse> searchByApplicantName(@PathVariable("applicantName")String applicantName,
                                                              @RequestParam(value = "page", required = false, defaultValue = "0") Integer page, @RequestParam(value = "fetch_limit", required = false, defaultValue = "8") Integer fetchLimit){
        try {
            return new ResponseEntity<>(searchManager.searchByApplicantName(applicantName,page,fetchLimit), HttpStatus.OK);
        } catch (Exception e) {
            log.error(Constants.ERROR_OCCURRED, ExceptionUtils.getStackTrace(e));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse(1,Constants.FAILURE_MESSAGE));
        }
    }
    @GetMapping("/search/expired")
    @ApiOperation("Search by expiration date, to find whose permits have expired or is valid")
    public ResponseEntity<BaseResponse> searchExpiredFacility(@RequestParam(value = "v",defaultValue = "true") Boolean expiredFlat,
                                                              @RequestParam(value = "page", required = false, defaultValue = "0") Integer page, @RequestParam(value = "fetch_limit", required = false, defaultValue = "8") Integer fetchLimit){
        try {
            return new ResponseEntity<>(searchManager.searchExpiredFacility(expiredFlat,page,fetchLimit), HttpStatus.OK);
        } catch (Exception e) {
            log.error(Constants.ERROR_OCCURRED, ExceptionUtils.getStackTrace(e));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse(1,Constants.FAILURE_MESSAGE));
        }
    }

    @GetMapping("/search/street")
    @ApiOperation("Search by street name (LIKE search)")
    public ResponseEntity<BaseResponse> searchByStreet(@RequestParam(value = "name",defaultValue = "true") String name,
    @RequestParam(value = "page", required = false, defaultValue = "0") Integer page, @RequestParam(value = "fetch_limit", required = false, defaultValue = "8") Integer fetchLimit){
        try {
            return new ResponseEntity<>(searchManager.searchByStreetNameLike(name,page,fetchLimit), HttpStatus.OK);
        } catch (Exception e) {
            log.error(Constants.ERROR_OCCURRED, ExceptionUtils.getStackTrace(e));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse(1,Constants.FAILURE_MESSAGE));
        }
    }

    @GetMapping("/search/geo-distance")
    @ApiModelProperty("Given a delivery location, find out the closest truck possible.")
    public ResponseEntity<BaseResponse> searchByStreet(@RequestParam(value = "lat") Double lat, @RequestParam(value = "lon") Double lon){
        try {
            return new ResponseEntity<>(searchManager.searchByGeoDistance(lat,lon), HttpStatus.OK);
        } catch (Exception e) {
            log.error(Constants.ERROR_OCCURRED, ExceptionUtils.getStackTrace(e));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse(1,Constants.FAILURE_MESSAGE));
        }
    }
}
