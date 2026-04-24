Feature: Negative flows and direct URL access
  As a security tester
  I want to verify that unauthenticated access is blocked
  And invalid inputs are handled gracefully

  Scenario Outline: Direct URL access without login redirects to login page
    Given I am not logged in
    When I navigate directly to "<path>"
    Then I should be on the login page

    Examples:
      | path                        |
      | /inventory.html             |
      | /inventory-item.html?id=4   |
      | /cart.html                  |
      | /checkout-step-one.html     |
      | /checkout-step-two.html     |
      | /checkout-complete.html     |

  Scenario: After logout back button does not restore session
    Given I am logged in as "standard_user" with password "secret_sauce"
    When I open the navigation menu
    And I click the logout link
    And I press the browser back button
    Then I should be on the login page

  Scenario: SQL injection attempt is rejected
    Given I am on the login page
    When I enter username "' OR '1'='1" and password "' OR '1'='1"
    And I click the login button
    Then I should see an error message containing "Username and password do not match"

  Scenario: XSS payload in username is not executed
    Given I am on the login page
    When I enter username "<script>alert('xss')</script>" and password "secret_sauce"
    And I click the login button
    Then I should see an error message containing "Username and password do not match"
    And no alert dialog should be present

  Scenario: Extremely long username shows error gracefully
    Given I am on the login page
    When I enter a 500-character username and password "secret_sauce"
    And I click the login button
    Then I should see an error message containing "Username and password do not match"
