Feature: Login functionality
  As a user of SauceDemo
  I want to be able to login to the application
  So that I can access the products

  Background:
    Given I am on the login page

  Scenario: Successful login with valid credentials
    When I enter username "standard_user" and password "secret_sauce"
    And I click the login button
    Then I should be on the products page

  Scenario: Failed login with locked out user
    When I enter username "locked_out_user" and password "secret_sauce"
    And I click the login button
    Then I should see an error message containing "Sorry, this user has been locked out"

  Scenario: Failed login with invalid credentials
    When I enter username "standard_user" and password "wrong_password"
    And I click the login button
    Then I should see an error message containing "Username and password do not match"

  Scenario: Failed login with empty username
    When I enter username "" and password "secret_sauce"
    And I click the login button
    Then I should see an error message containing "Username is required"

  Scenario: Failed login with empty password
    When I enter username "standard_user" and password ""
    And I click the login button
    Then I should see an error message containing "Password is required"
