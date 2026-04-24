# SDD-Automation Framework - Product Sorting Specification

## 1. OVERVIEW

SauceDemo supports four sort options on the inventory page. Tests verify that
each option correctly reorders the product list.

---

## 2. SORT OPTIONS

| Label | Value | Expected Behaviour |
|-------|-------|--------------------|
| Name (A to Z) | `az` | Products ordered alphabetically ascending |
| Name (Z to A) | `za` | Products ordered alphabetically descending |
| Price (low to high) | `lohi` | Products ordered by price ascending |
| Price (high to low) | `hilo` | Products ordered by price descending |

### Locator

| Element | Locator | Type |
|---------|---------|------|
| Sort Dropdown | `.product_sort_container` | Select |
| Product Name | `.inventory_item_name` | Text |
| Product Price | `.inventory_item_price` | Text |

---

## 3. TEST SCENARIOS

### PS-001: Default sort is Name (A to Z)
**Category**: Regression  
**Priority**: P1

**Given**: User is on products page (just logged in)  
**Then**: First product name is "Sauce Labs Backpack"  
**And**: Last product name is "Test.allTheThings() T-Shirt (Red)"  

### PS-002: Sort by Name (Z to A)
**Category**: Regression  
**Priority**: P1

**Given**: User is on products page  
**When**: Sort dropdown is set to "Name (Z to A)"  
**Then**: First product is "Test.allTheThings() T-Shirt (Red)"  
**And**: Last product is "Sauce Labs Backpack"  

### PS-003: Sort by Price (low to high)
**Category**: Regression  
**Priority**: P1

**Given**: User is on products page  
**When**: Sort dropdown is set to "Price (low to high)"  
**Then**: First product price is "$7.99"  
**And**: Last product price is "$49.99"  
**And**: Prices are in non-decreasing order  

### PS-004: Sort by Price (high to low)
**Category**: Regression  
**Priority**: P1

**Given**: User is on products page  
**When**: Sort dropdown is set to "Price (high to low)"  
**Then**: First product price is "$49.99"  
**And**: Last product price is "$7.99"  
**And**: Prices are in non-increasing order  

### PS-005: Sort persists when navigating back from product details
**Category**: Edge Case  
**Priority**: P2

**Given**: User sorts by Price (low to high)  
**When**: User clicks a product to view details  
**And**: User clicks "Back to Products"  
**Then**: Sort order is still Price (low to high)  

### PS-006: All products remain visible after each sort
**Category**: Regression  
**Priority**: P2

**Given**: User is on products page  
**When**: Each sort option is selected in turn  
**Then**: Product count is always 6 regardless of sort  

---

## 4. GHERKIN SCENARIOS

```gherkin
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

  Scenario: Sort persists after viewing product details and returning
    When I sort products by "Price (low to high)"
    And I click on product "Sauce Labs Onesie"
    And I click back to products
    Then the first product price should be "$7.99"
```

---

## 5. PAGE OBJECT ADDITIONS

Add to `ProductsPage.java`:

java
public String getFirstProductName() {
    return page.locator(".inventory_item_name").first().textContent().trim();
}

public String getLastProductName() {
    List<String> names = getProductNames();
    return names.get(names.size() - 1);
}

public String getLastProductPrice() {
    List<String> prices = getProductPrices();
    return prices.get(prices.size() - 1);
}

public String getFirstProductPrice() {
    return page.locator(".inventory_item_price").first().textContent().trim();
}

public boolean arePricesInAscendingOrder() {
    List<String> prices = getProductPrices();
    List<Double> parsed = prices.stream()
        .map(p -> Double.parseDouble(p.replace("$", "")))
        .collect(java.util.stream.Collectors.toList());
    for (int i = 0; i < parsed.size() - 1; i++) {
        if (parsed.get(i) > parsed.get(i + 1)) return false;
    }
    return true;
}

public boolean arePricesInDescendingOrder() {
    List<String> prices = getProductPrices();
    List<Double> parsed = prices.stream()
        .map(p -> Double.parseDouble(p.replace("$", "")))
        .collect(java.util.stream.Collectors.toList());
    for (int i = 0; i < parsed.size() - 1; i++) {
        if (parsed.get(i) < parsed.get(i + 1)) return false;
    }
    return true;
}

---

## 6. STEP DEFINITION ADDITIONS

java
@Then("the first product name should be {string}")
public void theFirstProductNameShouldBe(String expected) {
    assertThat(ctx.productsPage.getFirstProductName()).isEqualTo(expected);
}

@Then("the last product price should be {string}")
public void theLastProductPriceShouldBe(String expected) {
    assertThat(ctx.productsPage.getLastProductPrice()).isEqualTo(expected);
}

@And("I click back to products")
public void iClickBackToProducts() {
    ctx.productDetailsPage.clickBackToProducts();
}