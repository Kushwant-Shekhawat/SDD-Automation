# SDD-Automation Framework - Architecture Specification (CORRECTED)

## 1. PROJECT OVERVIEW

**Target Application**: SauceDemo (https://www.saucedemo.com)
**Framework Name**: SDD-Automation
**Technology Stack**: Java 11 + Gradle + Playwright + TestNG + ExtentReports + Cucumber
**Architecture Pattern**: Page Object Model (POM)
**Execution Models**: Sequential, Parallel, Data-Driven, BDD

---

## 2. FOLDER STRUCTURE

```
SDD-Automation/
├── src/main/java/org/example/
│   ├── base/
│   │   ├── BasePage.java
│   │   └── WebDriverManager.java
│   ├── pages/
│   │   ├── LoginPage.java
│   │   ├── ProductsPage.java
│   │   ├── ProductDetailsPage.java
│   │   ├── CartPage.java
│   │   └── CheckoutPage.java
│   └── utils/
│       ├── ConfigReader.java
│       ├── LoggerUtil.java
│       ├── ScreenshotUtil.java
│       ├── WaitUtil.java
│       ├── TestDataProvider.java
│       └── CustomExceptions.java
│
├── src/test/java/org/example/
│   ├── base/
│   │   └── BaseTest.java
│   ├── tests/
│   │   ├── LoginTests.java
│   │   ├── ProductsTests.java
│   │   ├── ShoppingCartTests.java
│   │   └── CheckoutTests.java
│   ├── runners/
│   │   └── CucumberRunner.java
│   ├── stepdefinitions/
│   │   ├── LoginSteps.java
│   │   ├── ProductsSteps.java
│   │   ├── ShoppingCartSteps.java
│   │   ├── CheckoutSteps.java
│   │   └── Hooks.java
│   └── listeners/
│       └── ExtentTestListener.java
│
├── src/test/resources/
│   ├── config/
│   │   ├── config.properties
│   │   ├── config-local.properties
│   │   └── config-ci.properties
│   ├── testdata/
│   │   ├── login-data.csv
│   │   ├── product-data.csv
│   │   ├── cart-data.csv
│   │   ├── checkout-data.csv
│   │   └── invalid-checkout-data.csv
│   ├── features/
│   │   ├── login.feature
│   │   ├── products.feature
│   │   ├── shopping_cart.feature
│   │   └── checkout.feature
│   └── testng/
│       ├── testng.xml
│       ├── testng-parallel.xml
│       └── testng-smoke.xml
│
├── build.gradle
├── settings.gradle
├── gradle.properties
├── .gitignore
└── README.md
```

---

## 3. BUILD CONFIGURATION

### Gradle Dependencies (Key additions for Cucumber):

```
Java 11 compatible libraries:
- Playwright: 1.40.0
- TestNG: 7.7.0
- ExtentReports: 5.0.9
- Cucumber: 7.14.0
- SLF4J + Logback for logging
- AssertJ for assertions
- Jackson for JSON
- Apache Commons CSV
- Faker for test data
```

Add to build.gradle:

```gradle
plugins {
    id 'java'
}

group = 'org.example'
version = '1.0-SNAPSHOT'

sourceCompatibility = '11'
targetCompatibility = '11'

repositories {
    mavenCentral()
}

dependencies {
    // Playwright (used in src/main — BasePage, WebDriverManager)
    implementation 'com.microsoft.playwright:playwright:1.40.0'

    // Logging (used in src/main — LoggerUtil)
    implementation 'org.slf4j:slf4j-api:1.7.36'
    implementation 'ch.qos.logback:logback-classic:1.2.11'

    // Data utilities (used in src/main — TestDataProvider, utils)
    implementation 'commons-csv:commons-csv:1.10.0'
    implementation 'com.fasterxml.jackson.core:jackson-databind:2.15.2'

    // Test dependencies
    testImplementation 'org.testng:testng:7.7.0'
    testImplementation 'com.aventstack:extentreports:5.0.9'
    testImplementation 'com.aventstack:extentreports-testng-adapter:1.10.10'
    testImplementation 'io.cucumber:cucumber-java:7.14.0'
    testImplementation 'io.cucumber:cucumber-testng:7.14.0'
    testImplementation 'io.cucumber:cucumber-picocontainer:7.14.0'
    testImplementation 'org.assertj:assertj-core:3.24.1'
    testImplementation 'com.github.javafaker:javafaker:1.0.2'
    testImplementation 'org.awaitility:awaitility:4.1.1'
}

test {
    useTestNG {
        suites 'src/test/resources/testng/testng.xml'
    }
}
```

---

## 4. CORE CLASSES

### WebDriverManager.java
- Singleton pattern for thread-safe driver management
- Playwright browser initialization
- Support for headless/headed modes
- Screenshot and video recording
- ThreadLocal for parallel execution
- Methods: launchBrowser(), getPage(), closeBrowser(), takeScreenshot()

### BasePage.java
- Abstract base class for all page objects
- Protected methods: click(), fill(), selectDropdown(), getText()
- Wait methods: waitForElement(), waitForElementVisible(), waitForElementClickable()
- Navigation: navigate(), getCurrentUrl()
- All using Playwright Locator and Page objects

### BaseTest.java
- TestNG base class
- @BeforeMethod: Initialize WebDriverManager
- @AfterMethod: Cleanup and screenshot on failure
- ExtentReports integration
- Retry mechanism support

---

## 5. PAGE OBJECTS

### LoginPage.java
Locators: username input, password input, login button, error message, menu, logout
Methods: login(), enterUsername(), enterPassword(), clickLoginButton(), getErrorMessage(), logout()

### ProductsPage.java
Locators: product items, add to cart buttons, cart badge, sort dropdown, cart link
Methods: addProductToCart(), getProductCount(), sortProducts(), getProductPrice(), goToCart()

### ProductDetailsPage.java
Locators: product name, price, description, add to cart button, back button
Methods: getProductName(), getProductPrice(), addToCart(), backToProducts()

### CartPage.java
Locators: cart items, remove buttons, cart total, checkout button
Methods: getCartItems(), removeItem(), getCartTotal(), proceedToCheckout()

### CheckoutPage.java
Locators: form fields (firstName, lastName, postalCode), continue button, finish button, thank you message
Methods: enterFirstName(), enterLastName(), enterPostalCode(), clickFinishButton(), isThankYouMessageDisplayed()

---

## 6. UTILITY CLASSES

### ConfigReader.java
Reads config.properties, config-local.properties, config-ci.properties
Methods: getConfig(), getConfigBoolean(), getConfigInt(), getBaseUrl(), isHeadless(), etc.

### LoggerUtil.java
Static logging methods: info(), warning(), error(), debug()
Uses SLF4J + Logback

### ScreenshotUtil.java
Captures screenshots on demand
Saves to build/reports/screenshots/
Attaches to ExtentReports

### WaitUtil.java
Explicit wait methods using Playwright
waitForSeconds(), waitForElementVisible(), waitForElementClickable()

### TestDataProvider.java
Reads CSV files from src/test/resources/testdata/
Methods: getLoginData(), getProductData(), getCheckoutData()
Returns Object[][] for TestNG @DataProvider

### CustomExceptions.java
FrameworkException, ElementNotFoundException, TimeoutException, ConfigurationException

---

## 7. CONFIGURATION FILES

### config.properties (Default)
```
base.url=https://www.saucedemo.com
browser.type=chromium
browser.timeout=10000
browser.headless=false
log.level=INFO
screenshot.on.failure=true
extent.report.path=build/reports/extentreports/index.html
```

### config-local.properties (Local Development)
```
browser.headless=false
log.level=DEBUG
parallel.thread.count=1
```

### config-ci.properties (CI/CD)
```
browser.headless=true
log.level=INFO
parallel.thread.count=4
```

---

## 8. TEST DATA (CSV FILES)

### login-data.csv
Headers: username, password, expectedResult, description
8 rows: standard_user, locked_user, invalid_user, empty fields, etc.

### product-data.csv
Headers: productId, productName, price, category, description, availability
6 rows with actual SauceDemo products

### cart-data.csv
Headers: testCase, productNames, quantities, expectedCount, expectedTotal, operation
6 rows with different cart scenarios

### checkout-data.csv
Headers: firstName, lastName, postalCode, expectedResult, description, testUser
6 rows with valid checkout data

### invalid-checkout-data.csv
Headers: firstName, lastName, postalCode, expectedError, description
Rows for negative testing (empty fields)

---

## 9. TEST CLASSES (TestNG)

### LoginTests.java
7-8 test methods covering login scenarios
Data-driven using @DataProvider
Groups: smoke, regression

### ProductsTests.java
7 test methods for browsing, sorting, adding products
Groups: smoke, regression

### ShoppingCartTests.java
6 test methods for cart operations
Groups: regression

### CheckoutTests.java
5 test methods for checkout flow
Groups: smoke, regression

**Total**: 25 TestNG tests + 20+ Cucumber BDD scenarios

---

## 10. CUCUMBER BDD (Feature Files)

### login.feature
Scenarios: Valid login, invalid login, locked user, logout

### products.feature
Scenarios: Browse products, sort products, add to cart, view details

### shopping_cart.feature
Scenarios: View cart, remove items, continue shopping, checkout

### checkout.feature
Scenarios: Complete checkout, form validation, cancel checkout

---

## 11. STEP DEFINITIONS

### LoginSteps.java
@Given, @When, @Then methods for login scenarios
Uses LoginPage page object

### ProductsSteps.java
Step definitions for product browsing
Uses ProductsPage page object

### ShoppingCartSteps.java
Step definitions for cart operations
Uses CartPage page object

### CheckoutSteps.java
Step definitions for checkout flow
Uses CheckoutPage page object

### Hooks.java
@Before: Launch browser
@After: Close browser, capture screenshot on failure

---

## 12. TEST RUNNERS

### CucumberRunner.java
Runs all .feature files from src/test/resources/features/
Generates HTML, JSON, JUnit reports
Uses PicoContainer for dependency injection

---

## 13. TESTNG CONFIGURATION

### testng.xml (Sequential)
xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "http://testng.org/testng-current.dtd">
<suite name="SDD-Automation Suite" parallel="none" thread-count="1">

  <listeners>
    <listener class-name="org.example.listeners.ExtentTestListener"/>
  </listeners>

  <test name="Cucumber Tests">
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

### testng-parallel.xml (Parallel)
Thread-count=4 for parallel execution

### testng-smoke.xml (Smoke Only)
Includes only @smoke tagged tests

---

## 14. EXECUTION MODELS

### Sequential Execution
gradle test
Expected: ~5 minutes for all tests

### Parallel Execution
```bash
./gradlew test -DsuiteFile=src/test/resources/testng/testng-parallel.xml
```
Expected: ~2 minutes for all tests

### Smoke Tests Only
```bash
./gradlew test -DsuiteFile=src/test/resources/testng/testng-smoke.xml
```
Expected: ~45 seconds

### Cucumber Features Only
```bash
./gradlew test -DsuiteFile=src/test/resources/testng/testng.xml -Dcucumber.filter.tags="@smoke"
```
Expected: ~3 minutes

---

## 15. REPORTING

### ExtentReports
- HTML dashboard: build/reports/extentreports/index.html
- Automatic screenshot attachment on failure
- Test categorization by groups
- Execution timeline
- System information
- Pass/Fail/Skip metrics

### Cucumber Reports
- HTML report: build/reports/cucumber/cucumber-report.html
- JSON format for integration
- JUnit XML for CI/CD

---

## 16. CI/CD INTEGRATION

### GitHub Actions Compatible
```bash
gradle clean test
```

### Jenkins Compatible
Uses JUnit XML and HTML reports

### Environment Variables
CI=true triggers config-ci.properties
Parallel execution enabled automatically

---

## 17. QUALITY STANDARDS

- Code coverage: 80% minimum
- Flaky test rate: <5% (target <2%)
- Execution time: <5 minutes sequential, <2 minutes parallel
- All code: JavaDoc comments
- Checkstyle: Google style guide
- No hardcoded values: All via ConfigReader

---

## 18. COMBINED STRATEGY

**Your Framework Has Two Test Types**:

1. **Cucumber BDD Tests** (Stakeholder-facing)
   - Feature files in plain English
   - Business-readable scenarios
   - 20+ scenarios across 4 features
   - Natural language test specifications

2. **TestNG Unit Tests** (Technical testing)
   - Data-driven tests
   - Utility function testing
   - Advanced assertions
   - 25 tests total

**Shared Resources**:
- Same Page Objects for both
- Same Base classes
- Same Configuration
- Same Reporting
- Same CI/CD pipeline

---

## 19. QUICK START COMMANDS

```bash
# Navigate to project
cd /Users/kushwantsinghshekhawat/SDD-Automation

# Start Claude Code
claude --here

# In Claude Code terminal:
> Read @specs/01-framework-architecture.md and create build.gradle with all dependencies including Cucumber

> Create WebDriverManager.java, BasePage.java, BaseTest.java

> Create all 5 page objects: LoginPage, ProductsPage, ProductDetailsPage, CartPage, CheckoutPage

> Create 6 utility classes: ConfigReader, LoggerUtil, ScreenshotUtil, WaitUtil, TestDataProvider, CustomExceptions

> Create 4 feature files: login.feature, products.feature, shopping_cart.feature, checkout.feature

> Create 5 step definition classes: LoginSteps, ProductsSteps, ShoppingCartSteps, CheckoutSteps, Hooks

> Create CucumberRunner.java

> Create 4 TestNG test classes: LoginTests, ProductsTests, ShoppingCartTests, CheckoutTests with all 25 tests

> Create ExtentTestListener.java for reporting

> Create 3 TestNG XML files: testng.xml, testng-parallel.xml, testng-smoke.xml

> Create 3 config files: config.properties, config-local.properties, config-ci.properties

> Create 5 CSV files: login-data.csv, product-data.csv, cart-data.csv, checkout-data.csv, invalid-checkout-data.csv

> Run: gradle clean build

> Run: gradle test
```

---

## 20. SUCCESS METRICS

After generation:
- 40+ Java files created
- 4 Feature files created
- 5 CSV data files created
- 3 Config files created
- 3 TestNG XML files created
- Gradle build successful
- All 25 TestNG tests passing
- All 20+ Cucumber scenarios passing
- ExtentReports generated
- Cucumber HTML reports generated
- 100% code generated from specifications

---

**Status**: ✅ Framework Architecture Specification - CORRECTED and SIMPLIFIED
