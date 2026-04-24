# SDD-Automation Framework - Test Scenarios Specification

## 1. TEST ORGANIZATION

### Test Categories
1. **Smoke Tests** - Critical path, quick validation
2. **Regression Tests** - All features, comprehensive coverage
3. **Edge Cases** - Boundary conditions, unusual scenarios
4. **Negative Tests** - Error handling, validation failures

### Test Suite Distribution
```
Total Test Cases: 25
├── Smoke Tests: 8
├── Regression Tests: 12
├── Edge Cases: 3
└── Negative Tests: 2
```

---

## 2. TEST DATA USERS

### SauceDemo Built-in Users

| Username | Password | Behavior | Usage |
|----------|----------|----------|-------|
| standard_user | secret_sauce | Normal behavior | Positive tests |
| locked_user | secret_sauce | Account locked | Negative test |
| problem_user | secret_sauce | Visual glitches | Edge case tests |
| performance_glitchy_user | secret_sauce | Slow responses | Performance tests |
| visual_user | secret_sauce | Visual rendering | Visual tests |

---

## 3. SMOKE TEST SUITE (8 Tests)

### Smoke Test: Critical User Journey

These tests verify the happy path - the most critical business flows.

#### ST-001: User Login with Valid Credentials
**Category**: Smoke
**Priority**: P0
**Expected Duration**: 5 seconds

**Given**: User is on the SauceDemo login page
**When**:
- User enters username "standard_user"
- User enters password "secret_sauce"
- User clicks Login button

**Then**:
- User is navigated to Products page (inventory.html)
- Products page loads successfully
- At least 6 products are displayed
- Shopping cart icon is visible

```gherkin
Scenario: User can login with valid credentials
  Given User is on login page
  When User logs in with "standard_user" and "secret_sauce"
  Then User should see products page
  And Products count should be at least 6
```

---

#### ST-002: Browse Products on Products Page
**Category**: Smoke
**Priority**: P0
**Expected Duration**: 3 seconds

**Given**: User is logged in and on Products page
**When**:
- Page loads

**Then**:
- All products are displayed
- Each product shows: name, image, price
- Product count is exactly 6
- All products are visible without scrolling

```gherkin
Scenario: All products are displayed on products page
  Given User is logged in
  When User navigates to products page
  Then User should see 6 products
  And Each product should have name, image, and price
```

---

#### ST-003: Add Single Product to Cart
**Category**: Smoke
**Priority**: P0
**Expected Duration**: 3 seconds

**Given**: User is logged in on Products page
**When**:
- User clicks "Add to Cart" for "Sauce Labs Backpack"

**Then**:
- "Add to Cart" button changes to "Remove"
- Cart count badge shows "1"
- Cart total shows 1 item

```gherkin
Scenario: User can add product to cart
  Given User is on products page
  When User adds "Sauce Labs Backpack" to cart
  Then Button text should change to "Remove"
  And Cart count should be 1
```

---

#### ST-004: Add Multiple Products to Cart
**Category**: Smoke
**Priority**: P0
**Expected Duration**: 5 seconds

**Given**: User is on Products page
**When**:
- User adds "Sauce Labs Backpack" to cart
- User adds "Sauce Labs Bike Light" to cart
- User adds "Sauce Labs Bolt T-Shirt" to cart

**Then**:
- Cart badge shows "3"
- All three products are added

```gherkin
Scenario: User can add multiple products to cart
  Given User is on products page
  When User adds 3 products to cart
  Then Cart count should be 3
```

---

#### ST-005: View Shopping Cart
**Category**: Smoke
**Priority**: P0
**Expected Duration**: 3 seconds

**Given**: User has 3 items in cart
**When**:
- User clicks shopping cart icon

**Then**:
- User is navigated to Cart page
- All 3 items are displayed in cart
- Each item shows: name, price, quantity

```gherkin
Scenario: User can view shopping cart
  Given User has 3 items in cart
  When User clicks shopping cart icon
  Then User should be on cart page
  And All 3 items should be displayed
```

---

#### ST-006: Proceed to Checkout
**Category**: Smoke
**Priority**: P0
**Expected Duration**: 3 seconds

**Given**: User is on Cart page with items
**When**:
- User clicks Checkout button

**Then**:
- User is navigated to Checkout Information page
- Checkout heading is displayed
- First Name input field is visible
- Last Name input field is visible
- Postal Code input field is visible

