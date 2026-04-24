Feature: Checkout functionality
  As a logged-in user with items in my cart
  I want to complete the checkout process
  So that I can purchase products

  Background:
    Given I am logged in as "standard_user" with password "secret_sauce"
    And I have "Sauce Labs Backpack" in my cart
    And I am on the cart page

  Scenario: Navigate to checkout step one
    When I click the checkout button
    Then I should be on checkout step one

  Scenario: Complete checkout information and proceed to step two
    When I click the checkout button
    And I enter first name "John", last name "Doe", and postal code "12345"
    And I click continue on checkout
    Then I should be on checkout step two

  Scenario: Complete a full order successfully
    When I click the checkout button
    And I enter first name "John", last name "Doe", and postal code "12345"
    And I click continue on checkout
    And I click finish
    Then I should see the order confirmation page
    And the confirmation message should contain "Thank you for your order"

  Scenario: Checkout validation - missing first name
    When I click the checkout button
    And I enter first name "", last name "Doe", and postal code "12345"
    And I click continue on checkout
    Then I should see an error message containing "First Name is required"

  Scenario: Cancel checkout returns to cart
    When I click the checkout button
    And I click cancel on checkout
    Then I should be on the cart page
