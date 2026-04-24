Feature: Product sorting functionality
  As a logged-in user
  I want to sort products in different orders
  So that I can find what I need easily

  Background:
    Given I am logged in as "standard_user" with password "secret_sauce"

  Scenario: Default sort shows products A to Z
    Then the first product name should be "Sauce Labs Backpack"

  Scenario: Sort by Name Z to A
    When I sort products by "Name (Z to A)"
    Then the first product name should be "Test.allTheThings() T-Shirt (Red)"

  Scenario: Sort by Price low to high
    When I sort products by "Price (low to high)"
    Then the first product price should be "$7.99"
    And the last product price should be "$49.99"

  Scenario: Sort by Price high to low
    When I sort products by "Price (high to low)"
    Then the first product price should be "$49.99"
    And the last product price should be "$7.99"

  Scenario: Product count stays the same after sorting
    When I sort products by "Name (Z to A)"
    Then I should see 6 products on the page
    When I sort products by "Price (low to high)"
    Then I should see 6 products on the page

  Scenario: Back from product details returns to products page
    When I sort products by "Price (low to high)"
    And I click on product "Sauce Labs Onesie"
    And I click back to products
    Then I should be on the products page