```gherkin
Scenario: User can proceed to checkout
  Given User is on cart page
  When User clicks Checkout button
  Then User should be on checkout step one page
  And Checkout form should be displayed
```

---

#### ST-007: Complete Checkout Process
**Category**: Smoke
**Priority**: P0
**Expected Duration**: 5 seconds

**Given**: User is on Checkout Information page with items
**When**:
- User enters First Name "John"
- User enters Last Name "Doe"
- User enters Postal Code "12345"
- User clicks Continue button
- User reviews order on Checkout Overview page
- User clicks Finish button

**Then**:
- User sees "Thank you for your order!" message
- Order Complete page is displayed
- Pony Express image is visible

```gherkin
Scenario: User can complete checkout successfully
  Given User is on checkout information page
  When User fills checkout form with valid data
  And User proceeds through checkout
  Then User should see thank you message
  And Order should be confirmed
```

---

#### ST-008: Logout from Application
**Category**: Smoke
**Priority**: P0
**Expected Duration**: 2 seconds

**Given**: User is logged in (any page)
**When**:
- User clicks Menu button (hamburger icon)
- User clicks Logout option

**Then**:
- User is navigated to login page
- Login button is visible
- Previous session is cleared

```gherkin
Scenario: User can logout from application
  Given User is logged in
  When User clicks menu button
  And User clicks logout option
  Then User should be on login page
```

---

## 4. REGRESSION TEST SUITE (12 Tests)

### Regression Tests: Comprehensive Feature Coverage

#### RT-001: Login with Invalid Username
**Category**: Regression
**Priority**: P1
**Expected Duration**: 3 seconds

**Given**: User is on login page
**When**:
- User enters username "invalid_user"
- User enters password "secret_sauce"
- User clicks Login button

**Then**:
- Error message is displayed
- Error contains "Epic sadface: Username and password do not match any user in this service"
- User remains on login page

```gherkin
Scenario: Login fails with invalid username
  Given User is on login page
  When User logs in with "invalid_user" and "secret_sauce"
  Then Error message should be displayed
  And Error text should mention username and password mismatch
```

---

#### RT-002: Login with Invalid Password
**Category**: Regression
**Priority**: P1
**Expected Duration**: 3 seconds

**Given**: User is on login page
**When**:
- User enters username "standard_user"
- User enters password "wrong_password"
- User clicks Login button

**Then**:
- Error message is displayed
- Error contains "Epic sadface: Username and password do not match any user in this service"
- User remains on login page

---

#### RT-003: Login with Locked User
**Category**: Regression
**Priority**: P1
**Expected Duration**: 3 seconds

**Given**: User is on login page
**When**:
- User enters username "locked_user"
- User enters password "secret_sauce"
- User clicks Login button

**Then**:
- Error message is displayed
- Error contains "Epic sadface: Sorry, this user has been locked out"
- User remains on login page

---

#### RT-004: Sort Products A-Z
**Category**: Regression
**Priority**: P1
**Expected Duration**: 3 seconds

**Given**: User is on Products page
**When**:
- User selects "Name (A to Z)" from sort dropdown

**Then**:
- Products are sorted alphabetically (A-Z)
- First product is "Sauce Labs Backpack"
- Last product is "Test.allTheThings() T-Shirt (Red)"

```gherkin
Scenario: User can sort products A-Z
  Given User is on products page
  When User sorts products by "Name (A to Z)"
  Then Products should be sorted alphabetically
```

---

#### RT-005: Sort Products Z-A
**Category**: Regression
**Priority**: P1
**Expected Duration**: 3 seconds

**Given**: User is on Products page
**When**:
- User selects "Name (Z to A)" from sort dropdown

**Then**:
- Products are sorted reverse alphabetically (Z-A)
- First product is "Test.allTheThings() T-Shirt (Red)"
- Last product is "Sauce Labs Backpack"

---

#### RT-006: Sort Products by Price Low to High
**Category**: Regression
**Priority**: P1
**Expected Duration**: 3 seconds

**Given**: User is on Products page
**When**:
- User selects "Price (low to high)" from sort dropdown

**Then**:
- Products are sorted by price ascending
- Lowest price product appears first
- Highest price product appears last

---

#### RT-007: Sort Products by Price High to Low
**Category**: Regression
**Priority**: P1
**Expected Duration**: 3 seconds

**Given**: User is on Products page
**When**:
- User selects "Price (high to low)" from sort dropdown

**Then**:
- Products are sorted by price descending
- Highest price product appears first
- Lowest price product appears last

