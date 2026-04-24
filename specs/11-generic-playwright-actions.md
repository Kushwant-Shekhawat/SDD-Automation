# SDD-Automation Framework - Generic Playwright Actions & Locator Repository Specification

## 1. OVERVIEW

### Problem this solves
- Locator strings are currently scattered and duplicated across 6 page object Java files — a change to one selector requires hunting through Java code
- `BasePage` exposes only ~6 Playwright actions; page objects and step defs call raw Playwright APIs directly for anything else, creating inconsistency
- No single source of truth for what selectors exist per page

### Solution
1. **`LocatorStore.java`** — loads one JSON file per page from `src/test/resources/locators/`, resolves runtime template substitution, returns selector strings
2. **`PlaywrightActions.java`** — replaces `BasePage`; a comprehensive wrapper over every Playwright browser action; all page objects extend this class
3. **Locator JSON files** — one file per page, all selectors in one place, duplicate-visible by inspection

---

## 2. LOCATOR JSON FILES

### Directory

```
src/test/resources/locators/
├── login.json
├── products.json
├── product-details.json
├── cart.json
├── checkout.json
└── navigation.json
```

### Static locator format

```json
{
  "username":     "[data-test='username']",
  "password":     "[data-test='password']",
  "loginButton":  "[id='login-button']",
  "errorMessage": "[data-test='error']"
}
```

### Dynamic locator format (runtime template substitution)

Use `{paramName}` placeholders. The value is substituted at call time by `LocatorStore`.

```json
{
  "addToCartButton": "[data-test='add-to-cart-{productName}']",
  "removeButton":    "[data-test='remove-{productName}']",
  "productLink":     ".inventory_item:has-text('{productName}') a.inventory_item_name"
}
```

### Full file examples

#### `login.json`
```json
{
  "username":     "[data-test='username']",
  "password":     "[data-test='password']",
  "loginButton":  "[id='login-button']",
  "errorMessage": "[data-test='error']",
  "errorDismiss": ".error-button"
}
```

#### `products.json`
```json
{
  "inventoryContainer": "div.inventory_container",
  "inventoryItem":      "div.inventory_item",
  "productNameLink":    "a.inventory_item_name",
  "productPrice":       "div.inventory_item_price",
  "sortDropdown":       "select.product_sort_container",
  "cartBadge":          "span.shopping_cart_badge",
  "cartLink":           "a.shopping_cart_link",
  "allAddToCartBtns":   ".btn_inventory",
  "addToCartButton":    "[data-test='add-to-cart-{productName}']",
  "removeButton":       "[data-test='remove-{productName}']",
  "productLink":        ".inventory_item:has-text('{productName}') a.inventory_item_name"
}
```

#### `product-details.json`
```json
{
  "productName":        ".inventory_details_name",
  "productDescription": ".inventory_details_desc",
  "productPrice":       ".inventory_details_price",
  "productImage":       ".inventory_details_img",
  "addToCartButton":    "[data-test*='add-to-cart']",
  "removeButton":       "[data-test*='remove']",
  "backToProducts":     "[data-test='back-to-products']",
  "cartLink":           "a.shopping_cart_link"
}
```

#### `cart.json`
```json
{
  "cartList":          "div.cart_list",
  "cartItem":          "div.cart_item",
  "itemName":          "a.inventory_item_name",
  "itemPrice":         "div.inventory_item_price",
  "itemQuantity":      "div.cart_quantity",
  "continueShopping":  "[data-test='continue-shopping']",
  "checkoutButton":    "[data-test='checkout']",
  "removeButton":      "button[data-test='remove-{itemName}']"
}
```

