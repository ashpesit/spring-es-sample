package com.saisonomni.poc;

import com.saisonomni.poc.response.CustomSearchResponse;
import com.saisonomni.poc.utils.Constants;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
@Log4j2
@RequiredArgsConstructor
public class SearchStepDefinition {
    private final ScenarioContext scenarioContext;
    private static final String END_POINT = "/poc/mobile-food-facility/v1/search";

    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy H:mm:ss");

    @Given("A valid Applicant name of a food truck permit")
    public void aValidApplicantNameOfAFoodTruckPermit() {
        Map<String, String> obj = StringContextLoader.readObjectFromFile("search.json", Map.class);
        assertTrue(MapUtils.isNotEmpty(obj));
        assertTrue(obj.containsKey("Applicant"));
        scenarioContext.setGenericMapObject(obj);
    }

    @When("Request to fetch all records by applicant name is made")
    public void requestToFetchAllRecordsByApplicantNameIsMade() {
        Map<String, String> map = scenarioContext.getGenericMapObject();
        String endPoint = END_POINT + "/applicant/" + map.get("Applicant");
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.ACCEPT, Constants.APPLICATION_JSON);
        Map<String, String> params = new HashMap<>();
        params.put("page", "0");
        params.put("fetch_limit", "2");
        CustomSearchResponse<?> response = RestAssuredUtil.execute(null, endPoint, params, headers, CustomSearchResponse.class, Constants.GET);
        assertNotNull(response);
        scenarioContext.setSearchResponse(response);
    }

    @Then("List of document of that applicant is received")
    public void listOfDocumentOfThatApplicantIsReceived() {
        CustomSearchResponse<?> response = scenarioContext.getSearchResponse();
        assertTrue(CollectionUtils.isNotEmpty(response.getDocuments()));
        Map<String, String> mapResponse = (Map<String, String>) response.getDocuments().get(0);
        assertTrue(MapUtils.isNotEmpty(mapResponse));
        Map<String, String> map = scenarioContext.getGenericMapObject();
        assertEquals(map.get("Applicant"), mapResponse.get("Applicant"));
    }

    @When("Request to search all food truck permit that are expired is made")
    public void requestToSearchAllFoodTruckPermitThatAreExpiredIsMade() {
        Map<String, String> map = scenarioContext.getGenericMapObject();
        String endPoint = END_POINT + "/expired";
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.ACCEPT, Constants.APPLICATION_JSON);
        Map<String, String> params = new HashMap<>();
        params.put("v", "true");
        params.put("page", "0");
        params.put("fetch_limit", "2");
        CustomSearchResponse<?> response = RestAssuredUtil.execute(null, endPoint, params, headers, CustomSearchResponse.class, Constants.GET);
        assertNotNull(response);
        scenarioContext.setSearchResponse(response);
    }

    @Then("List of document of expired permit is received")
    public void listOfDocumentOfExpiredPermitIsReceived() {
        CustomSearchResponse<?> response = scenarioContext.getSearchResponse();
        assertTrue(CollectionUtils.isNotEmpty(response.getDocuments()));
        try {
            for (int i = 0; i < response.getDocuments().size(); i++) {
                Map<String, String> resOb = (Map<String, String>) response.getDocuments().get(i);
                String expiryDate = resOb.get("ExpirationDate");
                Date date = dateFormat.parse(expiryDate);
                assertTrue(date.before(new Date()));
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @When("Request to search all food truck permit that are not expired is made")
    public void requestToSearchAllFoodTruckPermitThatAreNotExpiredIsMade() {
        Map<String, String> map = scenarioContext.getGenericMapObject();
        String endPoint = END_POINT + "/expired";
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.ACCEPT, Constants.APPLICATION_JSON);
        Map<String, String> params = new HashMap<>();
        params.put("v", "false");
        params.put("page", "0");
        params.put("fetch_limit", "2");
        CustomSearchResponse<?> response = RestAssuredUtil.execute(null, endPoint, params, headers, CustomSearchResponse.class, Constants.GET);
        assertNotNull(response);
        scenarioContext.setSearchResponse(response);
    }

    @Then("List of document of non expired permit is received")
    public void listOfDocumentOfNonExpiredPermitIsReceived() {
        CustomSearchResponse<?> response = scenarioContext.getSearchResponse();
        assertTrue(CollectionUtils.isNotEmpty(response.getDocuments()));
        try {
            for (int i = 0; i < response.getDocuments().size(); i++) {
                Map<String, String> resOb = (Map<String, String>) response.getDocuments().get(i);
                String expiryDate = resOb.get("ExpirationDate");
                Date date = dateFormat.parse(expiryDate);
                assertTrue(date.after(new Date()));
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    @Given("A valid street name of a food truck")
    public void aValidStreetNameOfAFoodTruck() {
        Map<String, String> obj = (Map<String, String>) StringContextLoader.readObjectFromFile("search.json", Map.class);
        assertTrue(MapUtils.isNotEmpty(obj));
        assertTrue(obj.containsKey("LocationDescription"));
        scenarioContext.setGenericMapObject(obj);
    }
    @When("Request to search by street name is made")
    public void requestToSearchByStreetNameIsMade() {
        Map<String, String> map = scenarioContext.getGenericMapObject();
        String endPoint = END_POINT + "/street";
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.ACCEPT, Constants.APPLICATION_JSON);
        Map<String, String> params = new HashMap<>();
        params.put("name", map.get("LocationDescription"));
        params.put("page", "0");
        params.put("fetch_limit", "2");
        CustomSearchResponse<?> response = RestAssuredUtil.execute(null, endPoint, params, headers, CustomSearchResponse.class, Constants.GET);
        assertNotNull(response);
        scenarioContext.setSearchResponse(response);
    }

    @Then("List of document of food facility on that street is received")
    public void listOfDocumentOfFoodFacilityOnThatStreetIsReceived() {
        CustomSearchResponse<?> response = scenarioContext.getSearchResponse();
        assertTrue(CollectionUtils.isNotEmpty(response.getDocuments()));
        Map<String, String> mapResponse = (Map<String, String>) response.getDocuments().get(0);
        assertTrue(MapUtils.isNotEmpty(mapResponse));
        Map<String, String> map = scenarioContext.getGenericMapObject();
        assertEquals(map.get("LocationDescription"), mapResponse.get("LocationDescription"));
    }

    @Given("A valid value of latitude and longitude")
    public void aValidValueOfLatitudeAndLongitude() {
        Map<String, String> obj = (Map<String, String>) StringContextLoader.readObjectFromFile("search.json", Map.class);
        assertTrue(MapUtils.isNotEmpty(obj));
        assertTrue(obj.containsKey("Location"));
        scenarioContext.setGenericMapObject(obj);
    }

    @When("Request to search the nearest food truck is made")
    public void requestToSearchTheNearestFoodTruckIsMade() {
        Map<String, String> map = scenarioContext.getGenericMapObject();
        String latLon=map.get("Location");
        String[] latLonArr=latLon.split(",");
        String lat= StringUtils.trim(latLonArr[0]);
        String lon= StringUtils.trim(latLonArr[1]);
        String endPoint = END_POINT + "/geo-distance";
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.ACCEPT, Constants.APPLICATION_JSON);
        Map<String, String> params = new HashMap<>();
        params.put("lat", lat);
        params.put("lon", lon);
        CustomSearchResponse<?> response = RestAssuredUtil.execute(null, endPoint, params, headers, CustomSearchResponse.class, Constants.GET);
        assertNotNull(response);
        scenarioContext.setSearchResponse(response);
    }

    @Then("List of trucks closest to that location is received")
    public void listOfTrucksClosestToThatLocationIsReceived() {
        CustomSearchResponse<?> response = scenarioContext.getSearchResponse();
        assertTrue(CollectionUtils.isNotEmpty(response.getDocuments()));
        assertTrue(CollectionUtils.size(response.getDocuments())>1);
        double distance=-1000000.0;
        for(int i=0;i<response.getDocuments().size();i++){
            Map<String, Object> mapResponse = (Map<String, Object>) response.getDocuments().get(i);
            assertTrue(MapUtils.isNotEmpty(mapResponse));
            assertNotNull(mapResponse.get("Distance"));
            double actualDistanceVal=Double.parseDouble(String.valueOf(mapResponse.get("Distance")));
            log.info("actual {} , previous {}",actualDistanceVal,distance);
            assertTrue(actualDistanceVal>=distance);
            distance=actualDistanceVal;
        }


    }


}