---

#### RT-008: Remove Product from Cart
**Category**: Regression
**Priority**: P1
**Expected Duration**: 3 seconds

**Given**: User has "Sauce Labs Backpack" in cart
**When**:
- User clicks "Remove" button on the product

**Then**:
- Product is removed from cart
- Cart count decreases by 1
- Button text changes to "Add to Cart"

```gherkin
Scenario: User can remove product from cart
  Given User has product in cart
  When User clicks remove button
  Then Product should be removed from cart
  And Cart count should decrease
```

---

#### RT-009: Continue Shopping from Cart
**Category**: Regression
**Priority**: P1
**Expected Duration**: 3 seconds

**Given**: User is on Cart page
**When**:
- User clicks "Continue Shopping" button

**Then**:
- User is navigated back to Products page
- Cart items are still in cart
- Products page is fully loaded

---

#### RT-010: View Product Details
**Category**: Regression
**Priority**: P1
**Expected Duration**: 3 seconds

**Given**: User is on Products page
**When**:
- User clicks on product name "Sauce Labs Backpack"

**Then**:
- User is navigated to product details page
- Product name is displayed
- Product price is displayed
- Product description is displayed
- Product image is displayed
- Add to Cart button is visible

```gherkin
Scenario: User can view product details
  Given User is on products page
  When User clicks on product name
  Then User should see product details page
  And All product information should be displayed
```

---

#### RT-011: Calculate Cart Total Correctly
**Category**: Regression
**Priority**: P1
**Expected Duration**: 5 seconds

**Given**: User has products in cart with known prices
**When**:
- User goes to cart page

**Then**:
- Cart total should equal sum of all product prices
- Total is calculated correctly
- No rounding errors

```gherkin
Scenario: Cart total is calculated correctly
  Given User has products in cart
  When User views cart
  Then Cart total should equal sum of products
```

---

#### RT-012: Checkout Form Validation
**Category**: Regression
**Priority**: P1
**Expected Duration**: 4 seconds

**Given**: User is on Checkout Information page
**When**:
- User leaves First Name empty
- User enters Last Name "Doe"
- User enters Postal Code "12345"
- User clicks Continue button

**Then**:
- Error message is displayed
- Error contains "Error: First Name is required"
- User remains on checkout page

```gherkin
Scenario: Checkout form validates required fields
  Given User is on checkout information page
  When User leaves required field empty
  And User clicks continue
  Then Error message should be displayed
  And Validation error should be shown
```

---

## 5. EDGE CASE TESTS (3 Tests)

### Edge Case Tests: Boundary Conditions and Special Scenarios

#### EC-001: Checkout with Problem User (Visual Glitches)
**Category**: Edge Case
**Priority**: P2
**Expected Duration**: 5 seconds

**Given**: User logs in with "problem_user"
**When**:
- User adds product to cart
- User proceeds to checkout
- User completes order

**Then**:
- Order completes successfully despite visual glitches
- Thank you message is displayed
- Order confirmation is shown

```gherkin
Scenario: Checkout succeeds with problem user (visual issues)
  Given User logs in as problem_user
  When User completes checkout
  Then Order should be successful
  And Thank you message should be displayed
```

---

#### EC-002: Checkout with Performance User (Slow Responses)
**Category**: Edge Case
**Priority**: P2
**Expected Duration**: 15 seconds (due to slow responses)

**Given**: User logs in with "performance_glitchy_user"
**When**:
- User adds product to cart
- User proceeds to checkout

**Then**:
- System waits for slow responses
- All operations complete successfully
- No timeout errors occur

```gherkin
Scenario: System handles slow responses gracefully
  Given User logs in as performance_glitchy_user
  When User interacts with application
  Then All operations should complete eventually
  And No timeout errors should occur
```

---

#### EC-003: Cart Persistence After Navigation
**Category**: Edge Case
**Priority**: P2
**Expected Duration**: 5 seconds

**Given**: User has items in cart
**When**:
- User adds product to cart
- User navigates to product details page
- User navigates back to products page

**Then**:
- Cart items persist
- Cart count remains the same
- Items are not lost

```gherkin
Scenario: Cart persists when navigating away
  Given User has items in cart
  When User navigates to other pages
  And Returns to products page
  Then Cart items should still be present
```

---

## 6. NEGATIVE TESTS (2 Tests)

### Negative Tests: Error Handling and Validation

#### NEG-001: Checkout with Empty First Name
**Category**: Negative
**Priority**: P1
**Expected Duration**: 3 seconds

