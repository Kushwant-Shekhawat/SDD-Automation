# SDD-Automation — Spec-11 Task List
## Generic Playwright Actions + JSON Locator Repository

### STATUS KEY
- [ ] TODO
- [~] IN PROGRESS
- [x] DONE
- [!] BLOCKED

---

## PHASE A — New infrastructure

- [x] A1 — Create `PlaywrightActions.java` in `src/main/java/org/example/pages/`
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

- [x] A2 — Create `LocatorStore.java` in `src/main/java/org/example/utils/`
  - Static class; loads JSON from classpath `locators/<page>.json` on first access; caches per page name
  - `get(String page, String key)` — returns selector string, throws `ConfigurationException` if missing
  - `get(String page, String key, Map<String, String> params)` — substitutes `{param}` tokens
  - `get(String page, String key, String paramName, String paramValue)` — single-param convenience
  - `toLocatorToken(String productName)` — converts "Sauce Labs Backpack" → "sauce-labs-backpack"
  - Uses Jackson `ObjectMapper` to parse JSON

- [x] A3 — Verify: `./gradlew compileJava` passes with both new files present

---

## PHASE B — JSON locator files

- [x] B1 — Create `src/test/resources/locators/login.json`
  - Keys: `username`, `password`, `loginButton`, `errorMessage`, `errorDismiss`

- [x] B2 — Create `src/test/resources/locators/navigation.json`
  - Keys: `menuButton`, `menuContainer`, `menuOpen`, `closeButton`, `logoutLink`, `allItemsLink`, `aboutLink`, `resetLink`

- [x] B3 — Create `src/test/resources/locators/product-details.json`
  - Keys: `productName`, `productDescription`, `productPrice`, `productImage`, `addToCartButton`, `removeButton`, `backToProducts`, `cartLink`

- [x] B4 — Create `src/test/resources/locators/cart.json`
  - Keys: `cartTitle`, `cartList`, `cartItem`, `itemName`, `itemPrice`, `itemQuantity`, `continueShopping`, `checkoutButton`

- [x] B5 — Create `src/test/resources/locators/checkout.json`
  - Keys: `firstName`, `lastName`, `postalCode`, `continueButton`, `cancelButton`, `errorMessage`, `errorDismiss`, `cartItem`, `itemName`, `itemPrice`, `subtotalLabel`, `taxLabel`, `totalLabel`, `finishButton`, `thankYouHeader`, `thankYouText`, `backHomeButton`, `stepOneTitle`, `stepTwoTitle`, `completeTitle`

- [x] B6 — Create `src/test/resources/locators/products.json`
  - Keys: `inventoryContainer`, `inventoryItem`, `productNameLink`, `productPrice`, `sortDropdown`, `cartBadge`, `cartLink`, `allAddToCartBtns`, `pageTitle`
  - Dynamic: `addToCartButton` with `{dataTestSuffix}`, `removeButton` with `{dataTestSuffix}`

---

## PHASE C — Migrate page objects (one at a time, test after each)

- [x] C1 — Migrate `LoginPage.java`
- [x] C2 — Migrate `NavigationComponent.java`
- [x] C3 — Migrate `ProductDetailsPage.java`
- [x] C4 — Migrate `CartPage.java`
- [x] C5 — Migrate `CheckoutPage.java`
- [x] C6 — Migrate `ProductsPage.java`

---

## PHASE D — Cleanup and verification

- [x] D1 — Delete `BasePage.java`
- [x] D2 — Audit step defs for direct `ctx.page.locator(...)` calls
  - `NavigationSteps.java` — 3 direct calls moved to `NavigationComponent.clickLogoutLink()` / `clickResetLink()`
- [x] D3 — Full headless run: all 69 scenarios pass

---

## PHASE E — Documentation

- [x] E1 — Update `CLAUDE.md` — `LocatorStore` added, locators directory added, `BasePage` deletion noted
- [x] E2 — Commit and push all changes

---

## SUMMARY

| Phase | Tasks | Status |
|-------|-------|--------|
| A — Infrastructure | A1–A3 | ✅ Complete |
| B — JSON files | B1–B6 | ✅ Complete |
| C — Page object migration | C1–C6 | ✅ Complete |
| D — Cleanup | D1–D3 | ✅ Complete |
| E — Docs | E1–E2 | ✅ Complete |
| **Total** | **18 tasks** | **✅ All done** |
