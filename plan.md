# SDD-Automation Implementation Plan

## Project Goal
Build a complete UI test automation framework for https://www.saucedemo.com using:
**Java 11 + Gradle + Playwright + TestNG + Cucumber 7 + ExtentReports 5**

## Architecture Decision: src/main vs src/test

| Location | Classes |
|----------|---------|
| `src/main/java/org/example/` | BasePage, WebDriverManager, all pages, all utils |
| `src/test/java/org/example/` | BaseTest, test classes, step definitions, runner, listener |

---

## Step 1 — Project Foundation
**Files**: 3
**Goal**: Valid Gradle project that compiles with all dependencies

| # | File | Location |
|---|------|----------|
| 1 | `build.gradle` | root |
| 2 | `settings.gradle` | root |
| 3 | `gradle.properties` | root |

**Dependencies to declare**:
- `com.microsoft.playwright:playwright:1.40.0`
- `org.testng:testng:7.7.0`
- `com.aventstack:extentreports:5.0.9`
- `com.aventstack:extentreports-testng-adapter:1.10.10`
- `io.cucumber:cucumber-java:7.14.0`
- `io.cucumber:cucumber-testng:7.14.0`
- `io.cucumber:cucumber-picocontainer:7.14.0`
- `org.slf4j:slf4j-api:1.7.36`
- `ch.qos.logback:logback-classic:1.2.11`
- `org.assertj:assertj-core:3.24.1`
- `commons-csv:commons-csv:1.10.0`
- `com.fasterxml.jackson.core:jackson-databind:2.15.2`
- `com.github.javafaker:javafaker:1.0.2`
- `org.awaitility:awaitility:4.1.1`

**Done when**: `./gradlew dependencies` runs without error

---

## Step 2 — Core Infrastructure
**Files**: 3
**Goal**: Browser launches, page is accessible, test lifecycle works

| # | File | Location |
|---|------|----------|
| 4 | `WebDriverManager.java` | `src/main/java/org/example/base/` |
| 5 | `BasePage.java` | `src/main/java/org/example/base/` |
| 6 | `BaseTest.java` | `src/test/java/org/example/base/` |

**Key responsibilities**:
- `WebDriverManager`: ThreadLocal `Page`, `launchBrowser()`, `getPage()`, `closeBrowser()`, headless/headed toggle via config
- `BasePage`: `click()`, `fill()`, `getText()`, `isVisible()`, `waitForElementVisible()`, `navigate()`, `selectDropdown()`
- `BaseTest`: `@BeforeMethod` → launchBrowser, `@AfterMethod` → screenshot on failure + closeBrowser

**Done when**: `./gradlew compileJava compileTestJava` passes

---

## Step 3 — Utilities
**Files**: 5
**Goal**: All helper classes available before page objects or tests are written

| # | File | Location |
|---|------|----------|
| 7  | `ConfigReader.java`      | `src/main/java/org/example/utils/` |
| 8  | `LoggerUtil.java`        | `src/main/java/org/example/utils/` |
| 9  | `WaitUtil.java`          | `src/main/java/org/example/utils/` |
| 10 | `ScreenshotUtil.java`    | `src/main/java/org/example/utils/` |
| 11 | `CustomExceptions.java`  | `src/main/java/org/example/utils/` |

**Key responsibilities**:
- `ConfigReader`: reads `config.properties` → overridden by `config-local.properties` or `config-ci.properties`; methods: `getBaseUrl()`, `getBrowserType()`, `isHeadless()`, `getTimeout()`, `getExtentReportPath()`, `getScreenshotPath()`
- `LoggerUtil`: static SLF4J wrapper — `info()`, `warn()`, `error()`, `debug()`
- `WaitUtil`: Playwright explicit waits — `waitForVisible()`, `waitForClickable()`, `waitForUrl()`
- `ScreenshotUtil`: `captureScreenshot(Page, testName)` → saves to `build/reports/screenshots/`, returns path
- `CustomExceptions`: `FrameworkException`, `ElementNotFoundException`, `ConfigurationException`

