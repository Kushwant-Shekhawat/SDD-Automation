# SDD-Automation Framework - Checkout Validation Specification

## 1. OVERVIEW

Checkout validation tests cover all field-level error states on Checkout Step One,
cancellation flows, and edge cases in the order overview and confirmation pages.

---

## 2. VALIDATION RULES

### Step One — Customer Information

| Scenario | Input State | Expected Error |
|----------|-------------|----------------|
| Missing first name | firstName="" | "Error: First Name is required" |
| Missing last name | lastName="" | "Error: Last Name is required" |
| Missing postal code | postalCode="" | "Error: Postal Code is required" |
| All fields empty | all="" | "Error: First Name is required" |
| Valid submission | all filled | Proceeds to Step Two |

### Step One Locators

| Element | Locator | Type |
|---------|---------|------|
| First Name | `[data-test="firstName"]` | Text Input |
| Last Name | `[data-test="lastName"]` | Text Input |
| Postal Code | `[data-test="postalCode"]` | Text Input |
| Continue Button | `[data-test="continue"]` | Button |
| Cancel Button | `[data-test="cancel"]` | Button |
| Error Message | `[data-test="error"]` | Text Container |
| Error Close | `.error-button` | Button |

### Step Two — Order Overview

| Element | Locator | Type |
|---------|---------|------|
| Item Total | `.summary_subtotal_label` | Text |
| Tax Amount | `.summary_tax_label` | Text |
| Order Total | `.summary_total_label` | Text |
| Finish Button | `[data-test="finish"]` | Button |
| Cancel Button | `[data-test="cancel"]` | Button |

---

## 3. TEST SCENARIOS

### CV-001: Missing first name shows error
**Category**: Negative  
**Priority**: P1

**Given**: User has item in cart and is on checkout step one  
**When**: firstName="", lastName="Doe", postalCode="12345", click Continue  
**Then**: Error "Error: First Name is required" is displayed  

### CV-002: Missing last name shows error
**Category**: Negative  
**Priority**: P1

**Given**: Checkout step one  
**When**: firstName="John", lastName="", postalCode="12345", click Continue  
**Then**: Error "Error: Last Name is required" is displayed  

### CV-003: Missing postal code shows error
**Category**: Negative  
**Priority**: P1

**Given**: Checkout step one  
**When**: firstName="John", lastName="Doe", postalCode="", click Continue  
**Then**: Error "Error: Postal Code is required" is displayed  

### CV-004: All fields empty shows first-name error
**Category**: Negative  
**Priority**: P2

**Given**: Checkout step one  
**When**: All fields empty, click Continue  
**Then**: Error "Error: First Name is required" is displayed  

### CV-005: Error message can be dismissed
**Category**: Regression  
**Priority**: P2

**Given**: Validation error is visible  
**When**: User clicks the X button on the error  
**Then**: Error message disappears  

### CV-006: Cancel on step one returns to cart
**Category**: Regression  
**Priority**: P1

**Given**: User is on checkout step one  
**When**: User clicks Cancel  
**Then**: User is back on cart page  

### CV-007: Cancel on step two returns to products
**Category**: Regression  
**Priority**: P1

**Given**: User is on checkout step two (order overview)  
**When**: User clicks Cancel  
**Then**: User is navigated back to the products page  

### CV-008: Order total includes tax
**Category**: Regression  
**Priority**: P1

**Given**: User is on checkout step two with one item  
**Then**: Total = Item subtotal + Tax  
**And**: Tax value is non-zero  

### CV-009: Checkout with special characters in name fields
**Category**: Edge Case  
**Priority**: P3

**Given**: Checkout step one  
**When**: firstName="John-O'Brien", lastName="Müller", postalCode="SW1A 1AA"  
**Then**: Proceeds to step two without error  

### CV-010: Checkout with very long postal code
**Category**: Edge Case  
**Priority**: P3

**Given**: Checkout step one  
**When**: postalCode="1234567890123456789"  
**Then**: Form submits without crashing (SauceDemo accepts any non-empty value)  

---

## 4. GHERKIN SCENARIOS

gherkin
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

  Scenario: Cancel on step two returns to products
    When I enter first name "John", last name "Doe", and postal code "12345"
    And I click continue on checkout
    And I click cancel on checkout
    Then I should be on the products page

  Scenario: Order total includes tax on step two
    When I enter first name "John", last name "Doe", and postal code "12345"
    And I click continue on checkout
    Then the order total should be greater than the item total

  Scenario: Checkout with special characters completes successfully
    When I enter first name "John-O'Brien", last name "Müller", and postal code "SW1A 1AA"
    And I click continue on checkout
    Then I should be on checkout step two


---

## 5. PAGE OBJECT ADDITIONS

Add to `CheckoutPage.java`:

java
public void dismissError() {
    click(page.locator(".error-button"));
}

public boolean isErrorVisible() {
    return isVisible(page.locator("[data-test='error']"), 2000);
}

public String getTaxAmount() {
    return getText(page.locator(".summary_tax_label"));
}

public String getOrderTotal() {
    return getText(page.locator(".summary_total_label"));
}

public double parsePrice(String priceLabel) {
    // extracts numeric value from e.g. "Item total: $39.98"
    return Double.parseDouble(priceLabel.replaceAll("[^0-9.]", ""));
}


---

## 6. STEP DEFINITION ADDITIONS

java
@And("I dismiss the checkout error")
public void iDismissTheCheckoutError() {
    ctx.checkoutPage.dismissError();
}

@Then("the checkout error should not be visible")
public void theCheckoutErrorShouldNotBeVisible() {
    assertThat(ctx.checkoutPage.isErrorVisible()).isFalse();
}

@Then("the order total should be greater than the item total")
public void theOrderTotalShouldBeGreaterThanTheItemTotal() {
    double itemTotal = ctx.checkoutPage.parsePrice(ctx.checkoutPage.getItemTotal());
    double orderTotal = ctx.checkoutPage.parsePrice(ctx.checkoutPage.getOrderTotal());
    assertThat(orderTotal).isGreaterThan(itemTotal);
}

