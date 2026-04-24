# SDD-Automation Framework - Page Object Model Specification (CORRECTED)

## 1. POM PRINCIPLES

- Encapsulation: Each page is a separate class
- Abstraction: Implementation details hidden from tests
- Reusability: Page methods reused across tests
- Maintainability: All locators in one place
- Scalability: New pages added without affecting tests

## 2. PAGE HIERARCHY

All pages extend BasePage abstract class.

**Pages to Create:**
- LoginPage
- ProductsPage
- ProductDetailsPage
- CartPage
- CheckoutPage

---

## 3. LOGINPAGE SPECIFICATION

### URL
https://www.saucedemo.com/

### Elements and Locators

| Element | Locator | Type |
|---------|---------|------|
| Username Input | [data-test="username"] | Text Input |
| Password Input | [data-test="password"] | Text Input |
| Login Button | [id="login-button"] | Button |
| Error Message | [data-test="error"] | Text Container |
| Menu Button | #react-burger-menu-btn | Button |
| Logout Link | [id="logout_sidebar_link"] | Link |

### Public Methods


login(String username, String password)
enterUsername(String username)
enterPassword(String password)
clickLoginButton()
isErrorMessageDisplayed() -> boolean
getErrorMessage() -> String
logout()
isLoginButtonVisible() -> boolean


### Usage Example
java
LoginPage loginPage = new LoginPage(page);
loginPage.login("standard_user", "secret_sauce");
String errorMsg = loginPage.getErrorMessage();


---

## 4. PRODUCTSPAGE SPECIFICATION

### URL
https://www.saucedemo.com/inventory.html

### Elements and Locators

| Element | Locator | Type |
|---------|---------|------|
| Products Container | div.inventory_container | Container |
| Product Items | div.inventory_item | List Item |
| Product Name Link | a.inventory_item_name | Link |
| Product Price | div.inventory_item_price | Text |
| Add to Cart Button | button[id*="add-to-cart"] | Button |
| Remove Button | button[id*="remove"] | Button |
| Cart Badge | span.shopping_cart_badge | Badge |
| Cart Link | a.shopping_cart_link | Link |
| Sort Dropdown | select.product_sort_container | Select |
| Menu Button | #react-burger-menu-btn | Button |
| Logout Link | [id="logout_sidebar_link"] | Link |

### Public Methods


addProductToCart(String productName)
removeProductFromCart(String productName)
getProductCount() -> int
getProductNames() -> List<String>
sortProducts(String sortOption)
getProductPrice(String productName) -> double
clickProduct(String productName)
getCartCount() -> int
goToCart()
logout()


### Usage Example
java
ProductsPage productsPage = new ProductsPage(page);
productsPage.addProductToCart("Sauce Labs Backpack");
int count = productsPage.getProductCount();


---

## 5. PRODUCTDETAILSPAGE SPECIFICATION

### URL
https://www.saucedemo.com/inventory-item.html?id=[id]

### Elements and Locators

| Element | Locator | Type |
|---------|---------|------|
| Product Name | div.inventory_details_name | Text |
| Product Price | div.inventory_details_price | Text |
| Product Description | div.inventory_details_desc | Text |
| Product Image | img.inventory_details_img | Image |
| Add to Cart Button | button[id*="add-to-cart"] | Button |
| Remove Button | button[id*="remove"] | Button |
| Back Button | [data-test="back-to-products"] | Button |
| Cart Link | a.shopping_cart_link | Link |

### Public Methods


getProductName() -> String
getProductPrice() -> double
getProductDescription() -> String
addToCart()
removeFromCart()
backToProducts()
isAddToCartButtonVisible() -> boolean
isRemoveButtonVisible() -> boolean
goToCart()


### Usage Example
java
ProductDetailsPage detailsPage = new ProductDetailsPage(page);
String name = detailsPage.getProductName();
double price = detailsPage.getProductPrice();
detailsPage.addToCart();


---

