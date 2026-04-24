# SDD-Automation Framework - Multi-Item Cart Specification

## 1. OVERVIEW

Multi-item cart tests verify that multiple products can be added, managed, and
proceed through checkout correctly, and that cart badge counts stay accurate.

---

## 2. TEST SCENARIOS

### MC-001: Add all 6 products to cart
**Category**: Regression  
**Priority**: P1

**Given**: User is on products page  
**When**: User adds all 6 products to cart  
**Then**: Cart badge shows "6"  
**And**: All 6 products are listed on cart page  

### MC-002: Cart badge increments correctly with each addition
**Category**: Regression  
**Priority**: P1

**Given**: User is on products page  
**When**: User adds products one by one  
**Then**: Cart badge increments by 1 after each addition  
(1 → 2 → 3)  

### MC-003: Remove one item from multi-item cart
**Category**: Regression  
**Priority**: P1

**Given**: User has 3 items in cart  
**When**: User removes 1 item from cart page  
**Then**: Cart shows 2 items  
**And**: Cart badge shows "2"  

### MC-004: Checkout total reflects all items
**Category**: Regression  
**Priority**: P1

**Given**: User has Sauce Labs Backpack ($29.99) and Sauce Labs Bike Light ($9.99) in cart  
**When**: User proceeds to checkout overview  
**Then**: Item total is "$39.98"  

### MC-005: Cart persists across page navigation
**Category**: Edge Case  
**Priority**: P2

**Given**: User has 2 items in cart  
**When**: User navigates to product details and back  
**Then**: Cart badge still shows "2"  

### MC-006: Adding same product via list and detail pages is idempotent
**Category**: Edge Case  
**Priority**: P2

**Given**: User adds "Sauce Labs Backpack" from products page  
**When**: User visits the Backpack detail page  
**Then**: "Add to cart" button is replaced by "Remove" (already in cart)  
**And**: Cart count remains "1"  

### MC-007: Remove all items, cart is empty at checkout
**Category**: Regression  
**Priority**: P1

**Given**: User adds 2 items then removes both from cart page  
**Then**: Cart is empty  
**And**: Checkout button proceeds to step one with no items listed  

---

## 3. GHERKIN SCENARIOS

gherkin
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


---

## 4. PAGE OBJECT ADDITIONS

Add to `CartPage.java`:

java
public int getCartItemCount() {
    return page.locator(".cart_item").count();
}


Add to `CheckoutPage.java`:

java
public String getItemTotal() {
    return getText(page.locator(".summary_subtotal_label"));
}


---

## 5. STEP DEFINITION ADDITIONS

java
@Then("the cart item count should be {int}")
public void theCartItemCountShouldBe(int expected) {
    assertThat(ctx.cartPage.getCartItemCount()).isEqualTo(expected);
}

@Then("the item total should contain {string}")
public void theItemTotalShouldContain(String expected) {
    assertThat(ctx.checkoutPage.getItemTotal()).contains(expected);
}

