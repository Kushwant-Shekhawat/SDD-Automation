# SDD-Automation Task List

## STATUS KEY
- [ ] TODO
- [~] IN PROGRESS
- [x] DONE
- [!] BLOCKED

---

## STEP 1 — Project Foundation
**Goal**: Valid Gradle project that compiles with all dependencies

- [ ] T01 — Update `build.gradle` with all dependencies (Playwright, TestNG, Cucumber, ExtentReports, SLF4J, CSV, Jackson, AssertJ, Faker, Awaitility)
- [ ] T02 — Verify `settings.gradle` has correct project name `SDD-Automation`
- [ ] T03 — Create `gradle.properties` with JVM args and parallel settings

**Done when**: `./gradlew dependencies` exits 0

---

## STEP 2 — Core Infrastructure
**Goal**: Browser launches, page accessible, test lifecycle works

- [ ] T04 — Create `src/main/java/org/example/base/WebDriverManager.java`
  - ThreadLocal<Page> for parallel safety
  - `launchBrowser()`, `getPage()`, `closeBrowser()`
  - Reads headless/browser type from ConfigReader

- [ ] T05 — Create `src/main/java/org/example/base/BasePage.java`
  - Abstract class, receives `Page` via constructor
  - `click()`, `fill()`, `getText()`, `isVisible()`
  - `waitForElementVisible()`, `navigate()`, `getCurrentUrl()`
  - `selectDropdown()`, `takeScreenshot()`

- [ ] T06 — Create `src/test/java/org/example/base/BaseTest.java`
  - `@BeforeMethod`: calls `WebDriverManager.launchBrowser()`
  - `@AfterMethod`: screenshot on failure + `WebDriverManager.closeBrowser()`
  - `protected Page page` field accessible to test subclasses

**Done when**: `./gradlew compileJava compileTestJava` passes

---

## STEP 3 — Utilities
**Goal**: All helper classes available before page objects or tests

- [ ] T07 — Create `src/main/java/org/example/utils/ConfigReader.java`
  - Loads `config.properties` → overridden by `config-local.properties` or `config-ci.properties`
  - `getConfig(key)`, `getConfig(key, default)`, `getBoolean()`, `getInt()`
  - `getBaseUrl()`, `getBrowserType()`, `isHeadless()`, `getTimeout()`
  - `getConfig("extent.report.path")`, `getConfig("screenshot.path")`

- [ ] T08 — Create `src/main/java/org/example/utils/LoggerUtil.java`
  - Static SLF4J wrapper
  - `info(String msg)`, `warn(String msg)`, `error(String msg)`, `debug(String msg)`

- [ ] T09 — Create `src/main/java/org/example/utils/WaitUtil.java`
  - `waitForVisible(Locator, int timeoutMs)`
  - `waitForClickable(Locator, int timeoutMs)`
  - `waitForUrl(Page, String urlPattern)`

- [ ] T10 — Create `src/main/java/org/example/utils/ScreenshotUtil.java`
  - `captureScreenshot(Page page, String testName)` → returns file path string
  - Saves to `build/reports/screenshots/<testName>_<timestamp>.png`
  - Creates directories if absent

- [ ] T11 — Create `src/main/java/org/example/utils/CustomExceptions.java`
  - `FrameworkException extends RuntimeException`
  - `ElementNotFoundException extends FrameworkException`
  - `ConfigurationException extends FrameworkException`

**Done when**: `./gradlew compileJava` passes

---

## STEP 4 — Configuration & Resources
**Goal**: Framework reads correct values per environment

- [ ] T12 — Create `src/test/resources/config/config.properties`
  ```
  base.url=https://www.saucedemo.com
  browser.type=chromium
  browser.timeout=10000
  browser.headless=false
  screenshot.on.failure=true
  extent.report.path=build/reports/extentreports/index.html
  extent.report.title=SDD-Automation Test Report
  screenshot.path=build/reports/screenshots
  parallel.thread.count=4
  log.level=INFO
  ```

- [ ] T13 — Create `src/test/resources/config/config-ci.properties`
  ```
  browser.headless=true
  log.level=INFO
  parallel.thread.count=4
  ```

- [ ] T14 — Create `src/test/resources/config/config-local.properties` (gitignored)
  ```
  browser.headless=false
  log.level=DEBUG
  parallel.thread.count=1
  ```

- [ ] T15 — Create `src/test/resources/logback.xml` (SLF4J configuration)

**Done when**: `ConfigReader.getBaseUrl()` returns `https://www.saucedemo.com`

---

## STEP 5 — Page Objects
**Goal**: All SauceDemo pages wrapped with stable locators and clean methods

- [ ] T16 — Create `src/main/java/org/example/pages/LoginPage.java`
  - Locators: `[data-test="username"]`, `[data-test="password"]`, `[id="login-button"]`, `[data-test="error"]`, `#react-burger-menu-btn`, `[id="logout_sidebar_link"]`
  - Methods: `login()`, `enterUsername()`, `enterPassword()`, `clickLoginButton()`, `getErrorMessage()`, `isErrorMessageDisplayed()`, `isLoginButtonVisible()`, `logout()`

