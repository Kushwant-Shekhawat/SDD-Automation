# SDD-Automation — Spec-11 Task List
## Generic Playwright Actions + JSON Locator Repository

### STATUS KEY
- [ ] TODO
- [~] IN PROGRESS
- [x] DONE
- [!] BLOCKED

---

## PHASE A — New infrastructure

- [ ] A1 — Create `PlaywrightActions.java` in `src/main/java/org/example/pages/`
  - Abstract class; constructor takes `Page page`, reads `ConfigReader.getTimeout()`
  - Navigation: `navigateTo(path)`, `navigateToUrl(url)`, `goBack()`, `goForward()`, `reload()`, `getCurrentUrl()`, `waitForUrl(pattern)`
  - Click: `click(Locator)`, `click(String selector)`, `doubleClick(Locator)`, `rightClick(Locator)`, `clickIfVisible(Locator)`, `clickByText(String text)`
  - Input: `fill(Locator, String)`, `fill(String selector, String)`, `type(Locator, String)`, `clear(Locator)`, `pressKey(Locator, String)`, `pressKey(String)`, `uploadFile(Locator, String)`
  - Select/checkbox: `selectByValue(Locator, String)`, `selectByLabel(Locator, String)`, `selectByIndex(Locator, int)`, `check(Locator)`, `uncheck(Locator)`, `isChecked(Locator)`
  - Read: `getText(Locator)`, `getText(String selector)`, `getInputValue(Locator)`, `getAttribute(Locator, String)`, `getAllTexts(Locator)`, `getCount(Locator)`, `getInnerHtml(Locator)`
  - State: `isVisible(Locator)`, `isVisible(Locator, int ms)`, `isEnabled(Locator)`, `isEditable(Locator)`, `isHidden(Locator)`, `hasText(Locator, String)`
  - Wait: `waitForVisible(Locator)`, `waitForVisible(Locator, int ms)`, `waitForHidden(Locator)`, `waitForEnabled(Locator)`, `waitForPageLoad()`, `waitForNetworkIdle()`, `waitForSelector(String)`
  - Hover/scroll: `hover(Locator)`, `focus(Locator)`, `scrollIntoView(Locator)`, `scrollTo(int x, int y)`
  - Drag: `dragAndDrop(Locator source, Locator target)`
  - Dialog: `acceptDialog()`, `dismissDialog()`
  - Screenshot: `takeScreenshot(String fileName)`

- [ ] A2 — Create `LocatorStore.java` in `src/main/java/org/example/utils/`
  - Static class; loads JSON from classpath `locators/<page>.json` on first access; caches per page name
  - `get(String page, String key)` — returns selector string, throws `ConfigurationException` if missing
  - `get(String page, String key, Map<String, String> params)` — substitutes `{param}` tokens
  - `get(String page, String key, String paramName, String paramValue)` — single-param convenience
  - `toLocatorToken(String productName)` — converts "Sauce Labs Backpack" → "sauce-labs-backpack"
  - Uses Jackson `ObjectMapper` to parse JSON

- [ ] A3 — Verify: `./gradlew compileJava` passes with both new files present

---

## PHASE B — JSON locator files

- [ ] B1 — Create `src/test/resources/locators/login.json`
  - Keys: `username`, `password`, `loginButton`, `errorMessage`, `errorDismiss`

- [ ] B2 — Create `src/test/resources/locators/navigation.json`
  - Keys: `menuButton`, `menuContainer`, `menuOpen`, `closeButton`, `logoutLink`, `allItemsLink`, `aboutLink`, `resetLink`

- [ ] B3 — Create `src/test/resources/locators/product-details.json`
  - Keys: `productName`, `productDescription`, `productPrice`, `productImage`, `addToCartButton`, `removeButton`, `backToProducts`, `cartLink`

- [ ] B4 — Create `src/test/resources/locators/cart.json`
  - Keys: `cartTitle`, `cartList`, `cartItem`, `itemName`, `itemPrice`, `itemQuantity`, `continueShopping`, `checkoutButton`

