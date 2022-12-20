Feature: Search based on different fields
  Scenario: Search by name of applicant
    Given A valid Applicant name of a food truck permit
    When Request to fetch all records by applicant name is made
    Then List of document of that applicant is received

  Scenario: Search by expiration date, to find whose permits have expired
    When Request to search all food truck permit that are expired is made
    Then List of document of expired permit is received

  Scenario: Search by expiration date, to find whose permits have not expired
    When Request to search all food truck permit that are not expired is made
    Then List of document of non expired permit is received

  Scenario: Search by street name
    Given A valid street name of a food truck
    When Request to search by street name is made
    Then List of document of food facility on that street is received


  Scenario: Given a delivery location, find out the closest truck possible.
    Given A valid value of latitude and longitude
    When Request to search the nearest food truck is made
    Then List of trucks closest to that location is received