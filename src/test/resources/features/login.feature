Feature: As an admin I want to log in the system so that I can access to Dashboard page

  Scenario Outline: test the login functionality
    Given the user is on the login page
    When he enters his "<username>" and his "<password>"
    And clicks on the login button
    Then he should be redirected to the "<Result>" page

    Examples:
      | username | password  | Result |
      |  Admin   | admin123  | Dashboard |
      |  Admin   | admin321  | Invalid credentials |
      |  NotAdmin   | admin123  | Invalid credentials |