# Case Study: Building a Production-Grade Test Automation Framework with Spec-Driven Development

## The problem

Most automation projects fail the same way: a developer writes a test suite directly against an application, and six months later the suite is brittle, slow to maintain, and trusted by nobody. The tests pass locally and fail in CI. Locators are scattered across dozens of files. Nobody knows why a particular assertion uses `contains()` in one place and `equals()` in another. Adding a new test requires reading three existing ones to understand the pattern.

The root cause is almost always the same: the framework was designed implicitly, as a side effect of writing tests, rather than explicitly, before a single test was written.

This project was an experiment in doing the opposite.

---

## The approach: Spec-Driven Development

Before any Java was written, 11 specification files were produced — one per concern:

- Framework architecture (package layout, class responsibilities, dependency graph)
- Page Object Model (method signatures, locator strategy, what each page object owns)
- Test scenarios (what behaviours to cover, in Gherkin)
- Test data management (CSV structure, how data reaches step definitions)
- Configuration management (property files, runtime overrides, environment layering)
- Reporting (Extent + Cucumber pipelines, screenshot strategy, report paths)
- BDD conventions (Cucumber options, step text ownership, PicoContainer DI)
- Cross-browser strategy (browser matrix, CI job structure, per-browser install)
- Visual regression (baseline/diff approach, when to run, CI exclusion)
- Negative flows (locked user, invalid credentials, missing form fields)
- Generic Playwright actions layer (the `PlaywrightActions` base class contract)

The specs were written to a [constitution](specs/constitution.md) — a set of rules every spec must follow: declare its own scope, list its dependencies, define its public interface, and flag anything it deliberately defers.

---

## Pre-implementation analysis

Once all 11 specs existed, they were cross-referenced against each other and against the live SauceDemo DOM before a single class was created. The [full analysis](analysis.md) produced:

**4 cross-spec conflicts**, resolved before implementation:
- Gherkin step text defined differently in Spec 03 vs Spec 07 (would have caused `UndefinedStepException` at runtime)
- `CartPage.getCartTotal()` referenced a DOM element that doesn't exist on `cart.html` — the total only appears on the checkout confirmation page
- `ConfigReader` method names inconsistent across two specs
- `CustomExceptions.java` placed in two different packages in one spec

**6 gaps** filled before implementation:
- No spec defined `logback.xml` content — added with console appender and INFO root level
- `gradle.properties` appeared in folder structure but was never specified — JVM args defined
- Playwright browser installation step missing from all specs — `installPlaywright` Gradle task added
- Empty cart state not covered by any scenario — guard assertion added
- `performance_glitchy_user` inclusion in data CSV not safe for regression — excluded

**7 technical risks** documented with mitigations:
- `cucumber-picocontainer:7.14.0` artifact ID needed verification on Maven Central
- `strict = true` deprecated in Cucumber 7 (default in that version — remove the flag)
- ExtentReports 5 adapter incompatibility — replaced with a custom `ITestListener`
- `performance_glitchy_user` flakiness — excluded from smoke and regression data
- `CartPage.getCartTotal()` would throw `ElementNotFoundException` — removed
- `problem_user` has broken images and sort — tagged `@edge`, excluded from smoke
- Checkout error message text — assertions changed from `assertEquals` to `contains()`

**18 locators** verified against the live DOM. One removed (`div.cart_item` total on cart page — doesn't exist).

This analysis took roughly the same time as debugging would have taken after finding these problems in a running test suite — except the problems were caught at zero cost, before any code existed to touch.

---

## Key design decisions

### `PlaywrightActions` base class

Page objects extend `PlaywrightActions` rather than delegating to a utility singleton or calling Playwright APIs directly. Every meaningful browser interaction — click, fill, getText, waitForVisible, screenshot — is a method on the base class. Adding a new page object means extending one class and writing methods that read like plain English.

The alternative (calling `page.locator(...).click()` inline throughout page objects) works until you need to add logging, retry logic, or screenshot capture to every action. At that point you're doing a grep-and-replace across dozens of files. With `PlaywrightActions`, you change one method.

### `LocatorStore` — locators as data, not code

Locators live in `src/test/resources/locators/<page>.json`, not embedded in Java source. `LocatorStore.get(page, key)` loads and caches each file on first access. Template substitution (`{paramName}`) handles dynamic selectors — for example, the add-to-cart button selector is parameterised by product name token, avoiding a separate method per product.

The practical benefit: changing a locator is a JSON edit. It doesn't require recompilation, it doesn't require touching a Java file, and a pull request that only touches a JSON file is immediately obvious in review.

### ThreadLocal throughout

`WebDriverManager`, `ScreenshotUtil`, and `SharedContext` all use `ThreadLocal`. The test suite runs 4 Cucumber scenarios in parallel via TestNG's `@DataProvider`. Each thread gets its own browser instance, its own screenshot counter, and its own shared state container. There is no locking, no synchronisation, and no way for one thread's browser to interfere with another's.

The alternative — a shared static browser instance with synchronised access — would serialize parallel execution and introduce subtle ordering bugs. ThreadLocal was specified before implementation precisely to avoid that class of failure.

### Configuration layering

`config.properties` is committed with safe defaults (`browser.headless=false` for local development). `config-local.properties` is gitignored and overrides committed defaults per developer. Any value can be overridden at runtime via `-D` flags. CI passes `-DENV=ci` and `-Dbrowser.headless=true` without touching any committed file.

The result: no committed file ever needs to change to run in a different environment, and a developer's local overrides never accidentally appear in a commit.

---

## What the CI pipeline does

Two jobs run on every push to `main`:

**Job 1** runs all 63 non-visual scenarios on Chromium, headless, 4 parallel threads. It uploads the Cucumber Masterthought report, the Extent report, and TestNG XML as artifacts. Failure screenshots are uploaded only on failure.

**Job 2** runs a cross-browser matrix — Firefox and WebKit — but only after Job 1 passes. Each browser installs only its own binaries. The same scenario set runs on each.

The pipeline was specified in Spec 08 before it was written. The CI YAML is an implementation of that spec, not a discovery process.

---

## Results

- 69 scenarios across 11 feature files, all green in CI
- 3 browsers covered (Chromium, Firefox, WebKit)
- 4 parallel threads with zero thread-safety incidents
- Zero hardcoded locators in Java source
- Zero hardcoded credentials or test data in feature files
- Dual HTML reports generated automatically on every run
- Per-step screenshots attached to both reports
- Every design decision traceable to a written spec

---

## What I would do differently

**Spec granularity on the actions layer.** Spec 11 (PlaywrightActions) was written later than the others and shows it — the method signatures are less precisely defined than in earlier specs. A second pass on that spec before implementation would have saved one round of refactoring.

**Visual regression baseline management.** Spec 09 covers the comparison approach but does not define where baselines are stored or how they are updated in CI. This was deferred and handled ad hoc. A proper baseline commit strategy (baselines committed to the repo, updated via a specific Gradle task) would make visual regression more maintainable.

**Test data for negative flows.** Negative flow test data is currently hardcoded in feature files (locked user credentials, empty field submissions). These should live in the CSV alongside the happy-path data, with a column indicating the expected error. That change is straightforward but was out of scope for this iteration.

---

## Takeaway

Spec-Driven Development is not a heavyweight process. The 11 specs and analysis document took less time than a typical debugging session on a brittle test suite. What they produced was a framework where every decision has a documented reason, every conflict was caught before it became a runtime error, and every new contributor has a written contract to implement against rather than existing code to reverse-engineer.

The framework is the documentation. The documentation is the framework.
