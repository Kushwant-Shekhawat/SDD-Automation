Feature: Multi-item cart management
  As a logged-in user
  I want to manage multiple items in my cart
  So that I can purchase several products at once

  Background:
    Given I am logged in as "standard_user" with password "secret_sauce"

  Scenario: Add multiple products and verify cart count
    When I add "Sauce Labs Backpack" to the cart
    And I add "Sauce Labs Bike Light" to the cart
    And I add "Sauce Labs Bolt T-Shirt" to the cart
    Then the cart badge should show "3"

  Scenario: Cart page lists all added products
    When I add "Sauce Labs Backpack" to the cart
    And I add "Sauce Labs Bike Light" to the cart
    And I navigate to the cart
    Then "Sauce Labs Backpack" should be in the cart
    And "Sauce Labs Bike Light" should be in the cart
    And the cart item count should be 2

  Scenario: Remove one item from multi-item cart
    When I add "Sauce Labs Backpack" to the cart
    And I add "Sauce Labs Bike Light" to the cart
    And I navigate to the cart
    And I remove "Sauce Labs Backpack" from the cart
    Then the cart item count should be 1
    And "Sauce Labs Bike Light" should be in the cart

  Scenario: Checkout item total is correct for two items
    When I add "Sauce Labs Backpack" to the cart
    And I add "Sauce Labs Bike Light" to the cart
    And I navigate to the cart
    And I click the checkout button
    And I enter first name "John", last name "Doe", and postal code "12345"
    And I click continue on checkout
    Then the item total should contain "39.98"

  Scenario: Cart count persists after visiting product detail page
    When I add "Sauce Labs Backpack" to the cart
    And I add "Sauce Labs Bike Light" to the cart
    And I click on product "Sauce Labs Bolt T-Shirt"
    And I click back to products
    Then the cart badge should show "2"

  Scenario: Add all 6 products and cart badge shows 6
    When I add all products to the cart
    Then the cart badge should show "6"
