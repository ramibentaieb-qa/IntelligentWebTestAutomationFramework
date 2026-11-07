Feature: Admin adds a new employee account

  Scenario Outline: Add a new employee
    Given the admin is logged in the OHR
    When he opens the Add Employee form
    And types employee's full name "<firstName>", "<middleName>", and "<lastName>"
    And types a unique employee id
    And opens the login details
    And enters employee's username "<username>"
    And chooses the status of the employee
    And enters employee's password "<password>"
    And confirms employee's password "<confirmPassword>"
    And clicks on the save button to add the new employee
    Then the new employee should be added "<expectedResult>"

    Examples:
      | firstName | middleName  | lastName | username  | password     | confirmPassword |expectedResult      |
      | jack      | j           | smith    | jackSmith | jack123      | jack123         | Success |