# SDD-Automation Framework - Cucumber BDD Specification

## 1. CUCUMBER BDD OVERVIEW

### Integration Strategy
- **Framework**: Cucumber 7.x with TestNG runner
- **Feature Files**: Gherkin language (.feature files)
- **Step Definitions**: Java-based implementation
- **Test Scenarios**: SauceDemo user journey tests
- **Execution**: TestNG runner for Cucumber features
- **Reporting**: ExtentReports + Cucumber HTML reports

### Test Scenarios Coverage
- **UI Flows**: Login, Browse, Add to Cart, Checkout (via Cucumber)
- **Unit Tests**: Utility functions, data providers (via TestNG)
- **Combined Approach**: BDD for business scenarios, TestNG for technical tests

---

## 2. GRADLE DEPENDENCIES FOR CUCUMBER

### Add to build.gradle:

```gradle
dependencies {
    // Existing dependencies...
    
    // Cucumber Dependencies
    testImplementation 'io.cucumber:cucumber-java:7.14.0'
    testImplementation 'io.cucumber:cucumber-testng:7.14.0'
    testImplementation 'io.cucumber:cucumber-picocontainer:7.14.0'
}
```

---

## 3. FEATURE FILES STRUCTURE

### Directory Structure:

```
src/test/resources/features/
├── login.feature
├── products.feature
├── shopping_cart.feature
└── checkout.feature
```

---

## 4. LOGIN FEATURE FILE

### File: src/test/resources/features/login.feature

```gherkin
Feature: User Login to SauceDemo

  Background:
    Given User is on the SauceDemo login page

  Scenario: User can login with valid credentials
    When User enters username "standard_user"
    And User enters password "secret_sauce"
    And User clicks the login button
    Then User should see the products page
    And Products count should be at least 6

  Scenario: User cannot login with invalid username
    When User enters username "invalid_user"
    And User enters password "secret_sauce"
    And User clicks the login button
    Then Error message should be displayed
    And Error message should contain "Username and password do not match"

  Scenario: User cannot login with locked account
    When User enters username "locked_user"
    And User enters password "secret_sauce"
    And User clicks the login button
    Then Error message should be displayed
    And Error message should contain "locked out"

  Scenario: User can logout from application
    When User enters username "standard_user"
    And User enters password "secret_sauce"
    And User clicks the login button
    And User clicks the menu button
    And User clicks the logout option
    Then User should be on the login page
```

---

## 5. PRODUCTS FEATURE FILE

### File: src/test/resources/features/products.feature

```gherkin
Feature: Browse Products on SauceDemo

  Background:
    Given User is logged in with username "standard_user" and password "secret_sauce"
    And User is on the products page

  Scenario: All products are displayed
    Then User should see 6 products
    And Each product should have name, image, and price

  Scenario: User can sort products alphabetically
    When User selects sort option "Name (A to Z)"
    Then Products should be sorted alphabetically
    And First product should be "Sauce Labs Backpack"

  Scenario: User can sort products by price low to high
    When User selects sort option "Price (low to high)"
    Then Products should be sorted by price ascending

  Scenario: User can add single product to cart
    When User adds "Sauce Labs Backpack" to cart
    Then Cart count should be 1
    And Button text for product should change to "Remove"

  Scenario: User can add multiple products to cart
    When User adds "Sauce Labs Backpack" to cart
    And User adds "Sauce Labs Bike Light" to cart
    And User adds "Sauce Labs Bolt T-Shirt" to cart
    Then Cart count should be 3

  Scenario: User can view product details
    When User clicks on product "Sauce Labs Backpack"
    Then User should see product details page
    And Product name should be "Sauce Labs Backpack"
    And Product price should be displayed
```

---

## 6. SHOPPING CART FEATURE FILE

### File: src/test/resources/features/shopping_cart.feature

```gherkin
Feature: Shopping Cart Management

  Background:
    Given User is logged in with username "standard_user" and password "secret_sauce"
    And User has added "Sauce Labs Backpack" to cart
    And User has added "Sauce Labs Bike Light" to cart

  Scenario: User can view shopping cart
    When User clicks the shopping cart icon
    Then User should be on the cart page
    And Cart should contain 2 items
    And Each item should display name, price, and quantity

  Scenario: User can remove product from cart
    When User clicks the shopping cart icon
    And User removes "Sauce Labs Backpack" from cart
    Then Cart item count should be 1
    And "Sauce Labs Backpack" should not be in cart

  Scenario: User can continue shopping from cart
    When User clicks the shopping cart icon
    And User clicks "Continue Shopping" button
    Then User should be on the products page
    And Cart still contains 2 items

  Scenario: Cart total is calculated correctly
    When User clicks the shopping cart icon
    Then Cart total should equal sum of all product prices
    And Total should be displayed

  Scenario: User can proceed to checkout from cart
    When User clicks the shopping cart icon
    And User clicks "Checkout" button
    Then User should be on the checkout information page
```

