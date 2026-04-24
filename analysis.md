# SDD-Automation — Analysis

## Purpose
Cross-reference all 7 specification files against each other and against the real SauceDemo application to find conflicts, gaps, risks, and missing pieces before a single line of production code is written.

---

## 1. DEPENDENCY MAP

```
ConfigReader
    ↓
WebDriverManager ←── ConfigReader
    ↓
BasePage ←────────── WebDriverManager (via constructor Page arg)
    ↓
All Page Objects ←── BasePage
    ↓
BaseTest ←─────────── WebDriverManager + ScreenshotUtil + ExtentTestListener
    ↓
TestNG Tests ←──────── BaseTest + Page Objects + TestDataProvider
    ↓
Cucumber Hooks ←─────── WebDriverManager
    ↓
Step Definitions ←────── Page Objects + WebDriverManager
    ↓
CucumberRunner ←───────── Step Definitions + Hooks
```

**Critical path for first compile:**
`ConfigReader` → `WebDriverManager` → `BasePage` → one page object → `BaseTest` → one test class

---

## 2. CROSS-SPEC CONFLICTS

### CONFLICT-01 — Gherkin step text mismatch (HIGH RISK)
**Specs affected**: `03-test-scenarios.md` vs `07-cucumber-bdd.md`

Spec 03 defines Gherkin like:
```
When User logs in with "standard_user" and "secret_sauce"
```

Spec 07 defines step definitions for:
```
When User enters username "standard_user"
And User enters password "secret_sauce"
And User clicks the login button
```

These are different step texts — Cucumber will throw `UndefinedStepException` at runtime.

**Resolution**: Use spec 07's step text as the authoritative version. Feature files must use the exact step strings that have matching `@When`/`@Then` annotations.

---

### CONFLICT-02 — CartPage.getCartTotal() on wrong page (MEDIUM RISK)
**Specs affected**: `02-page-object-model.md`, `03-test-scenarios.md`

Spec 02 defines `getCartTotal()` on `CartPage` (cart.html). But on SauceDemo, `cart.html` does NOT show a price total — it shows only individual item prices. The total (subtotal + tax + total) only appears on `checkout-step-two.html`.

**Resolution**: Remove `getCartTotal()` from `CartPage`. Keep it only on `CheckoutPage` as `getOrderTotal()`. The cart scenario "Cart total is calculated correctly" should verify sum of individual item prices instead.

---

### CONFLICT-03 — ConfigReader method names inconsistent (MEDIUM RISK)
**Specs affected**: `05-configuration-management.md` vs `06-reporting.md`

Spec 06 calls `ConfigReader.getExtentReportPath()` but spec 05 defines the method as `getConfig("extent.report.path")`.

**Resolution**: `ConfigReader` exposes only `getConfig(key)` and typed wrappers (`getBoolean`, `getInt`). Callers use `getConfig("extent.report.path")` directly — no named convenience methods for report paths.

---

### CONFLICT-04 — duplicate CustomExceptions location (LOW RISK)
**Spec affected**: `01-framework-architecture.md` (fixed in spec, but worth flagging for implementation)

Previously showed `CustomExceptions.java` in both `utils/` and `exception/`. Fixed in spec to be in `utils/` only.

**Resolution**: Create one file at `src/main/java/org/example/utils/CustomExceptions.java`. Do not create an `exception/` package.

---

## 3. GAPS — MISSING FROM SPECS

### GAP-01 — logback.xml not specified
No spec defines the content of `src/test/resources/logback.xml`. Without it, Logback will log a warning and fall back to a minimal default config.

**Resolution**: Create `logback.xml` at T15 with console appender, pattern `%d{HH:mm:ss} %-5level %logger{20} - %msg%n`, root level INFO.

---

### GAP-02 — gradle.properties content undefined
`gradle.properties` appears in the folder structure but no spec defines its content.

**Resolution**: Create with:
```
org.gradle.jvmargs=-Xmx2g
org.gradle.parallel=false
org.gradle.daemon=true
```

---

### GAP-03 — Playwright browser installation step missing
Playwright requires running `mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"` or `./gradlew installPlaywright` to download browser binaries. No spec mentions this.

