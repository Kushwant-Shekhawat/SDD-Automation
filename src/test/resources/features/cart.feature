Feature: Shopping cart functionality
  As a logged-in user
  I want to manage my shopping cart
  So that I can control what I purchase

  Background:
    Given I am logged in as "standard_user" with password "secret_sauce"

  Scenario: Cart is empty on first login
    When I navigate to the cart
    Then the cart should be empty

  Scenario: Item added to cart appears in cart page
    When I add "Sauce Labs Backpack" to the cart
    And I navigate to the cart
    Then "Sauce Labs Backpack" should be in the cart

  Scenario: Remove item from cart
    When I add "Sauce Labs Backpack" to the cart
    And I navigate to the cart
    And I remove "Sauce Labs Backpack" from the cart
    Then the cart should be empty

  Scenario: Continue shopping from cart returns to products
    When I navigate to the cart
    And I click continue shopping
    Then I should be on the products page