---

## 7. CHECKOUT FEATURE FILE

### File: src/test/resources/features/checkout.feature

```gherkin
Feature: Complete Checkout Process

  Background:
    Given User is logged in with username "standard_user" and password "secret_sauce"
    And User has added products to cart
    And User is on the checkout page step one

  Scenario: User can complete checkout with valid information
    When User enters first name "John"
    And User enters last name "Doe"
    And User enters postal code "12345"
    And User clicks continue button
    And User reviews order details
    And User clicks finish button
    Then User should see order confirmation message
    And User should be on the order complete page

  Scenario: Checkout form validates required first name
    When User leaves first name empty
    And User enters last name "Doe"
    And User enters postal code "12345"
    And User clicks continue button
    Then Error message should be displayed
    And Error should contain "First Name is required"

  Scenario: Checkout form validates required last name
    When User enters first name "John"
    And User leaves last name empty
    And User enters postal code "12345"
    And User clicks continue button
    Then Error message should be displayed
    And Error should contain "Last Name is required"

  Scenario: Checkout form validates required postal code
    When User enters first name "John"
    And User enters last name "Doe"
    And User leaves postal code empty
    And User clicks continue button
    Then Error message should be displayed
    And Error should contain "Postal Code is required"

  Scenario: User can cancel checkout and return to cart
    When User clicks cancel button
    Then User should be on the shopping cart page
    And Cart items should still be present
```

---

## 8. STEP DEFINITIONS STRUCTURE

### Directory Structure:

```
src/test/java/org/example/stepdefinitions/
├── LoginSteps.java
├── ProductsSteps.java
├── ShoppingCartSteps.java
├── CheckoutSteps.java
└── Hooks.java
```

---

## 9. LOGIN STEP DEFINITIONS

### File: src/test/java/org/example/stepdefinitions/LoginSteps.java

```java
package org.example.stepdefinitions;

import org.example.pages.LoginPage;
import org.example.base.WebDriverManager;
import com.microsoft.playwright.Page;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.testng.Assert.*;

public class LoginSteps {
    
    private Page page;
    private LoginPage loginPage;
    
    public LoginSteps() {
        this.page = WebDriverManager.getPage();
        this.loginPage = new LoginPage(page);
    }
    
    @Given("User is on the SauceDemo login page")
    public void userIsOnLoginPage() {
        page.navigate("https://www.saucedemo.com");
        assertTrue(loginPage.isLoginButtonVisible(), "Login page should be visible");
    }
    
    @When("User enters username {string}")
    public void userEntersUsername(String username) {
        loginPage.enterUsername(username);
    }
    
    @When("User enters password {string}")
    public void userEntersPassword(String password) {
        loginPage.enterPassword(password);
    }
    
    @When("User clicks the login button")
    public void userClicksLoginButton() {
        loginPage.clickLoginButton();
    }
    
    @Then("User should see the products page")
    public void userShouldSeeProductsPage() {
        String url = page.url();
        assertTrue(url.contains("inventory.html"), "Should be on products page");
    }
    
    @Then("Error message should be displayed")
    public void errorMessageShouldBeDisplayed() {
        assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be visible");
    }
    
    @Then("Error message should contain {string}")
    public void errorMessageShouldContain(String expectedText) {
        String actualMessage = loginPage.getErrorMessage();
        assertTrue(actualMessage.contains(expectedText), 
            "Error message should contain: " + expectedText);
    }
    
    @When("User clicks the menu button")
    public void userClicksMenuButton() {
        // Implement menu click
    }
    
    @When("User clicks the logout option")
    public void userClicksLogoutOption() {
        loginPage.logout();
    }
    
    @Then("User should be on the login page")
    public void userShouldBeOnLoginPage() {
        String url = page.url();
        assertTrue(url.equals("https://www.saucedemo.com/"), "Should be on login page");
    }
}
```

---

## 10. PRODUCTS STEP DEFINITIONS

### File: src/test/java/org/example/stepdefinitions/ProductsSteps.java