## 6. CARTPAGE SPECIFICATION

### URL
https://www.saucedemo.com/cart.html

### Elements and Locators

| Element | Locator | Type |
|---------|---------|------|
| Cart Items Container | div.cart_list | Container |
| Cart Item | div.cart_item | List Item |
| Item Name | a.inventory_item_name | Link |
| Item Price | div.inventory_item_price | Text |
| Item Quantity | div.cart_quantity | Text |
| Remove Button | button[id*="remove"] | Button |
| Continue Shopping | [data-test="continue-shopping"] | Button |
| Checkout Button | [data-test="checkout"] | Button |

### Public Methods


getCartItems() -> List<String>
removeItem(String itemName)
getCartTotal() -> double
getCartItemCount() -> int
isCartEmpty() -> boolean
continueShoppingButton()
proceedToCheckout()
getItemPrice(String itemName) -> double
checkItemExists(String itemName) -> boolean


### Usage Example
java
CartPage cartPage = new CartPage(page);
List<String> items = cartPage.getCartItems();
cartPage.removeItem("Sauce Labs Backpack");
double total = cartPage.getCartTotal();
cartPage.proceedToCheckout();


---

## 7. CHECKOUTPAGE SPECIFICATION

### Step 1: Checkout Information

**URL**: https://www.saucedemo.com/checkout-step-one.html

| Element | Locator | Type |
|---------|---------|------|
| First Name | [data-test="firstName"] | Text Input |
| Last Name | [data-test="lastName"] | Text Input |
| Postal Code | [data-test="postalCode"] | Text Input |
| Continue Button | [data-test="continue"] | Button |
| Cancel Button | [data-test="cancel"] | Button |
| Error Message | [data-test="error"] | Text |

### Step 2: Checkout Overview

**URL**: https://www.saucedemo.com/checkout-step-two.html

| Element | Locator | Type |
|---------|---------|------|
| Order Items | div.cart_item | List |
| Item Name | div.inventory_item_name | Text |
| Item Price | div.inventory_item_price | Text |
| Subtotal | div.summary_subtotal_label | Text |
| Tax | div.summary_tax_label | Text |
| Total | div.summary_total_label | Text |
| Finish Button | [data-test="finish"] | Button |
| Cancel Button | [data-test="cancel"] | Button |

### Step 3: Order Complete

**URL**: https://www.saucedemo.com/checkout-complete.html

| Element | Locator | Type |
|---------|---------|------|
| Thank You Header | h2.complete-header | Text |
| Thank You Text | div.complete-text | Text |
| Back Home Button | [data-test="back-to-products"] | Button |

### Public Methods


enterFirstName(String firstName)
enterLastName(String lastName)
enterPostalCode(String postalCode)
clickContinueButton()
cancelCheckout()
isErrorMessageDisplayed() -> boolean
getErrorMessage() -> String
getOrderTotal() -> double
clickFinishButton()
isThankYouMessageDisplayed() -> boolean
getThankYouMessage() -> String
backHome()


### Usage Example - Step 1
java
CheckoutPage checkoutPage = new CheckoutPage(page);
checkoutPage.enterFirstName("John");
checkoutPage.enterLastName("Doe");
checkoutPage.enterPostalCode("12345");
checkoutPage.clickContinueButton();


### Usage Example - Step 2
java
double total = checkoutPage.getOrderTotal();
checkoutPage.clickFinishButton();


### Usage Example - Step 3
java
boolean isConfirmed = checkoutPage.isThankYouMessageDisplayed();
String message = checkoutPage.getThankYouMessage();


---

## 8. BASEPAGE CLASS

### Constructor

java
public abstract class BasePage {
    protected Page page;

    public BasePage(Page page) {
        this.page = page;
    }
}


### Protected Methods to Implement

java
protected void click(Locator element)
protected void fill(Locator element, String text)
protected void selectDropdown(Locator element, String value)
protected String getText(Locator element)
protected void waitForElement(Locator element, int timeoutMs)
protected void waitForElementVisible(Locator element)
protected void navigate(String url)
protected String getCurrentUrl()
protected void takeScreenshot(String fileName)


