Feature: Logout functionality
  As a logged-in user
  I want to be able to log out
  So that my session is securely terminated

  Background:
    Given I am logged in as "standard_user" with password "secret_sauce"

  Scenario: Successful logout returns to login page
    When I open the navigation menu
    And I click the logout link
    Then I should be on the login page

  Scenario: After logout direct URL access redirects to login
    When I open the navigation menu
    And I click the logout link
    And I navigate directly to "/inventory.html"
    Then I should be on the login page

  Scenario: Reset app state clears cart
    Given I add "Sauce Labs Backpack" to the cart
    When I open the navigation menu
    And I click the reset app state link
    And I close the navigation menu
    Then the cart badge should not be visible

  Scenario: Menu can be opened and closed
    When I open the navigation menu
    Then the navigation menu should be visible
    When I close the navigation menu
    Then the navigation menu should not be visible
