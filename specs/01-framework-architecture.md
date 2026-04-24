# SDD-Automation Framework - Architecture Specification

## 1. PROJECT OVERVIEW

**Target Application**: SauceDemo (https://www.saucedemo.com)
**Framework Name**: SDD-Automation
**Technology Stack**: Java 11 + Gradle + Playwright 1.51.0 + TestNG 7.7.0 + ExtentReports 5.0.9 + Cucumber 7.14.0
**Architecture Pattern**: Page Object Model (POM)
**Test Strategy**: Cucumber BDD only — 69 scenarios across 11 feature files
**Execution Models**: Parallel (4 threads), headless/headed, cross-browser

---

## 2. FOLDER STRUCTURE

```
SDD-Automation/
├── src/main/java/org/example/
│   ├── driver/
│   │   └── WebDriverManager.java        # ThreadLocal Playwright lifecycle
│   ├── pages/
│   │   ├── BasePage.java
│   │   ├── LoginPage.java
│   │   ├── ProductsPage.java
│   │   ├── ProductDetailsPage.java
│   │   ├── CartPage.java
│   │   ├── CheckoutPage.java
│   │   └── NavigationComponent.java
│   ├── data/
│   │   └── TestDataProvider.java
│   └── utils/
│       ├── ConfigReader.java
│       ├── LoggerUtil.java
│       ├── ScreenshotUtil.java
│       ├── WaitUtil.java
│       ├── VisualCompareUtil.java
│       └── CustomExceptions.java
│
├── src/test/java/org/example/
│   ├── base/
│   │   └── BaseTest.java
│   ├── runners/
│   │   └── CucumberRunner.java          # @DataProvider(parallel=true), 4 threads
│   ├── stepdefs/                         # PicoContainer DI via SharedContext
│   │   ├── SharedContext.java
│   │   ├── Hooks.java
│   │   ├── LoginSteps.java
│   │   ├── ProductSteps.java
│   │   ├── CartSteps.java
│   │   ├── CheckoutSteps.java
│   │   ├── NavigationSteps.java
│   │   └── VisualSteps.java
│   └── listeners/
│       └── ExtentTestListener.java      # Custom ITestListener
│
├── src/test/resources/
│   ├── config/
│   │   ├── config.properties            # Committed defaults
│   │   └── config-local.properties      # Gitignored per-dev overrides
│   ├── testdata/
│   │   ├── login-data.csv
│   │   ├── product-data.csv
│   │   ├── cart-data.csv
│   │   ├── checkout-data.csv
│   │   └── invalid-checkout-data.csv
│   ├── features/
│   │   ├── login.feature
│   │   ├── products.feature
│   │   ├── cart.feature
│   │   ├── checkout.feature
│   │   ├── checkout_validation.feature
│   │   ├── logout.feature
│   │   ├── product_details.feature
│   │   ├── sorting.feature
│   │   ├── multi_item_cart.feature
│   │   ├── negative_flows.feature
│   │   └── visual_regression.feature    # @visual tag — excluded from CI
│   ├── visual-baselines/                # Baseline PNGs for visual regression
│   └── testng/
│       └── testng.xml                   # data-provider-thread-count="4"
│
├── .github/workflows/ci.yml             # Chromium + cross-browser matrix
├── CLAUDE.md
├── build.gradle
├── settings.gradle
├── gradle.properties
└── .gitignore
```

---

## 3. BUILD CONFIGURATION

### Actual dependency versions in use:

```
- Playwright:           1.51.0
- TestNG:               7.7.0
- ExtentReports:        5.0.9  (custom ITestListener — no adapter)
- Cucumber:             7.14.0
- cucumber-picocontainer: 7.14.0
- SLF4J + Logback:      1.7.36 / 1.2.11
- AssertJ:              3.24.1
- Jackson:              2.15.2
- Apache Commons CSV:   1.10.0  (groupId: org.apache.commons)
- JavaFaker:            1.0.2
- Awaitility:           4.1.1
- Masterthought cucumber-reporting: 5.7.5 (buildscript classpath only)
```

### build.gradle summary:

```gradle
buildscript {
    repositories { mavenCentral() }
    dependencies {
        classpath('net.masterthought:cucumber-reporting:5.7.5') {
            exclude group: 'com.fasterxml.jackson.core'
        }
        classpath 'com.fasterxml.jackson.core:jackson-databind:2.15.2'
    }
}

dependencies {
    implementation 'com.microsoft.playwright:playwright:1.51.0'
    implementation 'org.slf4j:slf4j-api:1.7.36'
    implementation 'ch.qos.logback:logback-classic:1.2.11'
    implementation 'org.apache.commons:commons-csv:1.10.0'
    implementation 'com.fasterxml.jackson.core:jackson-databind:2.15.2'

    testImplementation 'org.testng:testng:7.7.0'
    testImplementation 'com.aventstack:extentreports:5.0.9'
    testImplementation 'io.cucumber:cucumber-java:7.14.0'
    testImplementation 'io.cucumber:cucumber-testng:7.14.0'
    testImplementation 'io.cucumber:cucumber-picocontainer:7.14.0'
    testImplementation 'org.assertj:assertj-core:3.24.1'
    testImplementation 'com.github.javafaker:javafaker:1.0.2'
    testImplementation 'org.awaitility:awaitility:4.1.1'
}
```

---

## 4. CORE CLASSES

### WebDriverManager.java
- Package: `org.example.driver`
- Static ThreadLocal lifecycle — no singleton instance
- `initBrowser()` — launches Playwright + browser + context + page, stores all in ThreadLocals
- `getPage()` — returns the current thread's Page
- `closeBrowser()` — closes page, context, browser, playwright; removes all ThreadLocals
- Supports chromium / firefox / webkit via `browser.type` config key
- Headless mode and `browser.slow.mo` controlled via ConfigReader

### BasePage.java
- Package: `org.example.pages`
- Abstract base class for all page objects
- Constructor: `BasePage(Page page)` — also reads `browser.timeout` via ConfigReader
- Protected methods: `click(Locator)`, `fill(Locator, String)`, `getText(Locator)`, `isVisible(Locator)`, `isVisible(Locator, int timeoutMs)`
- Navigation: `navigateTo(String path)` — prepends base URL from ConfigReader
- Page load: `waitForPageLoad()` — calls `page.waitForLoadState()`
- All element interactions delegate to WaitUtil for visibility before acting

### BaseTest.java
- Package: `org.example.base`
- TestNG lifecycle class — used only by CucumberRunner (no standalone TestNG test classes)
- `@BeforeMethod setUp(Method)` — calls `WebDriverManager.initBrowser()`, sets `page` field
- `@AfterMethod tearDown(ITestResult)` — captures failure screenshot, calls `WebDriverManager.closeBrowser()`
- `@BeforeTest setBrowser(@Optional String)` — sets `browser.type` system property for cross-browser runs

---

## 5. PAGE OBJECTS

All in package `org.example.pages`. Full locator and method specifications are in `02-page-object-model.md`.

### LoginPage.java
Key locators: `[data-test="username"]`, `[data-test="password"]`, `[id="login-button"]`, `[data-test="error"]`
Key methods: `login(username, password)`, `enterUsername()`, `enterPassword()`, `clickLoginButton()`, `getErrorMessage()`, `isLoginButtonVisible()`, `isLoginPageDisplayed()`

### ProductsPage.java
Key locators: `.inventory_item`, `.product_sort_container`, `span.shopping_cart_badge`, `a.shopping_cart_link`
Key methods: `addProductToCart(productName)`, `removeProductFromCart(productName)`, `getProductCount()`, `sortProducts(sortOption)`, `goToCart()`, `getCartCount()`, `getFirstProductName()`, `arePricesInAscendingOrder()`

### ProductDetailsPage.java
Key locators: `.inventory_details_name`, `.inventory_details_price`, `[data-test*="add-to-cart"]`, `[data-test="back-to-products"]`
Key methods: `getProductName()`, `getProductPrice()`, `addToCart()`, `removeFromCart()`, `backToProducts()`, `isAddToCartButtonVisible()`

### CartPage.java
Key locators: `div.cart_item`, `[data-test="checkout"]`, `[data-test="continue-shopping"]`
Key methods: `getCartItems()`, `removeItem(itemName)`, `getCartItemCount()`, `isCartEmpty()`, `proceedToCheckout()`, `continueShoppingButton()`

### CheckoutPage.java
Covers three pages: step-one, step-two, checkout-complete
Key methods: `enterFirstName()`, `enterLastName()`, `enterPostalCode()`, `clickContinueButton()`, `cancelCheckout()`, `isErrorMessageDisplayed()`, `getErrorMessage()`, `clickFinishButton()`, `isThankYouMessageDisplayed()`, `getOrderTotal()`, `dismissError()`

### NavigationComponent.java
Key locators: `#react-burger-menu-btn`, `.bm-menu-wrap`, `#logout_sidebar_link`, `#reset_sidebar_link`
Key methods: `openMenu()`, `closeMenu()`, `logout()`, `resetAppState()`, `isMenuVisible()`

---

## 6. UTILITY CLASSES

All in package `org.example.utils`.

### ConfigReader.java
- Loads `config.properties` first, then env-specific override (`config/config-local.properties` or `config/config-ci.properties` based on `ENV` system property or env var)
- System properties override all file values
- Methods: `getConfig(key)`, `getConfig(key, default)`, `getBoolean(key)`, `getBoolean(key, default)`, `getInt(key)`, `getInt(key, default)`, `getBaseUrl()`, `getBrowserType()`, `isHeadless()`, `getTimeout()`

### LoggerUtil.java
- Static wrapper over SLF4J Logger
- Methods: `info(msg)`, `warn(msg)`, `error(msg)`, `error(msg, throwable)`, `debug(msg)`

### ScreenshotUtil.java
- Run-level folder: `build/reports/screenshots/<timestamp>/` (created once per JVM run, synchronized)
- Scenario-level sub-folder: one per scenario, named from sanitized scenario name
- `initScenarioFolder(scenarioName)` — creates folder, resets step counter and last-failure path
- `captureStepScreenshot(page)` — saves `step_NN.png`, increments counter, returns absolute path
- `captureFailureScreenshot(page, scenarioName)` — saves `Failed_Step_<name>.png`
- `cleanupScenario()` — removes ThreadLocals (called in `@After` Hooks)
- All methods are ThreadLocal-safe for parallel execution

### WaitUtil.java
- Playwright-native explicit waits; no `Thread.sleep()`
- `waitForVisible(locator)` — waits up to configured timeout for VISIBLE state
- `waitForVisible(locator, int timeoutMs)` — overload with custom timeout

### TestDataProvider.java
- Reads CSV files from `src/test/resources/testdata/` via Apache Commons CSV
- Returns parsed rows for use in Cucumber step definitions

### VisualCompareUtil.java
- `compareScreenshot(page, baselineName)` — takes screenshot, compares pixel-by-pixel against baseline PNG
- Baseline directory: `src/test/resources/visual-baselines/`
- Diff output directory: `build/reports/visual/diffs/`
- Threshold: **3%** pixel difference — if no baseline exists, captures and saves it automatically
- Used exclusively by `VisualSteps.java` (tagged `@visual`)

### CustomExceptions.java
- Inner static classes: `FrameworkException`, `ElementNotFoundException`, `TimeoutException`, `ConfigurationException`

---

## 7. CONFIGURATION FILES

Config files live in `src/test/resources/config/`. The root `config.properties` (no subdirectory) is the primary file also loaded from classpath root.

### config.properties (Primary defaults)
```
base.url=https://www.saucedemo.com
browser.type=chromium
browser.headless=false
browser.timeout=10000
browser.slow.mo=0
user.standard=standard_user
user.locked=locked_out_user
user.problem=problem_user
user.performance=performance_glitch_user
user.password=secret_sauce
screenshot.on.failure=true
screenshot.path=build/reports/screenshots
extent.report.path=build/reports/extent/ExtentReport.html
extent.report.title=SauceDemo Automation Report
extent.report.name=SDD Automation Suite
cucumber.report.path=build/reports/cucumber
retry.count=1
```

### config/config-local.properties (Local override)
```
browser.headless=true
browser.timeout=10000
browser.slow.mo=0
```

### config/config-ci.properties (CI override — activated by ENV=ci)
```
browser.headless=true
browser.timeout=15000
screenshot.on.failure=true
```

---

## 8. TEST DATA (CSV FILES)

All files in `src/test/resources/testdata/`.

### login_valid.csv
Headers: `username, password, expectedResult`
2 rows: standard_user, performance_glitch_user (both succeed)

### login_invalid.csv
Headers: `username, password, expectedError`
5 rows: locked_out_user, wrong password, invalid user, empty username, empty password

### products.csv
Headers: `productName, price, description`
6 rows — all 6 SauceDemo products with actual prices

### checkout.csv
Headers: `firstName, lastName, postalCode`
3 rows with valid checkout data

### checkout_invalid.csv
Headers: `firstName, lastName, postalCode, expectedError`
3 rows: missing first name, missing last name, missing postal code

---

## 9. TEST EXECUTION STRATEGY

This framework is **Cucumber-only**. There are no standalone TestNG test classes (no LoginTests, ProductsTests, etc.). All 69 scenarios run through `CucumberRunner`, which extends `AbstractTestNGCucumberTests`.

Parallelism is at the Cucumber scenario level via `@DataProvider(parallel = true)` with `data-provider-thread-count="4"` in `testng.xml`. Each thread gets its own Playwright browser instance via ThreadLocal.

---

## 10. CUCUMBER BDD (Feature Files)

All 11 feature files in `src/test/resources/features/`. Total: **69 scenarios**.

| Feature File | Scenarios | Tags |
|---|---|---|
| `login.feature` | 7 | @smoke, @regression |
| `products.feature` | 6 | @smoke, @regression |
| `cart.feature` | 4 | @regression |
| `checkout.feature` | 6 | @smoke, @regression |
| `checkout_validation.feature` | 9 | @regression |
| `logout.feature` | 4 | @regression |
| `product_details.feature` | 5 | @regression |
| `sorting.feature` | 6 | @regression |
| `multi_item_cart.feature` | 6 | @regression |
| `negative_flows.feature` | 5 | @regression |
| `visual_regression.feature` | 6 | @visual (excluded from CI) |

---

## 11. STEP DEFINITIONS

All in package `org.example.stepdefs`. Dependency injection via PicoContainer — `SharedContext` is injected into each step class.

### SharedContext.java
- PicoContainer-managed context shared across all step classes in a scenario
- Instantiates `WebDriverManager.initBrowser()` in constructor; navigates to base URL
- Exposes: `page`, `loginPage`, `productsPage`, `productDetailsPage`, `cartPage`, `checkoutPage`, `navigationComponent`
- `tearDown()` — closes browser

### Hooks.java
- `@Before` — calls `ScreenshotUtil.initScenarioFolder()`
- `@AfterStep` — captures step screenshot, attaches to Extent report and Cucumber JSON
- `@After` — captures failure screenshot if scenario failed, attaches to both reports, calls `ScreenshotUtil.cleanupScenario()` and `ctx.tearDown()`

### LoginSteps.java
Login, locked user, invalid login, empty credentials scenarios

### ProductSteps.java
Product browsing, add to cart, cart badge count scenarios

### CartSteps.java
Cart view, remove items, continue shopping, multi-item cart scenarios

### CheckoutSteps.java
Checkout flow (step 1, step 2, complete), order confirmation scenarios

### NavigationSteps.java
Menu open/close, logout, reset app state, back-navigation scenarios

### VisualSteps.java
Visual comparison steps using `VisualCompareUtil` — used only with `@visual` tag

---

## 12. TEST RUNNERS

### CucumberRunner.java
```java
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "org.example.stepdefs",
    plugin = {
        "pretty",
        "json:build/reports/cucumber/cucumber-report.json"
    },
    monochrome = true
)
public class CucumberRunner extends AbstractTestNGCucumberTests {
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() { return super.scenarios(); }
}
```
- Outputs JSON to `build/reports/cucumber/cucumber-report.json`
- Masterthought generates HTML from that JSON via Gradle task `generateCucumberReport`
- No built-in `html:` plugin (causes XSS errors and inflated file size)

---

## 13. TESTNG CONFIGURATION

Five XML files in `src/test/resources/testng/`:

### testng.xml (Default — all Cucumber scenarios, parallel)
```xml
<suite name="SDD Automation Suite" parallel="none" data-provider-thread-count="4">
  <listeners>
    <listener class-name="org.example.listeners.ExtentTestListener"/>
  </listeners>
  <test name="Cucumber BDD Suite">
    <classes>
      <class name="org.example.runners.CucumberRunner"/>
    </classes>
  </test>
</suite>
```

### testng-cucumber.xml
Identical to testng.xml — explicit alias for Cucumber-only runs.

### testng-smoke.xml
Runs `CucumberRunner` with `-Dcucumber.filter.tags="@smoke"` applied at command line.
(Also contains a legacy class list referencing `org.example.tests.*` — those classes no longer exist.)

### testng-cross-browser.xml
Runs `CucumberRunner` with `data-provider-thread-count="4"` for Firefox/WebKit runs.
Browser set via `-Dbrowser.type=firefox|webkit` system property.
Note: Exclude `@visual` tag for cross-browser runs — baselines are Chromium-only.

### testng-visual.xml
Runs visual regression only — references `org.example.tests.VisualRegressionTest` (legacy, not used). Use `CucumberRunner` with `-Dcucumber.filter.tags="@visual"` instead.

---

## 14. EXECUTION MODELS

### Run all scenarios (default)
```bash
./gradlew clean test
```
Uses `testng.xml` with `data-provider-thread-count="4"`. Expected: ~3–4 minutes.

### Smoke scenarios only
```bash
./gradlew clean test -Dcucumber.filter.tags="@smoke"
```
Expected: ~45–60 seconds.

### Specific browser
```bash
./gradlew clean test -Dbrowser.type=firefox
./gradlew clean test -Dbrowser.type=webkit
```

### CI / headless
```bash
ENV=ci ./gradlew clean test
```
Activates `config-ci.properties` (headless=true, timeout=15000).

### Visual regression only (local)
```bash
./gradlew clean test -Dcucumber.filter.tags="@visual"
```

### Exclude visual from CI
```bash
./gradlew clean test -Dcucumber.filter.tags="not @visual"
```

---

## 15. REPORTING

### ExtentReports (primary HTML dashboard)
- Output: `build/reports/extent/ExtentReport.html`
- Generated by `ExtentTestListener` (custom `ITestListener`) — no adapter library
- Screenshots attached as relative paths from the report's directory — resolves to `../screenshots/<run>/<scenario>/step_NN.png`
- Step screenshots attached via `@AfterStep`; failure screenshot attached via `@After`

### Cucumber Report (Masterthought)
- JSON source: `build/reports/cucumber/cucumber-report.json`
- HTML output: `build/reports/cucumber/html/cucumber-html-reports/overview-features.html`
- Generated by Gradle task `generateCucumberReport` (runs automatically after `test` via `finalizedBy`)
- Masterthought saves screenshot embeddings as external files under `embeddings/` — keeps HTML files small
- Masterthought is in `buildscript { classpath }` only, not `testImplementation`

---

## 16. CI/CD INTEGRATION

### GitHub Actions
Workflow: `.github/workflows/ci.yml`
- Runs on push/PR to main
- Sets `ENV=ci` to activate CI config
- Excludes `@visual` scenarios (`-Dcucumber.filter.tags="not @visual"`)
- Uploads `build/reports/` as artifact

### Run command for CI
```bash
ENV=ci ./gradlew clean test -Dcucumber.filter.tags="not @visual"
```

### Install Playwright browsers (required first run in CI)
```bash
./gradlew installPlaywright
```

---

## 17. QUALITY STANDARDS

- Flaky test rate: target <2%
- No `Thread.sleep()` anywhere — use WaitUtil or Playwright's built-in waits
- No hardcoded credentials or URLs — all via ConfigReader
- Screenshots on every step (Extent) and on failure (Cucumber)
- Visual regression threshold: 3% pixel difference

---

## 18. FRAMEWORK STRATEGY

This is a **Cucumber BDD-only** framework. All 69 test scenarios are expressed as Gherkin feature files and executed through `CucumberRunner`. The BDD approach makes tests readable by non-technical stakeholders while Page Objects keep locators maintainable.

**Component responsibilities:**
- **Feature files** — define behaviour in plain English
- **Step definitions** — bridge Gherkin to Page Object calls
- **SharedContext** — PicoContainer DI container; one browser per scenario
- **Page Objects** — encapsulate all locators and interactions
- **Hooks** — cross-cutting concerns (screenshots, browser lifecycle)
- **Listeners** — Extent report population

---

## 19. QUICK START COMMANDS

```bash
# Install Playwright browsers (once)
./gradlew installPlaywright

# Run all tests
./gradlew clean test

# Run smoke tests only
./gradlew clean test -Dcucumber.filter.tags="@smoke"

# Run headless (CI mode)
ENV=ci ./gradlew clean test

# Run on Firefox
./gradlew clean test -Dbrowser.type=firefox

# Run visual regression (local only)
./gradlew clean test -Dcucumber.filter.tags="@visual"

# Open Extent report
open build/reports/extent/ExtentReport.html

# Open Cucumber Masterthought report
open build/reports/cucumber/html/cucumber-html-reports/overview-features.html
```

---

## 20. SUCCESS METRICS

Current state of the framework:
- **27 Java files** across main and test source trees
- **11 feature files**, **69 Cucumber scenarios**
- **5 CSV test data files**
- **3 config property files** (config.properties + 2 environment overrides)
- **5 TestNG XML files** for different execution modes
- **2 HTML reports** per run: Extent (step-by-step with screenshots) + Masterthought Cucumber
- All scenarios executable locally and in GitHub Actions CI

---

**Status**: ✅ Framework Architecture Specification - ACCURATE as of 2026-04-24