#### `checkout.json`
```json
{
  "firstName":         "[data-test='firstName']",
  "lastName":          "[data-test='lastName']",
  "postalCode":        "[data-test='postalCode']",
  "continueButton":    "[data-test='continue']",
  "cancelButton":      "[data-test='cancel']",
  "errorMessage":      "[data-test='error']",
  "errorDismiss":      ".error-button",
  "cartItem":          "div.cart_item",
  "itemName":          "div.inventory_item_name",
  "itemPrice":         "div.inventory_item_price",
  "subtotalLabel":     "div.summary_subtotal_label",
  "taxLabel":          "div.summary_tax_label",
  "totalLabel":        "div.summary_total_label",
  "finishButton":      "[data-test='finish']",
  "thankYouHeader":    "h2.complete-header",
  "thankYouText":      "div.complete-text",
  "backHomeButton":    "[data-test='back-to-products']"
}
```

#### `navigation.json`
```json
{
  "menuButton":        "#react-burger-menu-btn",
  "menuContainer":     ".bm-menu-wrap",
  "menuOpen":          ".bm-menu-wrap[aria-hidden='false']",
  "closeButton":       "#react-burger-cross-btn",
  "logoutLink":        "#logout_sidebar_link",
  "allItemsLink":      "#inventory_sidebar_link",
  "aboutLink":         "#about_sidebar_link",
  "resetLink":         "#reset_sidebar_link"
}
```

---

## 3. LOCATOR STORE

### Class: `LocatorStore.java`
**Package**: `org.example.utils`

### Responsibilities
- Load JSON files from classpath on first access (cached per page name)
- Resolve `{param}` placeholders via a `Map<String, String>` at call time
- Throw `ConfigurationException` if a key is not found — fail fast, no silent nulls

### API

```java
// Static locator — no substitution needed
public static String get(String page, String key)

// Dynamic locator — substitutes {param} tokens at runtime
public static String get(String page, String key, Map<String, String> params)

// Convenience single-param overload
public static String get(String page, String key, String paramName, String paramValue)
```

### Usage in page objects

```java
// Static
Locator username = page.locator(LocatorStore.get("login", "username"));

// Dynamic — single param
Locator addBtn = page.locator(
    LocatorStore.get("products", "addToCartButton", "productName", "sauce-labs-backpack")
);

// Dynamic — multiple params
Locator el = page.locator(
    LocatorStore.get("cart", "removeButton", Map.of("itemName", "sauce-labs-backpack"))
);
```

### Template substitution rules
- Placeholder format: `{paramName}` (case-sensitive)
- If a placeholder is present in the selector string but no matching key is provided in params, throw `ConfigurationException`
- SauceDemo product name tokens use the kebab-case form of the product name (e.g. `"Sauce Labs Backpack"` → `"sauce-labs-backpack"`) — a helper `toLocatorToken(String productName)` in `LocatorStore` handles this conversion

---

## 4. PLAYWRIGHT ACTIONS CLASS

### Class: `PlaywrightActions.java`
**Package**: `org.example.pages`
**Replaces**: `BasePage.java` — page objects extend `PlaywrightActions` instead of `BasePage`

### Constructor

```java
public abstract class PlaywrightActions {
    protected final Page page;
    protected final int timeout;

    protected PlaywrightActions(Page page) {
        this.page = page;
        this.timeout = ConfigReader.getTimeout();
    }
}
```

### Full method catalogue

#### Navigation
| Method | Description |
|--------|-------------|
| `navigateTo(String path)` | Prepends base URL, navigates, waits for load |
| `navigateToUrl(String fullUrl)` | Navigate to absolute URL |
| `goBack()` | Browser back |
| `goForward()` | Browser forward |
| `reload()` | Reload current page |
| `getCurrentUrl()` | Returns current page URL |
| `waitForUrl(String urlPattern)` | Wait until URL matches pattern |

#### Click actions
| Method | Description |
|--------|-------------|
| `click(Locator)` | Wait for visible, then click |
| `click(String selector)` | Resolve selector, then click |
| `doubleClick(Locator)` | Double-click |
| `rightClick(Locator)` | Right-click (context menu) |
| `clickIfVisible(Locator)` | Click only if element is currently visible |
| `clickByText(String text)` | Click first element matching text |

