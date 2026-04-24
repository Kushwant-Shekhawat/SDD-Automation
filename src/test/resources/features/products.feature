Feature: Products page functionality
  As a logged-in user
  I want to browse and interact with products
  So that I can add items to my cart

  Background:
    Given I am logged in as "standard_user" with password "secret_sauce"

  Scenario: Products page displays all 6 products
    Then I should see 6 products on the page

  Scenario: Add a product to cart
    When I add "Sauce Labs Backpack" to the cart
    Then the cart badge should show "1"

  Scenario: Sort products by price low to high
    When I sort products by "Price (low to high)"
    Then the first product price should be "$7.99"

  Scenario: Navigate to product details page
    When I click on product "Sauce Labs Backpack"
    Then I should be on the product details page
    And the product name should be "Sauce Labs Backpack"

  Scenario: Remove product from cart on products page
    When I add "Sauce Labs Backpack" to the cart
    And I remove "Sauce Labs Backpack" from the cart
    Then the cart badge should not be visible
