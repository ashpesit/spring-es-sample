package com.saisonomni.poc;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SearchStepDefinition {
    @Given("A valid Applicant name of a food truck permit")
    public void aValidApplicantNameOfAFoodTruckPermit() {
    }

    @When("Request to fetch all records by applicant name is made")
    public void requestToFetchAllRecordsByApplicantNameIsMade() {

    }

    @Then("List of document of that applicant is received")
    public void listOfDocumentOfThatApplicantIsReceived() {

    }

    @When("Request to search all food truck permit that are expired is made")
    public void requestToSearchAllFoodTruckPermitThatAreExpiredIsMade() {

    }

    @Then("List of document of expired permit is received")
    public void listOfDocumentOfExpiredPermitIsReceived() {
    }

    @When("Request to search all food truck permit that are not expired is made")
    public void requestToSearchAllFoodTruckPermitThatAreNotExpiredIsMade() {
    }

    @Then("List of document of non expired permit is received")
    public void listOfDocumentOfNonExpiredPermitIsReceived() {
    }

    @When("Request to search by street name is made")
    public void requestToSearchByStreetNameIsMade() {
    }

    @Then("List of document of food facility on that street is received")
    public void listOfDocumentOfFoodFacilityOnThatStreetIsReceived() {
    }

    @Given("A valid value of latitude and longitude")
    public void aValidValueOfLatitudeAndLongitude() {
    }

    @When("Request to search the nearest food truck is made")
    public void requestToSearchTheNearestFoodTruckIsMade() {
    }

    @Then("List of {int} trucks closest to that location is received")
    public void listOfTrucksClosestToThatLocationIsReceived(int arg0) {
    }
}