#### Input actions
| Method | Description |
|--------|-------------|
| `fill(Locator, String text)` | Clear and fill input |
| `fill(String selector, String text)` | Selector overload |
| `type(Locator, String text)` | Type character by character (for inputs with listeners) |
| `clear(Locator)` | Clear input field |
| `pressKey(Locator, String key)` | Press keyboard key on element (e.g. `"Enter"`, `"Tab"`) |
| `pressKey(String key)` | Press key on page (e.g. `"Escape"`) |
| `uploadFile(Locator, String filePath)` | Set file on file input |

#### Select / checkbox
| Method | Description |
|--------|-------------|
| `selectByValue(Locator, String value)` | Select `<option>` by value attribute |
| `selectByLabel(Locator, String label)` | Select `<option>` by visible text |
| `selectByIndex(Locator, int index)` | Select `<option>` by index |
| `check(Locator)` | Check a checkbox |
| `uncheck(Locator)` | Uncheck a checkbox |
| `isChecked(Locator)` | Returns boolean |

#### Read / query
| Method | Description |
|--------|-------------|
| `getText(Locator)` | Returns trimmed `textContent()` |
| `getText(String selector)` | Selector overload |
| `getInputValue(Locator)` | Returns value of input/textarea |
| `getAttribute(Locator, String attr)` | Returns attribute value |
| `getAllTexts(Locator)` | Returns `List<String>` of all matching elements' text |
| `getCount(Locator)` | Returns number of matching elements |
| `getInnerHtml(Locator)` | Returns inner HTML string |

#### State checks
| Method | Description |
|--------|-------------|
| `isVisible(Locator)` | Returns boolean — no wait |
| `isVisible(Locator, int timeoutMs)` | Returns boolean — waits up to timeout |
| `isEnabled(Locator)` | Returns boolean |
| `isEditable(Locator)` | Returns boolean |
| `isHidden(Locator)` | Returns boolean |
| `hasText(Locator, String text)` | Returns boolean — checks text content |

#### Wait actions
| Method | Description |
|--------|-------------|
| `waitForVisible(Locator)` | Wait for VISIBLE state (uses default timeout) |
| `waitForVisible(Locator, int timeoutMs)` | With custom timeout |
| `waitForHidden(Locator)` | Wait for HIDDEN state |
| `waitForEnabled(Locator)` | Wait for ENABLED state |
| `waitForPageLoad()` | Wait for `LOAD` state |
| `waitForNetworkIdle()` | Wait for `NETWORKIDLE` state |
| `waitForSelector(String selector)` | Wait until selector exists in DOM |

#### Hover / focus / scroll
| Method | Description |
|--------|-------------|
| `hover(Locator)` | Mouse hover |
| `focus(Locator)` | Focus element |
| `scrollIntoView(Locator)` | Scroll element into viewport |
| `scrollTo(int x, int y)` | Scroll page to absolute coordinates |

#### Drag and drop
| Method | Description |
|--------|-------------|
| `dragAndDrop(Locator source, Locator target)` | Drag source and drop on target |

#### Dialog handling
| Method | Description |
|--------|-------------|
| `acceptDialog()` | Accept next browser dialog (alert/confirm) |
| `dismissDialog()` | Dismiss next browser dialog |

#### Screenshot
| Method | Description |
|--------|-------------|
| `takeScreenshot(String fileName)` | Full-page screenshot to given path |

---

## 5. HOW PAGE OBJECTS CHANGE

### Before (current pattern)
```java
public class LoginPage extends BasePage {
    private final Locator usernameInput = page.locator("[data-test='username']");
    private final Locator loginButton   = page.locator("[id='login-button']");

    public void login(String username, String password) {
        WaitUtil.waitForVisible(usernameInput);
        usernameInput.fill(username);
        loginButton.click();
    }
}
```

