# SDD-Automation Framework - Product Details Specification

## 1. OVERVIEW

Product details page tests verify that individual product pages display correct
information and that add-to-cart / remove interactions work from the detail view.

---

## 2. PAGE ELEMENTS

### ProductDetailsPage Locators

| Element | Locator | Type |
|---------|---------|------|
| Product Name | `.inventory_details_name` | Text |
| Product Description | `.inventory_details_desc` | Text |
| Product Price | `.inventory_details_price` | Text |
| Product Image | `.inventory_details_img` | Image |
| Add to Cart Button | `[data-test*="add-to-cart"]` | Button |
| Remove Button | `[data-test*="remove"]` | Button |
| Back to Products | `[data-test="back-to-products"]` | Link |
| Cart Badge | `.shopping_cart_badge` | Badge |

---

## 3. TEST SCENARIOS

### PD-001: Product details page shows correct name
**Category**: Regression  
**Priority**: P1

**Given**: User is on products page  
**When**: User clicks "Sauce Labs Backpack"  
**Then**: Product name on detail page is "Sauce Labs Backpack"  

### PD-002: Product details page shows correct price
**Category**: Regression  
**Priority**: P1

**Given**: User navigates to Sauce Labs Backpack detail page  
**Then**: Price displayed is "$29.99"  

### PD-003: Product details page shows description
**Category**: Regression  
**Priority**: P2

**Given**: User navigates to a product detail page  
**Then**: Product description text is non-empty  

### PD-004: Product details page shows image
**Category**: Regression  
**Priority**: P2

**Given**: User navigates to a product detail page  
**Then**: Product image is visible  

### PD-005: Add to cart from detail page updates cart badge
**Category**: Smoke  
**Priority**: P0

**Given**: User is on product details page for "Sauce Labs Backpack"  
**When**: User clicks "Add to cart"  
**Then**: Cart badge shows "1"  
**And**: Button changes to "Remove"  

### PD-006: Remove from cart on detail page clears cart badge
**Category**: Regression  
**Priority**: P1

**Given**: User has added "Sauce Labs Backpack" from detail page  
**When**: User clicks "Remove"  
**Then**: Cart badge is not visible  
**And**: Button changes back to "Add to cart"  

### PD-007: Back to products returns to inventory
**Category**: Smoke  
**Priority**: P0

**Given**: User is on product details page  
**When**: User clicks "Back to Products"  
**Then**: User is on the products page  

### PD-008: All 6 products have accessible detail pages
**Category**: Regression  
**Priority**: P2

**Given**: User is on products page  
**When**: User clicks each product name  
**Then**: Each detail page loads with a non-empty product name  
**And**: Each detail page has a price in format "$X.XX"  

---

## 4. GHERKIN SCENARIOS

```gherkin
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
```

---

## 5. PAGE OBJECT ADDITIONS

Add to `ProductDetailsPage.java`:

java
public String getProductPrice() {
    return getText(page.locator(".inventory_details_price"));
}

public String getProductDescription() {
    return getText(page.locator(".inventory_details_desc"));
}

public boolean isProductImageVisible() {
    return isVisible(page.locator(".inventory_details_img"), 3000);
}

public boolean isAddToCartButtonVisible() {
    return isVisible(page.locator("[data-test*='add-to-cart']"), 3000);
}

public boolean isRemoveButtonVisible() {
    return isVisible(page.locator("[data-test*='remove']"), 3000);
}

public void addToCart() {
    click(page.locator("[data-test*='add-to-cart']"));
}

public void removeFromCart() {
    click(page.locator("[data-test*='remove']"));
}

public int getCartBadgeCount() {
    Locator badge = page.locator(".shopping_cart_badge");
    if (!badge.isVisible()) return 0;
    return Integer.parseInt(badge.textContent().trim());
}

---

## 6. STEP DEFINITION ADDITIONS

java
@Then("the product price should be {string}")
public void theProductPriceShouldBe(String expected) {
    assertThat(ctx.productDetailsPage.getProductPrice()).isEqualTo(expected);
}

@Then("the product description should not be empty")
public void theProductDescriptionShouldNotBeEmpty() {
    assertThat(ctx.productDetailsPage.getProductDescription()).isNotEmpty();
}

@And("I click add to cart on the details page")
public void iClickAddToCartOnTheDetailsPage() {
    ctx.productDetailsPage.addToCart();
}

@And("I click remove on the details page")
public void iClickRemoveOnTheDetailsPage() {
    ctx.productDetailsPage.removeFromCart();
}

@Then("the remove button should be visible on details page")
public void theRemoveButtonShouldBeVisibleOnDetailsPage() {
    assertThat(ctx.productDetailsPage.isRemoveButtonVisible()).isTrue();
}

@Then("the add to cart button should be visible on details page")
public void theAddToCartButtonShouldBeVisibleOnDetailsPage() {
    assertThat(ctx.productDetailsPage.isAddToCartButtonVisible()).isTrue();
}