**Given**: User is on Checkout Information page with items
**When**:
- User leaves First Name field empty
- User enters "Doe" in Last Name
- User enters "12345" in Postal Code
- User clicks Continue

**Then**:
- Error message is displayed
- Error contains "First Name is required"
- User remains on checkout page
- Cart items are preserved

```gherkin
Scenario: Checkout validates first name requirement
  Given User is on checkout page
  When User submits form without first name
  Then Error message should be shown
  And User should remain on same page
```

---

#### NEG-002: Checkout with Invalid Postal Code (Non-numeric)
**Category**: Negative
**Priority**: P1
**Expected Duration**: 3 seconds

**Given**: User is on Checkout Information page
**When**:
- User enters "John" in First Name
- User enters "Doe" in Last Name
- User enters "ABCDE" (non-numeric) in Postal Code
- User clicks Continue

**Then**:
- Submission is processed (SauceDemo doesn't validate postal code format)
- Or error is displayed if validation is implemented
- User flow continues or error is shown appropriately

```gherkin
Scenario: Checkout handles invalid postal code
  Given User is on checkout page
  When User enters invalid postal code
  And User submits form
  Then System should handle appropriately
```

---

## 7. TEST DATA MATRIX

### Login Test Combinations

| Test | Username | Password | Expected Result |
|------|----------|----------|-----------------|
| Valid | standard_user | secret_sauce | ✅ Login success |
| Invalid User | invalid_user | secret_sauce | ❌ Error message |
| Invalid Pass | standard_user | wrong_pass | ❌ Error message |
| Locked User | locked_user | secret_sauce | ❌ Account locked error |
| Visual User | visual_user | secret_sauce | ✅ Login success (visual issues) |
| Performance | performance_glitchy_user | secret_sauce | ✅ Login success (slow) |

### Product Test Combinations

| Product Name | Price | Add-to-Cart | Remove | Details |
|--------------|-------|------------|---------|---------|
| Sauce Labs Backpack | $29.99 | ✅ | ✅ | ✅ |
| Sauce Labs Bike Light | $9.99 | ✅ | ✅ | ✅ |
| Sauce Labs Bolt T-Shirt | $15.99 | ✅ | ✅ | ✅ |
| Sauce Labs Fleece Jacket | $49.99 | ✅ | ✅ | ✅ |
| Sauce Labs Onesie | $7.99 | ✅ | ✅ | ✅ |
| Test.allTheThings() T-Shirt | $15.99 | ✅ | ✅ | ✅ |

### Checkout Test Combinations

| Scenario | First Name | Last Name | Postal | Expected |
|----------|-----------|-----------|--------|----------|
| Valid | John | Doe | 12345 | ✅ Order complete |
| Empty First | (empty) | Doe | 12345 | ❌ Error |
| Empty Last | John | (empty) | 12345 | ❌ Error |
| Empty Postal | John | Doe | (empty) | ❌ Error |
| Valid Special Chars | John-Paul | O'Reilly | 12345 | ✅ Order complete |

---

## 8. TEST EXECUTION STRATEGY

### Sequential Execution
```bash
# Run all 25 tests sequentially
gradle test
# Expected: ~5 minutes
```

### Parallel Execution (Recommended)
```bash
# Run tests in parallel (4 threads)
gradle test --parallel --max-workers=4
# Expected: ~2 minutes
```

### Smoke Tests Only
```bash
# Run only smoke tests (8 tests)
gradle test -DsuiteFile=testng-smoke.xml
# Expected: ~45 seconds
```

### By Category
```bash
# Run only regression tests
gradle test -Dcategory=regression
# Run only edge cases
gradle test -Dcategory=edge-case
```

---

## 9. SUCCESS CRITERIA FOR TEST SUITE

✅ **All 25 tests must pass**
✅ **Average execution time < 5 minutes (sequential)**
✅ **Average execution time < 2 minutes (parallel, 4 threads)**
✅ **No flaky tests (100% pass rate across 3 consecutive runs)**
✅ **All tests have clear assertions**
✅ **Screenshot captured on every failure**
✅ **ExtentReport generated with all test details**
✅ **Tests are independent (can run in any order)**

---

## 10. NEXT PHASE: CLARIFY

This test scenarios specification will be reviewed for:
1. Test case clarity and completeness
2. Expected results accuracy
3. Test data validity
4. Coverage of all features

**Status**: ✅ Test Scenarios Specification Complete