### After (target pattern)
```java
public class LoginPage extends PlaywrightActions {

    public LoginPage(Page page) {
        super(page);
    }

    public void login(String username, String password) {
        fill(page.locator(LocatorStore.get("login", "username")), username);
        fill(page.locator(LocatorStore.get("login", "password")), password);
        click(page.locator(LocatorStore.get("login", "loginButton")));
    }

    public String getErrorMessage() {
        return getText(page.locator(LocatorStore.get("login", "errorMessage")));
    }
}
```

Key changes:
- `extends BasePage` → `extends PlaywrightActions`
- No more `private final Locator` fields — locators resolved at call time via `LocatorStore`
- No more hardcoded selector strings in Java
- No more `WaitUtil` direct calls — `PlaywrightActions` methods handle waiting internally

---

## 6. HOW STEP DEFINITIONS CHANGE

Step defs primarily call page object methods. They should **not** be refactored to call `PlaywrightActions` directly unless the action genuinely does not belong in any page object (e.g. a one-off browser-level action in a negative flow test).

### Acceptable — step def calls page object
```java
@When("I add {string} to cart")
public void iAddToCart(String productName) {
    ctx.productsPage.addProductToCart(productName);
}
```

### Acceptable — step def calls PlaywrightActions directly (no page object owns this)
```java
@When("I press the browser back button")
public void iPressBack() {
    ctx.goBack();   // ctx exposes PlaywrightActions methods via shared page
}
```

### Not acceptable — step def calls Playwright API directly
```java
// Wrong — bypasses both page object and PlaywrightActions
ctx.page.locator("[data-test='username']").fill(username);
```

---

## 7. FILES TO CREATE / MODIFY

### New files
| File | Location |
|------|----------|
| `PlaywrightActions.java` | `src/main/java/org/example/pages/` |
| `LocatorStore.java` | `src/main/java/org/example/utils/` |
| `login.json` | `src/test/resources/locators/` |
| `products.json` | `src/test/resources/locators/` |
| `product-details.json` | `src/test/resources/locators/` |
| `cart.json` | `src/test/resources/locators/` |
| `checkout.json` | `src/test/resources/locators/` |
| `navigation.json` | `src/test/resources/locators/` |

### Files to modify
| File | Change |
|------|--------|
| `BasePage.java` | Delete — replaced by `PlaywrightActions.java` |
| `WaitUtil.java` | Keep — called internally by `PlaywrightActions`; page objects no longer call it directly |
| `LoginPage.java` | `extends PlaywrightActions`, remove Locator fields, use `LocatorStore` |
| `ProductsPage.java` | Same |
| `ProductDetailsPage.java` | Same |
| `CartPage.java` | Same |
| `CheckoutPage.java` | Same |
| `NavigationComponent.java` | Same |
| `BaseTest.java` | Update `BasePage` import → `PlaywrightActions` |
| `SharedContext.java` | No change needed — constructs page objects as before |
| Step defs | Remove any direct `ctx.page.locator(...)` calls that belong in a page object |
| `CLAUDE.md` | Update page objects section and add `LocatorStore` / JSON locator notes |

---

## 8. LOCATOR JSON NAMING CONVENTIONS

- **File name**: lowercase, hyphen-separated, matching the page object name — `product-details.json` for `ProductDetailsPage`
- **Key names**: camelCase, descriptive, noun-first — `addToCartButton`, `errorMessage`, `cartBadge`
- **Template param names**: camelCase, match what the page object passes — `{productName}`, `{itemName}`
- **No prefixing with page name** inside the file — the file itself is the namespace
- **Shared selectors** (e.g. `cartLink` appears in both `products.json` and `product-details.json`) are intentionally duplicated per-page — the JSON is per-page scope, not a global registry

---

## 9. DUPLICATE DETECTION RATIONALE

When the same selector string appears in two different JSON files, it is immediately visible via a simple `grep` or IDE search across the `locators/` directory. This is the primary mechanism for spotting duplication — no tooling required, no runtime check needed.

```bash
# Find duplicate selector strings across all locator files
grep -rh '"' src/test/resources/locators/ | sort | uniq -d
```

---

**Status**: ✅ Generic Playwright Actions & Locator Repository Specification — 2026-04-24