---

## 9. LOCATOR STRATEGY

### Priority Order

1. `data-test` attributes (Best — intended for testing, SauceDemo uses these)
2. `id` attributes (Good — unique identifiers)
3. CSS class combinations (Acceptable — if semantically stable)
4. CSS attribute selectors `[attr*="value"]` (Less preferred)
5. XPath (Last resort — hardest to maintain)

### Examples

**Bad Locator** (fragile):

button[onclick="addToCart()"]


**Acceptable Locator**:

button[id*="add-to-cart"]


**Best Locator** (SauceDemo uses data-test):

[data-test="add-to-cart-sauce-labs-backpack"]


---

## 10. PAGE OBJECT PATTERNS

### Pattern 1: Locator as field (preferred for frequently used elements)

java
private final Locator loginButton;

public LoginPage(Page page) {
    super(page);
    this.loginButton = page.locator("[id='login-button']");
}

public void clickLoginButton() {
    click(loginButton);
}


### Pattern 2: Inline locator (acceptable for one-off interactions)

java
public void clickLoginButton() {
    click(page.locator("[id='login-button']"));
}


### Pattern 3: Filtering by text (replaces deprecated >> syntax)

java
public void addProductToCart(String productName) {
    // Find the product container that contains the given product name, then find its button
    page.locator(".inventory_item")
        .filter(new Locator.FilterOptions().setHasText(productName))
        .locator("button[id*='add-to-cart']")
        .click();
}


---

## 11. USER JOURNEY EXAMPLE

### Complete Login to Checkout Flow

java
// 1. Login
LoginPage loginPage = new LoginPage(page);
loginPage.login("standard_user", "secret_sauce");

// 2. Browse Products
ProductsPage productsPage = new ProductsPage(page);
productsPage.addProductToCart("Sauce Labs Backpack");
productsPage.addProductToCart("Sauce Labs Bike Light");

// 3. Go to Cart
productsPage.goToCart();
CartPage cartPage = new CartPage(page);
int itemCount = cartPage.getCartItemCount();

// 4. Proceed to Checkout
cartPage.proceedToCheckout();
CheckoutPage checkoutPage = new CheckoutPage(page);

// 5. Enter Information (Step 1)
checkoutPage.enterFirstName("John");
checkoutPage.enterLastName("Doe");
checkoutPage.enterPostalCode("12345");
checkoutPage.clickContinueButton();

// 6. Review & Finish (Step 2)
checkoutPage.clickFinishButton();

// 7. Verify Order (Step 3)
assertTrue(checkoutPage.isThankYouMessageDisplayed());


---

## 12. BEST PRACTICES

### DO:
- Use descriptive method names
- Define one Locator field per element
- Keep locators stable (prefer data-test)
- Reuse page methods across tests
- Use explicit waits via BasePage
- Return meaningful data types from query methods

### DON'T:
- Hardcode locator strings in test classes
- Use positional XPath (`//div[3]/button`)
- Use `Thread.sleep()` anywhere
- Put assertions inside page objects
- Put data loading (CSV/JSON) inside page objects

---

## 13. SYNCHRONIZATION STRATEGIES

### Explicit Waits (PREFERRED)

java
protected void waitForElementVisible(Locator element) {
    element.waitFor(new Locator.WaitForOptions()
        .setState(WaitForSelectorState.VISIBLE)
        .setTimeout(10000)
    );
}


### Page Load Waits

java
public void navigate(String url) {
    page.navigate(url);
    page.waitForLoadState(LoadState.NETWORKIDLE);
}


### Element Readiness Before Click

java
protected void click(Locator element) {
    element.waitFor(new Locator.WaitForOptions()
        .setState(WaitForSelectorState.VISIBLE)
    );
    element.click();
}


**Imports required:**
java
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.LoadState;