- [ ] T17 — Create `src/main/java/org/example/pages/ProductsPage.java`
  - Locators: `div.inventory_item`, `a.inventory_item_name`, `div.inventory_item_price`, `button[id*="add-to-cart"]`, `span.shopping_cart_badge`, `select.product_sort_container`
  - Methods: `addProductToCart()`, `removeProductFromCart()`, `getProductCount()`, `getProductNames()`, `sortProducts()`, `getProductPrice()`, `clickProduct()`, `getCartCount()`, `goToCart()`, `logout()`

- [ ] T18 — Create `src/main/java/org/example/pages/ProductDetailsPage.java`
  - Locators: `div.inventory_details_name`, `div.inventory_details_price`, `div.inventory_details_desc`, `button[id*="add-to-cart"]`, `[data-test="back-to-products"]`
  - Methods: `getProductName()`, `getProductPrice()`, `getProductDescription()`, `addToCart()`, `backToProducts()`, `goToCart()`

- [ ] T19 — Create `src/main/java/org/example/pages/CartPage.java`
  - Locators: `div.cart_item`, `a.inventory_item_name`, `div.inventory_item_price`, `button[id*="remove"]`, `[data-test="continue-shopping"]`, `[data-test="checkout"]`
  - Methods: `getCartItems()`, `removeItem()`, `getCartTotal()`, `getCartItemCount()`, `isCartEmpty()`, `continueShoppingButton()`, `proceedToCheckout()`

- [ ] T20 — Create `src/main/java/org/example/pages/CheckoutPage.java`
  - Step 1 locators: `[data-test="firstName"]`, `[data-test="lastName"]`, `[data-test="postalCode"]`, `[data-test="continue"]`, `[data-test="cancel"]`, `[data-test="error"]`
  - Step 2 locators: `div.summary_subtotal_label`, `div.summary_tax_label`, `div.summary_total_label`, `[data-test="finish"]`
  - Step 3 locators: `h2.complete-header`, `[data-test="back-to-products"]`
  - Methods: `enterFirstName()`, `enterLastName()`, `enterPostalCode()`, `clickContinueButton()`, `cancelCheckout()`, `getErrorMessage()`, `isErrorMessageDisplayed()`, `getOrderTotal()`, `clickFinishButton()`, `isThankYouMessageDisplayed()`, `getThankYouMessage()`

**Done when**: `./gradlew compileJava` passes

---

## STEP 6 — Test Data
**Goal**: Data-driven tests have structured CSV input

- [ ] T21 — Create `src/main/java/org/example/utils/TestDataProvider.java`
  - `getLoginData()` → `Object[][]` from `login-data.csv`
  - `getProductData()` → `Object[][]` from `product-data.csv`
  - `getCheckoutData()` → `Object[][]` from `checkout-data.csv`
  - `getInvalidCheckoutData()` → `Object[][]` from `invalid-checkout-data.csv`

- [ ] T22 — Create `src/test/resources/testdata/login-data.csv`
  - Headers: `username,password,expectedResult,description`
  - 8 rows: standard_user, locked_user, problem_user, invalid password, empty username, empty password, empty both, SQL injection attempt

- [ ] T23 — Create `src/test/resources/testdata/product-data.csv`
  - Headers: `productName,price`
  - 6 rows: all actual SauceDemo products with prices

- [ ] T24 — Create `src/test/resources/testdata/cart-data.csv`
  - Headers: `testCase,productName,expectedCount`
  - 6 rows: single item, multiple items, remove item scenarios

- [ ] T25 — Create `src/test/resources/testdata/checkout-data.csv`
  - Headers: `firstName,lastName,postalCode,expectedResult,description`
  - 6 rows with valid checkout combinations

- [ ] T26 — Create `src/test/resources/testdata/invalid-checkout-data.csv`
  - Headers: `firstName,lastName,postalCode,expectedError,description`
  - 3 rows: empty first name, empty last name, empty postal code

**Done when**: `TestDataProvider.getLoginData()` returns 8 rows

---

## STEP 7 — TestNG Test Classes
**Goal**: 25 tests covering all SauceDemo flows

- [ ] T27 — Create `src/test/java/org/example/tests/LoginTests.java`
  - 7-8 tests: valid login, locked user, invalid password, empty username, empty password, logout, session persistence
  - Groups: `smoke`, `regression`, `positive`, `negative`
  - Data-driven tests use `@DataProvider` from `TestDataProvider`

- [ ] T28 — Create `src/test/java/org/example/tests/ProductsTests.java`
  - 7 tests: view products, sort A-Z, sort price, add to cart, multiple items, view details, cart count
  - Groups: `smoke`, `regression`

- [ ] T29 — Create `src/test/java/org/example/tests/ShoppingCartTests.java`
  - 6 tests: view cart, remove item, continue shopping, cart total, cart empty, proceed to checkout
  - Groups: `regression`

