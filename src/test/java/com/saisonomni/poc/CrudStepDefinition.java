package com.saisonomni.poc;

import com.saisonomni.poc.elastic.entity.MobileFoodFacility;
import com.saisonomni.poc.response.CustomCrudResponse;
import com.saisonomni.poc.response.CustomSearchResponse;
import com.saisonomni.poc.utils.Constants;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
@Log4j2
@RequiredArgsConstructor
public class CrudStepDefinition {

    private static final String END_POINT = "/poc/mobile-food-facility/v1";

    private final ScenarioContext scenarioContext;

    @Before("@create")
    public void beforeScenario() {
        MobileFoodFacility obj = StringContextLoader.readObjectFromFile("food-truck.json", MobileFoodFacility.class);
        assertNotNull(obj);
        Map<String,String> headers=new HashMap<>();
        headers.put(Constants.CONTENT_TYPE,Constants.APPLICATION_JSON);
        headers.put(Constants.ACCEPT,Constants.APPLICATION_JSON);
        CustomCrudResponse response = RestAssuredUtil.execute(obj, END_POINT, new HashMap<>(), headers, CustomCrudResponse.class, Constants.POST);
        assertNotNull(response);
        assertNotNull(response.getId());
        scenarioContext.setId(response.getId());
    }

    @After("@delete")
    public void afterScenario() {
        assertNotNull(scenarioContext.getId());
        String endPoint=END_POINT+'/'+scenarioContext.getId();
        Map<String,String> headers=new HashMap<>();
        headers.put(Constants.CONTENT_TYPE,Constants.APPLICATION_JSON);
        headers.put(Constants.ACCEPT,Constants.APPLICATION_JSON);
        CustomCrudResponse response = RestAssuredUtil.execute(null, endPoint, new HashMap<>(), headers, CustomCrudResponse.class, Constants.DELETE);
        assertNotNull(response);
        assertEquals(Constants.SUCCESS_CODE,response.getErrorStatus());
    }

    @Given("Details about the new food facility")
    public void detailsAboutTheNewFoodFacility() {
        MobileFoodFacility obj = StringContextLoader.readObjectFromFile("food-truck.json", MobileFoodFacility.class);
        assertNotNull(obj);
        assertNotNull(obj.getLocationId());
        scenarioContext.setFoodFacility(obj);
    }

    @When("Request to create a new mobile food facility is made")
    public void requestToCreateANewMobileFoodFacilityIsMade() {
        MobileFoodFacility foodFacilityRequest = scenarioContext.getFoodFacility();
        Map<String,String> headers=new HashMap<>();
        headers.put(Constants.CONTENT_TYPE,Constants.APPLICATION_JSON);
        headers.put(Constants.ACCEPT,Constants.APPLICATION_JSON);
        CustomCrudResponse response = RestAssuredUtil.execute(foodFacilityRequest, END_POINT, new HashMap<>(), headers, CustomCrudResponse.class, Constants.POST);
        assertNotNull(response);
        log.info("response object {}",response);
        scenarioContext.setResponse(response);
    }

    @Then("Success response is received")
    public void successResponseIsReceived() {
        CustomCrudResponse response = (CustomCrudResponse) scenarioContext.getResponse();
        assertEquals(Constants.SUCCESS_CODE,response.getErrorStatus());
    }

    @And("New entry is created in the database")
    public void newEntryIsCreatedInTheDatabase() {
        CustomCrudResponse response = (CustomCrudResponse) scenarioContext.getResponse();
        assertNotNull(response.getId());
        scenarioContext.setId(response.getId());
    }

    @Given("A valid document id of a food facility in database")
    public void aValidDocumentIdOfAFoodFacilityInDatabase() {
        assertNotNull(scenarioContext.getId());
    }