---

## 14. COMMON LOCATOR PATTERNS FOR SAUCEDEMO


Data-test attributes (most stable):
[data-test="username"]
[data-test="password"]
[data-test="error"]
[data-test="firstName"]
[data-test="lastName"]
[data-test="postalCode"]
[data-test="continue"]
[data-test="finish"]
[data-test="checkout"]
[data-test="continue-shopping"]
[data-test="back-to-products"]

ID attributes:
[id="login-button"]

Attribute contains (for dynamic IDs):
button[id*="add-to-cart"]
button[id*="remove"]

Filter by text (for product-specific buttons):
.inventory_item.filter(hasText: productName).locator("button[id*='add-to-cart']")


---

**Status**: ✅ Page Object Model Specification - CORRECTED and SIMPLIFIED

---

## 15. NAVIGATIONCOMPONENT SPECIFICATION

### Elements and Locators

| Element | Locator | Type |
|---------|---------|------|
| Menu Button | #react-burger-menu-btn | Button |
| Menu Container | .bm-menu-wrap | Container |
| Close Button | #react-burger-cross-btn | Button |
| Logout Link | #logout_sidebar_link | Link |
| All Items Link | #inventory_sidebar_link | Link |
| About Link | #about_sidebar_link | Link |
| Reset App State Link | #reset_sidebar_link | Link |

### Public Methods

```java
public void openMenu()
public void closeMenu()
public void logout()
public void clickAllItems()
public void resetAppState()
public boolean isMenuVisible()   // checks .bm-menu-wrap[aria-hidden="false"]
```

---

## 16. PRODUCTSPAGE — SORTING & ADDITIONAL METHODS

### Sort Dropdown Locators

| Element | Locator | Type |
|---------|---------|------|
| Sort Dropdown | `.product_sort_container` | Select |
| Product Name | `.inventory_item_name` | Text |
| Product Price | `.inventory_item_price` | Text |

### Sort Values

| Label | Select Value |
|-------|-------------|
| Name (A to Z) | `az` |
| Name (Z to A) | `za` |
| Price (low to high) | `lohi` |
| Price (high to low) | `hilo` |

### Additional Public Methods

```java
public String getFirstProductName()
public String getLastProductName()
public String getFirstProductPrice()
public String getLastProductPrice()
public boolean arePricesInAscendingOrder()
public boolean arePricesInDescendingOrder()
public void addAllProductsToCart()   // clicks all .btn_inventory buttons
```

---

## 17. PRODUCTDETAILSPAGE — ADDITIONAL LOCATORS & METHODS

### Updated Locators (use data-test* to avoid strict-mode violations)

| Element | Locator | Type |
|---------|---------|------|
| Product Name | `.inventory_details_name` | Text |
| Product Description | `.inventory_details_desc` | Text |
| Product Price | `.inventory_details_price` | Text |
| Product Image | `.inventory_details_img` | Image |
| Add to Cart | `[data-test*="add-to-cart"]` | Button |
| Remove | `[data-test*="remove"]` | Button |
| Back to Products | `[data-test="back-to-products"]` | Link |

### Additional Public Methods

```java
public String getProductPrice()
public String getProductDescription()
public boolean isProductImageVisible()
public boolean isAddToCartButtonVisible()
public boolean isRemoveButtonVisible()
public void addToCart()
public void removeFromCart()
```

---

## 18. CARTPAGE & CHECKOUTPAGE — ADDITIONAL METHODS

### CartPage

```java
public int getCartItemCount()   // count of .cart_item elements
```

### CheckoutPage — Error & Totals

```java
public void dismissError()           // clicks .error-button
public boolean isErrorVisible()      // [data-test="error"] visible within 2s
public String getItemTotal()         // .summary_subtotal_label
public String getTaxAmount()         // .summary_tax_label
public String getOrderTotal()        // .summary_total_label
public double parsePrice(String priceLabel)  // strips non-numeric chars
```