- [ ] T30 — Create `src/test/java/org/example/tests/CheckoutTests.java`
  - 5 tests: complete checkout, empty first name, empty last name, empty postal code, cancel checkout
  - Groups: `smoke`, `regression`

**Done when**: `./gradlew compileTestJava` passes

---

## STEP 8 — Reporting & TestNG XML
**Goal**: HTML report generated after every run, screenshots on failure

- [ ] T31 — Create `src/test/java/org/example/listeners/ExtentTestListener.java`
  - Implements `ITestListener`
  - `onStart`: initialize ExtentReports (synchronized, null check)
  - `onTestStart`: create test node, assign categories from groups
  - `onTestFailure`: log exception + capture and attach screenshot
  - `onTestSkipped`: log skipped
  - `onFinish`: flush report
  - `static getTest()` for use in step definitions

- [ ] T32 — Create `src/test/resources/testng/testng.xml`
  - `parallel="none"`, `thread-count="1"`
  - Listener: `org.example.listeners.ExtentTestListener`
  - Includes: CucumberRunner, LoginTests, ProductsTests, ShoppingCartTests, CheckoutTests

- [ ] T33 — Create `src/test/resources/testng/testng-parallel.xml`
  - `parallel="methods"`, `thread-count="4"`
  - Same classes as testng.xml

- [ ] T34 — Create `src/test/resources/testng/testng-smoke.xml`
  - Groups include: `smoke`
  - Same classes as testng.xml

**Done when**: Run produces `build/reports/extentreports/index.html`

---

## STEP 9 — Cucumber BDD
**Goal**: 20+ BDD scenarios run end-to-end and produce Cucumber HTML report

- [ ] T35 — Create `src/test/resources/features/login.feature`
  - 4 scenarios: valid login, invalid username, locked account, logout

- [ ] T36 — Create `src/test/resources/features/products.feature`
  - 6 scenarios: all products displayed, sort A-Z, sort by price, add single, add multiple, view details

- [ ] T37 — Create `src/test/resources/features/shopping_cart.feature`
  - 5 scenarios: view cart, remove item, continue shopping, cart total, proceed to checkout

- [ ] T38 — Create `src/test/resources/features/checkout.feature`
  - 5 scenarios: complete checkout, empty first name, empty last name, empty postal code, cancel

- [ ] T39 — Create `src/test/java/org/example/stepdefinitions/Hooks.java`
  - `@Before`: `WebDriverManager.launchBrowser()`
  - `@After`: screenshot on failure + `WebDriverManager.closeBrowser()`

- [ ] T40 — Create `src/test/java/org/example/stepdefinitions/LoginSteps.java`
  - All @Given/@When/@Then for login.feature

- [ ] T41 — Create `src/test/java/org/example/stepdefinitions/ProductsSteps.java`
  - All @Given/@When/@Then for products.feature

- [ ] T42 — Create `src/test/java/org/example/stepdefinitions/ShoppingCartSteps.java`
  - All @Given/@When/@Then for shopping_cart.feature

- [ ] T43 — Create `src/test/java/org/example/stepdefinitions/CheckoutSteps.java`
  - All @Given/@When/@Then for checkout.feature

- [ ] T44 — Create `src/test/java/org/example/runners/CucumberRunner.java`
  - `@CucumberOptions`: features=`src/test/resources/features`, glue=`org.example.stepdefinitions`
  - Plugins: `pretty`, `html:build/reports/cucumber/cucumber-report.html`, `json`, `junit`
  - Extends `AbstractTestNGCucumberTests`

**Done when**: `./gradlew test` produces `build/reports/cucumber/cucumber-report.html`

---

## STEP 10 — Build, Run & Verify
**Goal**: All tests pass, both reports generated

- [ ] T45 — Run `./gradlew clean build` — fix any compile errors
- [ ] T46 — Run `./gradlew test` — fix any test failures
- [ ] T47 — Verify `build/reports/extentreports/index.html` exists and opens
- [ ] T48 — Verify `build/reports/cucumber/cucumber-report.html` exists and opens
- [ ] T49 — Run smoke suite: `./gradlew test -DsuiteFile=src/test/resources/testng/testng-smoke.xml`
- [ ] T50 — Commit all generated files to GitHub

---

## SUMMARY

| Step | Tasks | Files |
|------|-------|-------|
| 1 — Foundation | T01–T03 | 3 |
| 2 — Core Infra | T04–T06 | 3 |
| 3 — Utilities | T07–T11 | 5 |
| 4 — Config | T12–T15 | 4 |
| 5 — Page Objects | T16–T20 | 5 |
| 6 — Test Data | T21–T26 | 6 |
| 7 — TestNG Tests | T27–T30 | 4 |
| 8 — Reporting | T31–T34 | 4 |
| 9 — Cucumber | T35–T44 | 10 |
| 10 — Verify | T45–T50 | — |
| **Total** | **50 tasks** | **44 files** |

---

## CURRENT STATUS: Ready to implement T01
