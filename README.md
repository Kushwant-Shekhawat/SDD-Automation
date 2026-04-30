# SDD-Automation

[![CI](https://github.com/Kushwant-Shekhawat/SDD-Automation/actions/workflows/ci.yml/badge.svg)](https://github.com/Kushwant-Shekhawat/SDD-Automation/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-11-blue)
![Playwright](https://img.shields.io/badge/Playwright-1.51.0-green)
![Cucumber](https://img.shields.io/badge/Cucumber-7.14.0-brightgreen)
![TestNG](https://img.shields.io/badge/TestNG-7.7.0-orange)

End-to-end UI test automation framework for [SauceDemo](https://www.saucedemo.com), built using **Spec-Driven Development (SDD)** — every implementation decision is traceable to a written specification that existed before the first line of code.

**67 scenarios · 11 feature files · 3 browsers · 4 parallel threads · dual HTML reports**

---

## What makes this different

Most automation projects start with code. This one started with specs.

Before any Java was written, 11 specification files were produced covering framework architecture, page object contracts, test scenarios, data management, configuration, reporting, BDD conventions, cross-browser strategy, visual regression, negative flows, and the Playwright actions layer. The specs were cross-referenced against each other and against the live SauceDemo DOM — conflicts were resolved, gaps were filled, and technical risks were mitigated — all before implementation began.

The result is a framework where every design decision has a documented reason: why `[data-test]` selectors over CSS classes, why `LocatorStore` over inline strings, why `ThreadLocal` over a shared browser instance, why a custom `ITestListener` over the ExtentReports adapter.

---

## Stack

| Layer | Technology |
|-------|-----------|
| Browser automation | Playwright for Java 1.51.0 |
| Test runner | TestNG 7.7.0 |
| BDD layer | Cucumber 7.14.0 |
| DI | PicoContainer (step def injection) |
| Assertions | AssertJ 3.24.1 |
| Reporting | ExtentReports 5 + Masterthought Cucumber |
| Build | Gradle 8 |
| CI | GitHub Actions (3-browser matrix) |
| Language | Java 11 |

---

## Quick start

```bash
# 1. Clone
git clone https://github.com/Kushwant-Shekhawat/SDD-Automation.git
cd SDD-Automation

# 2. Install Playwright browsers (once per machine)
./gradlew installPlaywright

# 3. Run all tests
./gradlew test

# 4. Open reports
open build/reports/extent/ExtentReport.html
open build/reports/cucumber/html/cucumber-html-reports/overview-features.html
```

**Common overrides**

```bash
# Headless mode
./gradlew test -Dbrowser.headless=true

# Single browser
./gradlew test -Dbrowser=firefox

# Filter by tag
./gradlew test -Dcucumber.filter.tags="@login"

# Install one browser only
./gradlew installPlaywright -Dbrowser=chromium
```

---

## Project structure

```
src/main/java/org/example/
  driver/     WebDriverManager.java          — ThreadLocal Playwright lifecycle
  pages/      PlaywrightActions.java         — fluent action base class
              LoginPage, ProductsPage, ProductDetailsPage,
              CartPage, CheckoutPage, NavigationComponent
  utils/      ConfigReader, LocatorStore, LoggerUtil,
              ScreenshotUtil, WaitUtil, VisualCompareUtil

src/test/java/org/example/
  runners/    CucumberRunner.java            — @DataProvider(parallel=true), 4 threads
  stepdefs/   Hooks.java + 6 step def files + SharedContext.java
  listeners/  ExtentTestListener.java         — ITestListener wiring Extent reports
  base/       BaseTest.java

src/test/resources/
  features/   11 .feature files (69 scenarios)
  locators/   6 JSON files — one per page, loaded by LocatorStore
  config/     config.properties (committed defaults)
              config-local.properties (gitignored, per-developer overrides)
  testng/     testng.xml, testng-cross-browser.xml
```

---

## Test coverage

| Feature area | Scenarios | Tags |
|---|---|---|
| Login (valid + invalid users) | 7 | `@login` |
| Logout + menu navigation | 4 | `@logout` |
| Product listing | 6 | `@products` |
| Product sorting | 6 | `@products` |
| Product details | 5 | `@product_details` |
| Cart — single item | 4 | `@cart` |
| Cart — multi-item | 6 | `@cart` |
| Checkout happy path | 6 | `@checkout` |
| Checkout field validation | 9 | `@checkout` |
| Negative flows + security | 8 | — |
| Visual regression (Chromium only) | 6 | `@visual` |

---

## Architecture decisions

**`PlaywrightActions` base class** — Page objects extend `PlaywrightActions` rather than delegating to a utility singleton. Every action (click, fill, getText, waitForVisible) is a method on the base class, keeping page objects readable and keeping Playwright-specific APIs behind a single seam.

**`LocatorStore`** — Locators live in `src/test/resources/locators/<page>.json`, not scattered through Java source. `LocatorStore.get(page, key)` loads and caches each file on first access. Template substitution (`{paramName}`) handles dynamic selectors without regex gymnastics. Adding or changing a selector is a JSON edit, not a recompile.

**ThreadLocal throughout** — `WebDriverManager`, `ScreenshotUtil`, and `SharedContext` all use `ThreadLocal`. Four parallel Cucumber scenarios get four isolated browser instances with no shared mutable state between threads.

**No static test data** — Test data comes from CSVs read by `TestDataProvider`. Credentials, product names, and checkout details are not hardcoded in feature files or step definitions.

**Dual reports** — Extent HTML attaches per-step screenshots inline. Masterthought Cucumber HTML gives feature/scenario pass-rate breakdowns. Both are generated automatically after `./gradlew test`.

---

## Reports

| Report | Path after `./gradlew test` |
|--------|-----------------------------|
| Extent (step screenshots) | `build/reports/extent/ExtentReport.html` |
| Cucumber HTML (Masterthought) | `build/reports/cucumber/html/cucumber-html-reports/overview-features.html` |
| Cucumber JSON | `build/reports/cucumber/cucumber-report.json` |
| Step screenshots | `build/reports/screenshots/<timestamp>/<scenario>/step_NN.png` |

Open with `open <path>` — avoid IntelliJ's built-in browser (localhost:63342 triggers XSS warnings on the Extent report).

---

## CI pipeline

Two jobs run on every push to `main`:

**Job 1 — Chromium (all non-visual scenarios)**
Ubuntu · headless · 4 parallel threads · `not @visual` filter

**Job 2 — Cross-browser matrix (Firefox + WebKit)**
Runs only after Job 1 passes · same scenario set · browser installed per-job

Artifacts uploaded on every run: Cucumber report, Extent report, TestNG XML. Failure screenshots uploaded on failure only (7-day retention).

---

## Spec library

All 11 specification files live in [`specs/`](specs/). The pre-implementation analysis — cross-spec conflict detection, locator verification against the live DOM, dependency graph, risk register — is in [`analysis.md`](analysis.md).

| Spec | Topic |
|------|-------|
| [01](specs/01-framework-architecture.md) | Framework architecture |
| [02](specs/02-page-object-model.md) | Page Object Model contracts |
| [03](specs/03-test-scenarios.md) | Test scenario inventory |
| [04](specs/04-test-data-management.md) | CSV test data strategy |
| [05](specs/05-configuration-management.md) | Configuration management |
| [06](specs/06-reporting.md) | Reporting pipeline |
| [07](specs/07-cucumber-bdd.md) | Cucumber / BDD conventions |
| [08](specs/08-cross-browser.md) | Cross-browser strategy |
| [09](specs/09-visual-regression.md) | Visual regression approach |
| [10](specs/10-negative-flows.md) | Negative flow coverage |
| [11](specs/11-generic-playwright-actions.md) | PlaywrightActions layer |