    @When("Request to read a new mobile food facility is made")
    public void requestToReadANewMobileFoodFacilityIsMade() {
        String endPoint=END_POINT+'/'+scenarioContext.getId();
        Map<String,String> headers=new HashMap<>();
        headers.put(Constants.CONTENT_TYPE,Constants.APPLICATION_JSON);
        headers.put(Constants.ACCEPT,Constants.APPLICATION_JSON);
        CustomSearchResponse<?> response = RestAssuredUtil.execute(null, endPoint, new HashMap<>(), headers,CustomSearchResponse.class, Constants.GET);
        assertNotNull(response);
        scenarioContext.setSearchResponse(response);
    }

    @Then("Details of the food facility is received in the response")
    public void detailsOfTheFoodFacilityIsReceivedInTheResponse() {
        CustomSearchResponse<?> response = scenarioContext.getSearchResponse();
        assertNotNull(response.getDocuments());
        assertEquals(1,response.getDocuments().size());
        Map<String,String> ob= (Map<String, String>) response.getDocuments().get(0);
        assertEquals(scenarioContext.getId(),ob.get("id"));

    }


    @Given("A valid document id and the information to be updated")
    public void aValidDocumentIdAndTheInformationToBeUpdated() {
        assertNotNull(scenarioContext.getId());
        Map<String,String> obj = StringContextLoader.readObjectFromFile("food-truck-update.json", Map.class);
        assertNotNull(obj);
        assertTrue(MapUtils.isNotEmpty(obj));
        scenarioContext.setGenericMapObject(obj);
    }

    @When("Request to update the database entry is made")
    public void requestToUpdateTheDatabaseEntryIsMade() {
        Map<String,String> map=scenarioContext.getGenericMapObject();
        map.put("id", scenarioContext.getId());
        Map<String,String> headers=new HashMap<>();
        headers.put(Constants.CONTENT_TYPE,Constants.APPLICATION_JSON);
        headers.put(Constants.ACCEPT,Constants.APPLICATION_JSON);
        CustomCrudResponse response = RestAssuredUtil.execute(map, END_POINT, new HashMap<>(), headers, CustomCrudResponse.class, Constants.PATCH);
        assertNotNull(response);
        scenarioContext.setResponse(response);
    }

    @And("Entry in the database in updated with the new details")
    public void entryInTheDatabaseInUpdatedWithTheNewDetails() {
        String endPoint=END_POINT+'/'+scenarioContext.getId();
        Map<String,String> headers=new HashMap<>();
        headers.put(Constants.CONTENT_TYPE,Constants.APPLICATION_JSON);
        headers.put(Constants.ACCEPT,Constants.APPLICATION_JSON);
        CustomSearchResponse<?> response = RestAssuredUtil.execute(null, endPoint, new HashMap<>(), headers,CustomSearchResponse.class, Constants.GET);
        assertNotNull(response);
        assertNotNull(response.getDocuments());
        assertEquals(1,response.getDocuments().size());
        Map<String,String> ob= (Map<String, String>) response.getDocuments().get(0);
        assertEquals(scenarioContext.getId(),ob.get("id"));
        assertEquals(scenarioContext.getGenericMapObject().get("permit"),ob.get("permit"));
    }

    @When("Request to delete the document is received")
    public void requestToDeleteTheDocumentIsReceived() {
        assertNotNull(scenarioContext.getId());
        String endPoint=END_POINT+'/'+scenarioContext.getId();
        Map<String,String> headers=new HashMap<>();
        headers.put(Constants.CONTENT_TYPE,Constants.APPLICATION_JSON);
        headers.put(Constants.ACCEPT,Constants.APPLICATION_JSON);
        CustomCrudResponse response = RestAssuredUtil.execute(null, endPoint, new HashMap<>(), headers, CustomCrudResponse.class, Constants.DELETE);
        scenarioContext.setResponse(response);
    }

    @And("Entry for the database is removed")
    public void entryForTheDatabaseIsRemoved() {
        CustomCrudResponse response= (CustomCrudResponse) scenarioContext.getResponse();
        assertNotNull(response);
        assertEquals(Constants.SUCCESS_CODE,response.getErrorStatus());
    }
}