**Done when**: `./gradlew compileJava` passes

---

## Step 4 — Configuration & Resources
**Files**: 3
**Goal**: Framework reads correct values per environment

| # | File | Location |
|---|------|----------|
| 12 | `config.properties`       | `src/test/resources/config/` |
| 13 | `config-ci.properties`    | `src/test/resources/config/` |
| 14 | `config-local.properties` | `src/test/resources/config/` ← gitignored |

**Key properties**:
```
base.url=https://www.saucedemo.com
browser.type=chromium
browser.timeout=10000
browser.headless=false
screenshot.on.failure=true
extent.report.path=build/reports/extentreports/index.html
screenshot.path=build/reports/screenshots
parallel.thread.count=4
```

**Done when**: `ConfigReader.getBaseUrl()` returns correct value in a simple test

---

## Step 5 — Page Objects
**Files**: 5
**Goal**: All SauceDemo pages wrapped with stable locators and clean methods

| # | File | Location |
|---|------|----------|
| 15 | `LoginPage.java`          | `src/main/java/org/example/pages/` |
| 16 | `ProductsPage.java`       | `src/main/java/org/example/pages/` |
| 17 | `ProductDetailsPage.java` | `src/main/java/org/example/pages/` |
| 18 | `CartPage.java`           | `src/main/java/org/example/pages/` |
| 19 | `CheckoutPage.java`       | `src/main/java/org/example/pages/` |

**Locator strategy**: `data-test` attributes first, then `id`, then stable CSS class
**No assertions in page objects** — only in test classes and step definitions

**Done when**: `./gradlew compileJava` passes

---

## Step 6 — Test Data
**Files**: 6
**Goal**: Data-driven tests have structured input available

| # | File | Location |
|---|------|----------|
| 20 | `TestDataProvider.java`       | `src/main/java/org/example/utils/` |
| 21 | `login-data.csv`              | `src/test/resources/testdata/` |
| 22 | `product-data.csv`            | `src/test/resources/testdata/` |
| 23 | `cart-data.csv`               | `src/test/resources/testdata/` |
| 24 | `checkout-data.csv`           | `src/test/resources/testdata/` |
| 25 | `invalid-checkout-data.csv`   | `src/test/resources/testdata/` |

**CSV schemas**:
- `login-data.csv`: `username, password, expectedResult, description`
- `product-data.csv`: `productName, price, category`
- `cart-data.csv`: `testCase, productNames, expectedCount`
- `checkout-data.csv`: `firstName, lastName, postalCode, expectedResult`
- `invalid-checkout-data.csv`: `firstName, lastName, postalCode, expectedError`

**Done when**: `TestDataProvider.getLoginData()` returns correct `Object[][]`

---

## Step 7 — TestNG Test Classes
**Files**: 4
**Goal**: 25 TestNG tests cover all SauceDemo flows with data-driven coverage

| # | File | Location | Tests |
|---|------|----------|-------|
| 26 | `LoginTests.java`         | `src/test/java/org/example/tests/` | 7-8 |
| 27 | `ProductsTests.java`      | `src/test/java/org/example/tests/` | 7 |
| 28 | `ShoppingCartTests.java`  | `src/test/java/org/example/tests/` | 6 |
| 29 | `CheckoutTests.java`      | `src/test/java/org/example/tests/` | 5 |

**Groups used**: `smoke`, `regression`, `positive`, `negative`, `data-driven`
**All tests extend** `BaseTest`
**Assertions via** AssertJ `assertThat()`

**Done when**: `./gradlew compileTestJava` passes

---

## Step 8 — Reporting & TestNG XML
**Files**: 4
**Goal**: HTML report generated after every run, screenshots on failure

