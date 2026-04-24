# SDD-Automation — Claude Code Instructions

## Maintenance Rule
Whenever a change is made to this project — new page object, new feature file, new utility,
report path change, config change, convention change, new Gradle task, etc. — update this
CLAUDE.md file in the same commit to keep it accurate.

## Project Overview
UI test automation framework for [SauceDemo](https://www.saucedemo.com) built using
Spec-Driven Development (SDD). Stack: Java 11 + Playwright 1.51.0 + TestNG 7.7.0 + Cucumber 7.14.0.

## Key Commands

```bash
# Run all tests (headless, 4 parallel threads)
./gradlew test -Dbrowser.headless=true

# Run specific tag
./gradlew test -Dcucumber.filter.tags="@login"

# Run single browser
./gradlew test -Dbrowser=firefox

# Install Playwright browsers (required once after fresh clone)
./gradlew installPlaywright

# Install single browser
./gradlew installPlaywright -Dbrowser=chromium
```

## Project Structure

```
src/main/java/org/example/
  driver/       WebDriverManager.java        — ThreadLocal Playwright browser/page lifecycle
  pages/        BasePage.java + 6 page objects — Page Object Model
  data/         TestDataProvider.java         — CSV-based test data
  utils/        ConfigReader, LoggerUtil, ScreenshotUtil, WaitUtil, VisualCompareUtil

src/test/java/org/example/
  runners/      CucumberRunner.java           — @DataProvider(parallel=true), 4 threads
  stepdefs/     Hooks.java + 6 step def classes + SharedContext.java
  listeners/    ExtentTestListener.java        — ITestListener wiring Extent reports
  base/         BaseTest.java

src/test/resources/
  features/     11 .feature files (69 scenarios total, @visual tag excludes CI visual tests)
  config/       config.properties + config-local.properties (gitignored, local overrides)
  testng/       testng.xml
```

## Reports (generated after every test run)

| Report | Path |
|--------|------|
| Extent (step screenshots) | `build/reports/extent/ExtentReport.html` |
| Cucumber HTML (Masterthought) | `build/reports/cucumber/html/cucumber-html-reports/overview-features.html` |
| Cucumber JSON | `build/reports/cucumber/cucumber-report.json` |
| Screenshots | `build/reports/screenshots/<timestamp>/<scenario>/step_NN.png` |

Open reports directly from Finder/terminal (`open <path>`), not via IntelliJ's built-in browser
— IntelliJ serves files through localhost:63342 which triggers XSS warnings.

## Screenshot Strategy
- Every step → PNG saved to `build/reports/screenshots/<run-timestamp>/<scenario>/step_NN.png`
- Step screenshots attached to Extent report (relative paths) and Cucumber report (external embeddings)
- Failure screenshot saved as `Failed_Step_<scenarioname>.png` and attached to both reports
- `ScreenshotUtil` uses ThreadLocal so parallel execution never crosses wires

## Parallel Execution
- 4 threads via `data-provider-thread-count="4"` in `testng.xml`
- `WebDriverManager` uses ThreadLocal — each thread gets its own browser instance
- `ScreenshotUtil` and `SharedContext` are ThreadLocal-safe

## Configuration
- `config.properties` — committed defaults (`browser.headless=false` for local, overridden via `-D`)
- `config-local.properties` — gitignored, per-developer overrides (e.g. `browser.headless=true`)
- Pass any config value at runtime: `-Dbrowser.headless=true`, `-Dbrowser=firefox`, etc.

## Page Objects
All page objects extend `BasePage`. Locators use `data-test` attributes wherever possible.
- `LoginPage`, `ProductsPage`, `ProductDetailsPage`, `CartPage`, `CheckoutPage`, `NavigationComponent`

## Cucumber Tags
| Tag | Meaning |
|-----|---------|
| `@login` | Login scenarios |
| `@cart` | Cart scenarios |
| `@checkout` | Checkout scenarios |
| `@visual` | Visual regression — excluded from CI (`not @visual`) |

## CI (GitHub Actions)
- Main job: Chromium, headless, `not @visual`, 4 threads
- Cross-browser job: Firefox + WebKit (runs after main passes)
- Workflow file: `.github/workflows/ci.yml`

## Conventions
- Never modify `config-local.properties` in commits
- Never commit `.claude/` directory (gitignored)
- Page locators: prefer `[data-test='x']` over CSS class selectors
- Step def classes use PicoContainer DI via `SharedContext` — no static state
- New page objects go in `src/main/java/org/example/pages/`
- New step defs go in `src/test/java/org/example/stepdefs/`
- New features go in `src/test/resources/features/`
