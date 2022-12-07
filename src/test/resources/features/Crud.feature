Feature: Mobile food facility crud operations
  Scenario: Create new mobile food facility
    Given Details about the new food facility
    When Request to create a new mobile food facility is made
    Then Success response is received
    And New entry is created in the database


  Scenario: Read new mobile food facility
    Given A valid document id of a food facility in database
    When Request to read a new mobile food facility is made
    Then Details of the food facility is received in the response

  Scenario: Update existing food facility details
    Given A valid document id and the information to be updated
    When Request to update the database entry is made
    Then Success response is received
    And Entry in the database in updated with the new details

  Scenario: Delete the entry from the database
    Given A valid document id of a food facility in database
    When Request to delete the document is received
    Then Success response is received
    And Entry for the database is removed