| # | File | Location |
|---|------|----------|
| 30 | `ExtentTestListener.java` | `src/test/java/org/example/listeners/` |
| 31 | `testng.xml`              | `src/test/resources/testng/` |
| 32 | `testng-parallel.xml`     | `src/test/resources/testng/` |
| 33 | `testng-smoke.xml`        | `src/test/resources/testng/` |

**Listener hooks**: `onTestStart` → create node, `onTestFailure` → attach screenshot, `onFinish` → flush report
**testng.xml**: sequential, thread-count=1, all test classes + CucumberRunner
**testng-parallel.xml**: thread-count=4, parallel=methods
**testng-smoke.xml**: groups="smoke" only

**Done when**: running any suite produces `build/reports/extentreports/index.html`

---

## Step 9 — Cucumber BDD
**Files**: 10
**Goal**: 20+ BDD scenarios run end-to-end and produce Cucumber HTML report

| # | File | Location |
|---|------|----------|
| 34 | `login.feature`           | `src/test/resources/features/` |
| 35 | `products.feature`        | `src/test/resources/features/` |
| 36 | `shopping_cart.feature`   | `src/test/resources/features/` |
| 37 | `checkout.feature`        | `src/test/resources/features/` |
| 38 | `Hooks.java`              | `src/test/java/org/example/stepdefinitions/` |
| 39 | `LoginSteps.java`         | `src/test/java/org/example/stepdefinitions/` |
| 40 | `ProductsSteps.java`      | `src/test/java/org/example/stepdefinitions/` |
| 41 | `ShoppingCartSteps.java`  | `src/test/java/org/example/stepdefinitions/` |
| 42 | `CheckoutSteps.java`      | `src/test/java/org/example/stepdefinitions/` |
| 43 | `CucumberRunner.java`     | `src/test/java/org/example/runners/` |

**Done when**: `./gradlew test -Dtest=CucumberRunner` passes all scenarios

---

## Step 10 — Build, Run & Verify
**Goal**: All 45+ test cases pass, both reports generated

```bash
# Full build
./gradlew clean build

# Run all tests (TestNG + Cucumber)
./gradlew test

# Run smoke only
./gradlew test -DsuiteFile=src/test/resources/testng/testng-smoke.xml

# Run parallel
./gradlew test -DsuiteFile=src/test/resources/testng/testng-parallel.xml

# Open reports
open build/reports/extentreports/index.html
open build/reports/cucumber/cucumber-report.html
```

**Success criteria**:
- [ ] `./gradlew build` exits 0
- [ ] All 25 TestNG tests pass
- [ ] All 20+ Cucumber scenarios pass
- [ ] `build/reports/extentreports/index.html` generated
- [ ] `build/reports/cucumber/cucumber-report.html` generated
- [ ] Screenshots captured for any failures

---

## File Count Summary

| Step | Files | Cumulative |
|------|-------|-----------|
| 1 — Project Foundation | 3 | 3 |
| 2 — Core Infrastructure | 3 | 6 |
| 3 — Utilities | 5 | 11 |
| 4 — Configuration | 3 | 14 |
| 5 — Page Objects | 5 | 19 |
| 6 — Test Data | 6 | 25 |
| 7 — TestNG Tests | 4 | 29 |
| 8 — Reporting & XML | 4 | 33 |
| 9 — Cucumber BDD | 10 | 43 |
| 10 — Build & Verify | 0 | **43** |

---

## Status

- [x] Constitution
- [x] Specifications (7 files)
- [x] Clarify (package=org.example, BaseTest in test, gitignore updated)
- [x] Plan (this file)
- [ ] Implement Step 1 — Project Foundation
- [ ] Implement Step 2 — Core Infrastructure
- [ ] Implement Step 3 — Utilities
- [ ] Implement Step 4 — Configuration
- [ ] Implement Step 5 — Page Objects
- [ ] Implement Step 6 — Test Data
- [ ] Implement Step 7 — TestNG Tests
- [ ] Implement Step 8 — Reporting & XML
- [ ] Implement Step 9 — Cucumber BDD
- [ ] Implement Step 10 — Build & Verify
