Feature: Admin adds a new user account

  Scenario Outline: Add a new user
    Given the admin is logged in
    When he open the Add User form
    And selects user role "<userRole>"
    And selects status "<status>"
    And enters employee name "<employeeName>"
    And enters his username "<username>"
    And enters his password "<password>"
    And confirms his the password "<confirmPassword>"
    And clicks on the save button
    Then the new user should be added "<expectedResult>"

  Examples:
    | userRole | status  | employeeName | username | password     | confirmPassword |expectedResult         |
    | ESS      | Enabled | Megan Ortiz | meganOrtiz  | meganO12345 | meganO12345   | User Management         |