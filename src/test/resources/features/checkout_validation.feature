Feature: Checkout form validation
  As a user completing checkout
  I want to see clear validation messages
  So that I can correct my information

  Background:
    Given I am logged in as "standard_user" with password "secret_sauce"
    And I have "Sauce Labs Backpack" in my cart
    And I am on the cart page
    And I click the checkout button

  Scenario: Missing first name shows validation error
    When I enter first name "", last name "Doe", and postal code "12345"
    And I click continue on checkout
    Then I should see an error message containing "First Name is required"

  Scenario: Missing last name shows validation error
    When I enter first name "John", last name "", and postal code "12345"
    And I click continue on checkout
    Then I should see an error message containing "Last Name is required"

  Scenario: Missing postal code shows validation error
    When I enter first name "John", last name "Doe", and postal code ""
    And I click continue on checkout
    Then I should see an error message containing "Postal Code is required"

  Scenario: All fields empty shows first name error
    When I enter first name "", last name "", and postal code ""
    And I click continue on checkout
    Then I should see an error message containing "First Name is required"

  Scenario: Validation error can be dismissed
    When I enter first name "", last name "Doe", and postal code "12345"
    And I click continue on checkout
    And I dismiss the checkout error
    Then the checkout error should not be visible

  Scenario: Cancel on step one returns to cart
    When I click cancel on checkout
    Then I should be on the cart page

  Scenario: Cancel on step two returns to products page
    When I enter first name "John", last name "Doe", and postal code "12345"
    And I click continue on checkout
    And I click cancel on checkout
    Then I should be on the products page

  Scenario: Order total includes tax
    When I enter first name "John", last name "Doe", and postal code "12345"
    And I click continue on checkout
    Then the order total should be greater than the item total

  Scenario: Special characters in name fields pass validation
    When I enter first name "John-O'Brien", last name "Müller", and postal code "SW1A 1AA"
    And I click continue on checkout
    Then I should be on checkout step two