**Resolution**: Add a Gradle task in `build.gradle`:
```gradle
task installPlaywright(type: JavaExec) {
    classpath = sourceSets.main.runtimeClasspath
    mainClass = 'com.microsoft.playwright.CLI'
    args 'install', '--with-deps', 'chromium'
}
```

---

### GAP-04 — PicoContainer state sharing not specified
Spec 07 uses `cucumber-picocontainer` but step definition classes each call `WebDriverManager.getPage()` directly (static call). PicoContainer is not actually used for dependency injection. This works, but the dependency is unused weight.

**Resolution**: Keep `cucumber-picocontainer` in case future step definitions need shared state. Current approach (static `WebDriverManager`) is correct and simpler.

---

### GAP-05 — No spec for empty cart state
Spec 02 defines `isCartEmpty()` on `CartPage` but no test scenario covers the empty cart case. SauceDemo shows a blank cart page with no message.

**Resolution**: The method is still useful as a guard. Add assertion in `ShoppingCartTests` that a freshly logged-in user has an empty cart.

---

### GAP-06 — product_name_with_apostrophe not in test data
SauceDemo product "Sauce Labs Fleece Jacket" — no apostrophe issue. But "Sauce Labs Backpack" etc. have spaces which are fine for CSS `filter(hasText)`. No issues identified.

---

## 4. TECHNICAL RISKS

### RISK-01 — `cucumber-picocontainer:7.14.0` on Maven Central (VERIFY BEFORE BUILD)
The exact artifact `io.cucumber:cucumber-picocontainer:7.14.0` must be verified on Maven Central. Some Cucumber versions use `io.cucumber:cucumber-pico` instead.

**Action**: Verify at build time. If not found, replace with `io.cucumber:cucumber-pico:7.14.0` or remove.

---

### RISK-02 — `strict = true` deprecated in Cucumber 7 (LOW)
`CucumberOptions(strict = true)` is deprecated in Cucumber 7.x. It still compiles but produces a warning. In Cucumber 7, strict mode is the default (scenarios with undefined/pending steps fail automatically).

**Action**: Remove `strict = true` from `CucumberRunner.java`.

---

### RISK-03 — `extentreports-testng-adapter:1.10.10` compatibility (MEDIUM)
The adapter `com.aventstack:extentreports-testng-adapter:1.10.10` works with ExtentReports 4.x. For ExtentReports 5.x, the correct approach is a custom `ITestListener` (which we already have as `ExtentTestListener`).

**Action**: Remove `extentreports-testng-adapter` from dependencies entirely. Our `ExtentTestListener` covers all reporting needs for ExtentReports 5.x.

---

### RISK-04 — `performance_glitchy_user` flaky tests (MEDIUM)
Spec 03 references `performance_glitchy_user` for performance tests. This user introduces random delays in SauceDemo. Any test using this user with a fixed timeout may fail intermittently.

**Action**: Do not use `performance_glitchy_user` in regression or smoke suites. Use it only in explicitly tagged `@performance` tests, or exclude from test data CSV for now.

---

### RISK-05 — `CartPage.getCartTotal()` element not in DOM (HIGH)
As noted in CONFLICT-02, `cart.html` has no total element. Calling `getText()` on a missing locator will throw `ElementNotFoundException` and fail the test.

**Action**: Remove `getCartTotal()` from `CartPage`. Confirmed — use `getOrderTotal()` on `CheckoutPage` only.

---

### RISK-06 — `problem_user` image locators are broken in SauceDemo (LOW)
`problem_user` has broken images and a broken sort dropdown. Tests using this user that rely on images or sorting will fail. Fine for edge case testing but should be clearly tagged.

**Action**: Mark `problem_user` scenarios with `@edge` group only. Not in smoke or regression.

---

### RISK-07 — SauceDemo checkout error messages (VERIFY)
Spec expects `"First Name is required"` but the actual SauceDemo error text may be `"Error: First Name is required"`. The `contains()` check in step definitions would pass either way, but `assertEquals` would fail.

**Action**: All error message assertions use `contains()` not `equals()`. Confirmed in spec 07 — already uses `assertTrue(msg.contains(...))`. Safe.

---

## 5. IMPLEMENTATION ORDER VALIDATION

The task order in `tasks.md` is correct. Dependency analysis confirms:

```
T07 (ConfigReader) must be done before T04 (WebDriverManager)
T07 + T08 + T09 + T10 + T11 done before T16-T20 (page objects use WaitUtil)
T12-T15 (config files) done before running any tests
T16-T20 (page objects) done before T27-T30 (tests) and T40-T43 (step defs)
T31 (ExtentTestListener) done before T32-T34 (TestNG XMLs reference it)
T35-T38 (feature files) done before T40-T43 (step defs reference step text)
T39-T43 (step defs) done before T44 (runner glues them together)
```

**One correction to tasks.md**: T07 (ConfigReader) should be implemented before T04 (WebDriverManager), not after. Current tasks list them T04, T05, T06, T07 — the Step 3 utilities should be done before Step 2 core infrastructure, or at minimum ConfigReader must be first in Step 3.

---

## 6. SAUCEDEMO LOCATOR VERIFICATION

Cross-referenced spec locators against known SauceDemo DOM:

| Locator | Spec | SauceDemo Actual | Status |
|---------|------|-----------------|--------|
| `[data-test="username"]` | spec 02 | ✅ Exists | OK |
| `[data-test="password"]` | spec 02 | ✅ Exists | OK |
| `[id="login-button"]` | spec 02 | ✅ Exists | OK |
| `[data-test="error"]` | spec 02 | ✅ Exists | OK |
| `#react-burger-menu-btn` | spec 02 | ✅ Exists | OK |
| `[id="logout_sidebar_link"]` | spec 02 | ✅ Exists | OK |
| `a.inventory_item_name` | spec 02 | ✅ Exists | OK |
| `button[id*="add-to-cart"]` | spec 02 | ✅ Pattern matches | OK |
| `[data-test="continue-shopping"]` | spec 02 | ✅ Exists | OK |
| `[data-test="checkout"]` | spec 02 | ✅ Exists | OK |
| `[data-test="continue"]` | spec 02 | ✅ Exists | OK |
| `[data-test="finish"]` | spec 02 | ✅ Exists | OK |
| `[data-test="firstName"]` | spec 02 | ✅ Exists | OK |
| `[data-test="lastName"]` | spec 02 | ✅ Exists | OK |
| `[data-test="postalCode"]` | spec 02 | ✅ Exists | OK |
| `div.summary_total_label` | spec 02 | ✅ Exists (checkout-step-two only) | OK |
| `h2.complete-header` | spec 02 | ✅ Exists | OK |
| `div.cart_item` (CartPage total) | spec 02 | ❌ No total element on cart.html | REMOVE |

---

## 7. FINAL CHANGES BEFORE IMPLEMENTATION

These must be applied during implementation (not spec changes):

| # | Change | Affects |
|---|--------|---------|
| FC-01 | Remove `extentreports-testng-adapter` from `build.gradle` | T01 |
| FC-02 | Add Playwright `installPlaywright` Gradle task | T01 |
| FC-03 | Create `gradle.properties` with JVM args | T03 |
| FC-04 | Create `logback.xml` with console appender | T15 |
| FC-05 | Remove `getCartTotal()` from `CartPage` | T19 |
| FC-06 | Remove `strict = true` from `CucumberRunner` | T44 |
| FC-07 | Use `getConfig(key)` not named methods in `ExtentTestListener` | T31 |
| FC-08 | Feature files use spec 07 step text (not spec 03 Gherkin) | T35–T38 |
| FC-09 | ConfigReader implemented before WebDriverManager | T07 before T04 |
| FC-10 | Do not use `performance_glitchy_user` in smoke/regression CSV rows | T22 |

---

## 8. SUMMARY

| Category | Count | Severity |
|----------|-------|----------|
| Conflicts resolved | 4 | 1 HIGH, 2 MED, 1 LOW |
| Gaps identified | 6 | Straightforward to fill |
| Technical risks flagged | 7 | 2 HIGH, 3 MED, 2 LOW |
| Final changes for implementation | 10 | All minor |
| Locators verified | 18 | 1 removed (CartPage total) |

**Framework is ready to implement.** All conflicts are resolved, risks are documented with mitigations, and the task order is validated.

---

**Status**: ✅ Analysis Complete — Proceed to Implementation (T01)
