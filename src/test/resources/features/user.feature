Feature: Admin adds a new user account

  Scenario: Add a ne user
    Given then admin is logged in
    When he open the Add User form
    And fills the new user infomration
    And clicks the save button
    Then a new user is added