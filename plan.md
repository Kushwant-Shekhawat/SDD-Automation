# SDD-Automation — Implementation Plan

## Current State (as of 2026-04-24)
Framework is fully built and all 69 Cucumber scenarios pass. This plan covers the
**Spec-11 refactor**: generic Playwright actions + JSON locator repository.

---

## Spec-11 Goal
Replace scattered inline selector strings in 6 page objects with JSON locator files,
and replace `BasePage` with a comprehensive `PlaywrightActions` class that wraps
every Playwright browser action.

**Two deliverables:**
1. `PlaywrightActions.java` — replaces `BasePage.java` entirely
2. `LocatorStore.java` — loads JSON per page, resolves `{param}` templates
3. Six `src/test/resources/locators/*.json` files — one per page object
4. All 6 page objects refactored: `extends PlaywrightActions`, selectors via `LocatorStore`
5. Step defs cleaned of any direct `ctx.page.locator(...)` calls that belong in a page object

---

## Approach

### Phase A — New infrastructure (no breakage)
Create `PlaywrightActions.java` and `LocatorStore.java` without touching existing files.
All existing code continues to compile and run.

### Phase B — JSON locator files
Create the six JSON files with every selector currently used by that page object.
Verify completeness against the Java source.

### Phase C — Migrate page objects one at a time
For each page object: change `extends BasePage` → `extends PlaywrightActions`,
replace every `page.locator("...")` with `page.locator(LocatorStore.get(...))`.
Run tests after each migration to catch regressions immediately.

### Phase D — Delete BasePage, clean step defs, verify
Delete `BasePage.java`. Verify build. Clean any direct Playwright calls in step defs.
Full test run to confirm all 69 pass.

### Phase E — Update CLAUDE.md and specs
Update CLAUDE.md and spec 11 to reflect final state.

---

## Migration order for page objects
1. `LoginPage` — simplest, 5 selectors, no dynamic locators
2. `NavigationComponent` — 5 selectors, all static IDs
3. `ProductDetailsPage` — 6 selectors, no dynamic locators
4. `CartPage` — 6 selectors, one filter-based
5. `CheckoutPage` — 13 selectors, all static
6. `ProductsPage` — most complex, has dynamic/filter-based locators

---

## Risk: dynamic locators
`ProductsPage` uses `.inventory_item.filter(hasText(productName))` — Playwright chaining,
not a simple selector string. The JSON will store the base selector; the page object
builds the filter chain from it. `LocatorStore` returns the selector string only;
chaining stays in the page object method.

---

## Success criteria
- [ ] `./gradlew compileJava compileTestJava` exits 0
- [ ] `BasePage.java` deleted
- [ ] All 6 page objects extend `PlaywrightActions`
- [ ] Zero hardcoded selector strings in any page object Java file
- [ ] All 69 Cucumber scenarios pass headless
- [ ] `CLAUDE.md` updated