- [ ] B5 — Create `src/test/resources/locators/checkout.json`
  - Keys: `firstName`, `lastName`, `postalCode`, `continueButton`, `cancelButton`, `errorMessage`, `errorDismiss`, `cartItem`, `itemName`, `itemPrice`, `subtotalLabel`, `taxLabel`, `totalLabel`, `finishButton`, `thankYouHeader`, `thankYouText`, `backHomeButton`, `stepOneTitle`, `stepTwoTitle`, `completeTitle`

- [ ] B6 — Create `src/test/resources/locators/products.json`
  - Keys: `inventoryContainer`, `inventoryItem`, `productNameLink`, `productPrice`, `sortDropdown`, `cartBadge`, `cartLink`, `allAddToCartBtns`, `pageTitle`
  - Dynamic: `addToCartButton` with `{dataTestSuffix}`, `removeButton` with `{dataTestSuffix}`

---

## PHASE C — Migrate page objects (one at a time, test after each)

- [ ] C1 — Migrate `LoginPage.java`
  - `extends BasePage` → `extends PlaywrightActions`
  - Replace all `page.locator("...")` with `page.locator(LocatorStore.get("login", "key"))`
  - Run: `./gradlew test -Dcucumber.filter.tags="@login"` — must pass

- [ ] C2 — Migrate `NavigationComponent.java`
  - `extends BasePage` → `extends PlaywrightActions`
  - Replace all `page.locator("...")` with `LocatorStore.get("navigation", "key")`
  - Run: `./gradlew test -Dcucumber.filter.tags="@logout"` — must pass

- [ ] C3 — Migrate `ProductDetailsPage.java`
  - `extends BasePage` → `extends PlaywrightActions`
  - Replace all `page.locator("...")` with `LocatorStore.get("product-details", "key")`
  - Run: `./gradlew test -Dcucumber.filter.tags="@regression"` on product_details scenarios

- [ ] C4 — Migrate `CartPage.java`
  - `extends BasePage` → `extends PlaywrightActions`
  - Replace all `page.locator("...")` with `LocatorStore.get("cart", "key")`
  - Run: `./gradlew test -Dcucumber.filter.tags="@cart"` — must pass

- [ ] C5 — Migrate `CheckoutPage.java`
  - `extends BasePage` → `extends PlaywrightActions`
  - Replace all `page.locator("...")` with `LocatorStore.get("checkout", "key")`
  - Run: `./gradlew test -Dcucumber.filter.tags="@checkout"` — must pass

- [ ] C6 — Migrate `ProductsPage.java`
  - `extends BasePage` → `extends PlaywrightActions`
  - Static selectors via `LocatorStore.get("products", "key")`
  - Dynamic add/remove: `LocatorStore.get("products", "addToCartButton", "dataTestSuffix", token)`
  - Filter-based chaining stays in page object (Playwright API, not a selector string)
  - Run: `./gradlew test -Dcucumber.filter.tags="@smoke"` — must pass

---

## PHASE D — Cleanup and verification

- [ ] D1 — Delete `BasePage.java`
  - Confirm `./gradlew compileJava` still passes (all pages now extend `PlaywrightActions`)

- [ ] D2 — Audit step defs for direct `ctx.page.locator(...)` calls
  - `NavigationSteps.java` — has direct calls; move to `NavigationComponent` or keep if genuinely cross-cutting
  - Any other direct Playwright calls in step defs that belong in a page object

- [ ] D3 — Full headless run: `./gradlew test -Dbrowser.headless=true`
  - All 69 scenarios must pass

---

## PHASE E — Documentation

- [ ] E1 — Update `CLAUDE.md`
  - Add `LocatorStore` to utils section
  - Add locators JSON directory to project structure
  - Note that `BasePage` is deleted; page objects extend `PlaywrightActions`

- [ ] E2 — Commit and push all changes

---

## SUMMARY

| Phase | Tasks | Description |
|-------|-------|-------------|
| A — Infrastructure | A1–A3 | PlaywrightActions + LocatorStore |
| B — JSON files | B1–B6 | One locator file per page |
| C — Page object migration | C1–C6 | One page at a time |
| D — Cleanup | D1–D3 | Delete BasePage, audit step defs, full run |
| E — Docs | E1–E2 | CLAUDE.md update + push |
| **Total** | **18 tasks** | |