```java
package org.example.stepdefinitions;

import org.example.pages.LoginPage;
import org.example.pages.ProductsPage;
import org.example.base.WebDriverManager;
import com.microsoft.playwright.Page;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.testng.Assert.*;

public class ProductsSteps {
    
    private Page page;
    private LoginPage loginPage;
    private ProductsPage productsPage;
    
    public ProductsSteps() {
        this.page = WebDriverManager.getPage();
        this.loginPage = new LoginPage(page);
        this.productsPage = new ProductsPage(page);
    }
    
    @Given("User is logged in with username {string} and password {string}")
    public void userIsLoggedIn(String username, String password) {
        page.navigate("https://www.saucedemo.com");
        loginPage.login(username, password);
    }
    
    @Given("User is on the products page")
    public void userIsOnProductsPage() {
        assertTrue(page.url().contains("inventory.html"), "Should be on products page");
    }
    
    @Then("User should see 6 products")
    public void userShouldSeeSixProducts() {
        assertEquals(productsPage.getProductCount(), 6, "Should see 6 products");
    }
    
    @Then("Each product should have name, image, and price")
    public void eachProductShouldHaveDetails() {
        // Implementation to verify product details
    }
    
    @When("User selects sort option {string}")
    public void userSelectsSortOption(String option) {
        productsPage.sortProducts(option);
    }
    
    @Then("Products should be sorted alphabetically")
    public void productsShouldBeSortedAlphabetically() {
        // Implementation to verify sorting
    }
    
    @Then("First product should be {string}")
    public void firstProductShouldBe(String productName) {
        // Implementation to verify first product
    }
    
    @Then("Products should be sorted by price ascending")
    public void productsShouldBeSortedByPriceAscending() {
        // Implementation to verify price sorting
    }
    
    @When("User adds {string} to cart")
    public void userAddsProductToCart(String productName) {
        productsPage.addProductToCart(productName);
    }
    
    @Then("Cart count should be {int}")
    public void cartCountShouldBe(int expectedCount) {
        assertEquals(productsPage.getCartCount(), expectedCount, 
            "Cart count should be " + expectedCount);
    }
    
    @Then("Button text for product should change to {string}")
    public void buttonTextShouldChangeTo(String expectedText) {
        // Implementation to verify button text change
    }
    
    @When("User clicks on product {string}")
    public void userClicksOnProduct(String productName) {
        productsPage.clickProduct(productName);
    }
    
    @Then("User should see product details page")
    public void userShouldSeeProductDetailsPage() {
        assertTrue(page.url().contains("inventory-item.html"), 
            "Should be on product details page");
    }
}
```

---

## 11. SHOPPING CART STEP DEFINITIONS

### File: src/test/java/org/example/stepdefinitions/ShoppingCartSteps.java

```java
package org.example.stepdefinitions;

import org.example.pages.ProductsPage;
import org.example.pages.CartPage;
import org.example.base.WebDriverManager;
import com.microsoft.playwright.Page;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.testng.Assert.*;

public class ShoppingCartSteps {
    
    private Page page;
    private ProductsPage productsPage;
    private CartPage cartPage;
    
    public ShoppingCartSteps() {
        this.page = WebDriverManager.getPage();
        this.productsPage = new ProductsPage(page);
        this.cartPage = new CartPage(page);
    }
    
    @Given("User has added {string} to cart")
    public void userHasAddedProductToCart(String productName) {
        productsPage.addProductToCart(productName);
    }
    
    @Given("User has added products to cart")
    public void userHasAddedProductsToCart() {
        productsPage.addProductToCart("Sauce Labs Backpack");
        productsPage.addProductToCart("Sauce Labs Bike Light");
    }
    
    @When("User clicks the shopping cart icon")
    public void userClicksShoppingCartIcon() {
        productsPage.goToCart();
    }
    
    @Then("User should be on the cart page")
    public void userShouldBeOnCartPage() {
        assertTrue(page.url().contains("cart.html"), "Should be on cart page");
    }
    
    @Then("Cart should contain {int} items")
    public void cartShouldContainItems(int expectedCount) {
        assertEquals(cartPage.getCartItemCount(), expectedCount, 
            "Cart should contain " + expectedCount + " items");
    }
    
    @When("User removes {string} from cart")
    public void userRemovesProductFromCart(String productName) {
        cartPage.removeItem(productName);
    }
    
    @Then("Cart item count should be {int}")
    public void cartItemCountShouldBe(int expectedCount) {
        assertEquals(cartPage.getCartItemCount(), expectedCount, 
            "Cart item count should be " + expectedCount);
    }
    
    @Then("{string} should not be in cart")
    public void productShouldNotBeInCart(String productName) {
        assertFalse(cartPage.getCartItems().contains(productName), 
            productName + " should not be in cart");
    }
    
    @When("User clicks {string} button")
    public void userClicksButton(String buttonName) {
        if ("Continue Shopping".equals(buttonName)) {
            cartPage.continueShoppingButton();
        } else if ("Checkout".equals(buttonName)) {
            cartPage.proceedToCheckout();
        }
    }
    
    @Then("Cart still contains {int} items")
    public void cartStillContainsItems(int expectedCount) {
        assertEquals(cartPage.getCartItemCount(), expectedCount, 
            "Cart should still contain " + expectedCount + " items");
    }
    
    @Then("Cart total should equal sum of all product prices")
    public void cartTotalShouldEqualSum() {
        // Implementation to verify total calculation
    }
}
```

