Feature: Product details page functionality
  As a logged-in user
  I want to view product details
  So that I can make informed purchase decisions

  Background:
    Given I am logged in as "standard_user" with password "secret_sauce"

  Scenario: Product details shows correct name and price
    When I click on product "Sauce Labs Backpack"
    Then I should be on the product details page
    And the product name should be "Sauce Labs Backpack"
    And the product price should be "$29.99"

  Scenario: Product description is visible on detail page
    When I click on product "Sauce Labs Bike Light"
    Then I should be on the product details page
    And the product description should not be empty

  Scenario: Add to cart from product details page
    When I click on product "Sauce Labs Backpack"
    And I click add to cart on the details page
    Then the cart badge should show "1"
    And the remove button should be visible on details page

  Scenario: Remove from cart on product details page
    When I click on product "Sauce Labs Backpack"
    And I click add to cart on the details page
    And I click remove on the details page
    Then the cart badge should not be visible
    And the add to cart button should be visible on details page

  Scenario: Back to products from detail page
    When I click on product "Sauce Labs Backpack"
    Then I should be on the product details page
    When I click back to products
    Then I should be on the products page