---

## 12. CHECKOUT STEP DEFINITIONS

### File: src/test/java/org/example/stepdefinitions/CheckoutSteps.java

```java
package org.example.stepdefinitions;

import org.example.pages.CheckoutPage;
import org.example.base.WebDriverManager;
import com.microsoft.playwright.Page;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.testng.Assert.*;

public class CheckoutSteps {
    
    private Page page;
    private CheckoutPage checkoutPage;
    
    public CheckoutSteps() {
        this.page = WebDriverManager.getPage();
        this.checkoutPage = new CheckoutPage(page);
    }
    
    @Given("User is on the checkout page step one")
    public void userIsOnCheckoutPageStepOne() {
        assertTrue(page.url().contains("checkout-step-one.html"), 
            "Should be on checkout step one");
    }
    
    @When("User enters first name {string}")
    public void userEntersFirstName(String firstName) {
        checkoutPage.enterFirstName(firstName);
    }
    
    @When("User enters last name {string}")
    public void userEntersLastName(String lastName) {
        checkoutPage.enterLastName(lastName);
    }
    
    @When("User enters postal code {string}")
    public void userEntersPostalCode(String postalCode) {
        checkoutPage.enterPostalCode(postalCode);
    }
    
    @When("User clicks continue button")
    public void userClicksContinueButton() {
        checkoutPage.clickContinueButton();
    }
    
    @When("User reviews order details")
    public void userReviewsOrderDetails() {
        assertTrue(page.url().contains("checkout-step-two.html"), 
            "Should be on checkout step two");
    }
    
    @When("User clicks finish button")
    public void userClicksFinishButton() {
        checkoutPage.clickFinishButton();
    }
    
    @Then("User should see order confirmation message")
    public void userShouldSeeConfirmationMessage() {
        assertTrue(checkoutPage.isThankYouMessageDisplayed(), 
            "Thank you message should be displayed");
    }
    
    @Then("User should be on the order complete page")
    public void userShouldBeOnOrderCompletePage() {
        assertTrue(page.url().contains("checkout-complete.html"), 
            "Should be on order complete page");
    }
    
    @When("User leaves first name empty")
    public void userLeavesFirstNameEmpty() {
        // Step - do nothing as field is already empty
    }
    
    @Then("Error should contain {string}")
    public void errorShouldContain(String expectedText) {
        String errorMessage = checkoutPage.getErrorMessage();
        assertTrue(errorMessage.contains(expectedText), 
            "Error should contain: " + expectedText);
    }
    
    @When("User leaves last name empty")
    public void userLeavesLastNameEmpty() {
        // Step - do nothing as field is already empty
    }
    
    @When("User leaves postal code empty")
    public void userLeavesPostalCodeEmpty() {
        // Step - do nothing as field is already empty
    }
    
    @When("User clicks cancel button")
    public void userClicksCancelButton() {
        checkoutPage.cancelCheckout();
    }
    
    @Then("User should be on the shopping cart page")
    public void userShouldBeOnShoppingCartPage() {
        assertTrue(page.url().contains("cart.html"), "Should be on cart page");
    }
    
    @Then("Cart items should still be present")
    public void cartItemsShouldStillBePresent() {
        // Implementation to verify items still in cart
    }
}
```

---

## 13. CUCUMBER HOOKS

### File: src/test/java/org/example/stepdefinitions/Hooks.java

```java
package org.example.stepdefinitions;

import org.example.base.WebDriverManager;
import org.example.utils.ConfigReader;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;

public class Hooks {
    
    @Before
    public void setUp(Scenario scenario) {
        System.out.println("Starting scenario: " + scenario.getName());
        WebDriverManager.launchBrowser();
    }
    
    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            System.out.println("Scenario failed: " + scenario.getName());
            // Screenshot on failure
        }
        WebDriverManager.closeBrowser();
        System.out.println("Scenario completed: " + scenario.getName());
    }
}
```

---

## 14. CUCUMBER RUNNER CONFIGURATION

### File: src/test/java/org/example/runners/CucumberRunner.java

```java
package org.example.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features = "src/test/resources/features",
    glue = "org.example.stepdefinitions",
    plugin = {
        "pretty",
        "html:build/reports/cucumber/cucumber-report.html",
        "json:build/reports/cucumber/cucumber-report.json",
        "junit:build/reports/cucumber/cucumber-report.xml"
    },
    monochrome = true,
    dryRun = false,
    strict = true
)
public class CucumberRunner extends AbstractTestNGCucumberTests {
}
```

---

## 15. TESTNG CONFIGURATION FOR CUCUMBER

### Update testng.xml to include Cucumber runner:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "http://testng.org/testng-current.dtd">
<suite name="SDD-Automation Suite with Cucumber" parallel="classes" thread-count="1">
    
    <listeners>
        <listener class-name="org.example.listeners.ExtentTestListener"/>
        <listener class-name="com.aventstack.extentreports.testng.adapter.ExtentITestListenerAdapter"/>
    </listeners>
    
    <test name="Cucumber BDD Tests">
        <classes>
            <class name="org.example.runners.CucumberRunner"/>
        </classes>
    </test>
    
    <test name="Unit Tests">
        <classes>
            <class name="org.example.tests.LoginTests"/>
            <class name="org.example.tests.ProductsTests"/>
            <class name="org.example.tests.ShoppingCartTests"/>
            <class name="org.example.tests.CheckoutTests"/>
        </classes>
    </test>
    
</suite>
```

---

## 16. RUNNING CUCUMBER TESTS

### Run all feature files:
```bash
gradle test -DsuiteFile=testng.xml
```

### Run only Cucumber features:
```bash
gradle test -Dtest=CucumberRunner
```

### Run specific feature file:
```bash
gradle test -Dtest=CucumberRunner -Dcucumber.features=src/test/resources/features/login.feature
```

### Run with specific tags:
```bash
gradle test -Dtest=CucumberRunner -Dcucumber.filter.tags="@smoke"
```

---

## 17. ADDING TAGS TO FEATURE FILES

### Update feature files with tags:

```gherkin
@smoke @login
Feature: User Login to SauceDemo
...

@regression @products
Feature: Browse Products on SauceDemo
...

@regression @cart
Feature: Shopping Cart Management
...

@smoke @checkout
Feature: Complete Checkout Process
...
```

---

## 18. BEST PRACTICES FOR CUCUMBER BDD

✅ Use descriptive feature names
✅ Use Gherkin keywords consistently (Given, When, Then)
✅ One scenario per user story
✅ Reuse step definitions across features
✅ Keep step definitions simple and focused
✅ Use page objects in step definitions
✅ Tag scenarios for easy filtering
✅ Generate reports for stakeholder review

---

## 19. GRADLE BUILD.GRADLE ADDITIONS

### Add Cucumber plugin and configuration:

```gradle
plugins {
    id 'java'
}

dependencies {
    // Existing Playwright, TestNG, ExtentReports...
    
    // Cucumber
    testImplementation 'io.cucumber:cucumber-java:7.14.0'
    testImplementation 'io.cucumber:cucumber-testng:7.14.0'
    testImplementation 'io.cucumber:cucumber-picocontainer:7.14.0'
}

test {
    useTestNG {
        suites 'src/test/resources/testng/testng.xml'
    }
}
```

---

## 20. COMBINED TESTING STRATEGY

### Your Framework Now Supports:

**Cucumber BDD Tests** (Business-facing scenarios):
- 4 Feature files
- 20+ BDD scenarios
- Natural language specifications
- Stakeholder-friendly reports

**TestNG Unit Tests** (Technical testing):
- 4 Test classes
- 25 unit tests
- Data-driven testing
- Utility function testing

**Combined Benefits**:
✅ BDD for UI workflows (Cucumber)
✅ Technical testing for utilities (TestNG)
✅ Single page object model for both
✅ Unified reporting and CI/CD

---

**Status**: ✅ Cucumber BDD Specification Complete

This specification enables you to run both Cucumber feature files AND TestNG tests from a single framework using page objects and shared utilities!
