# SDD-Automation Framework - Cross-Browser Testing Specification

## 1. OVERVIEW

Cross-browser tests run the core happy-path and critical negative scenarios against
Firefox and WebKit (Safari) in addition to Chromium. No new test logic is introduced —
the same page objects and step definitions are reused; only the browser is varied.

---

## 2. SUPPORTED BROWSERS

| Browser | Playwright Engine | Config Value |
|---------|-------------------|--------------|
| Chromium | `playwright.chromium()` | `chromium` |
| Firefox | `playwright.firefox()` | `firefox` |
| WebKit | `playwright.webkit()` | `webkit` |

---

## 3. CONFIGURATION

### config.properties key

properties
browser.type=chromium


### Running on a specific browser

bash
# Firefox
./gradlew test -Dbrowser.type=firefox -DsuiteFile=src/test/resources/testng/testng.xml

# WebKit
./gradlew test -Dbrowser.type=webkit -DsuiteFile=src/test/resources/testng/testng.xml


### WebDriverManager already supports all browsers:

java
switch (browserType.toLowerCase()) {
    case "firefox": browser = playwright.firefox().launch(options); break;
    case "webkit":  browser = playwright.webkit().launch(options);  break;
    default:        browser = playwright.chromium().launch(options);
}


No code changes required — WebDriverManager already implements the switch.

---

## 4. CROSS-BROWSER TESTNG SUITE

File: `src/test/resources/testng/testng-cross-browser.xml`

xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Cross-Browser Suite" parallel="none" verbose="1">

    <listeners>
        <listener class-name="org.example.listeners.ExtentTestListener"/>
    </listeners>

    <!-- Chromium -->
    <test name="Chromium - Login Tests">
        <parameter name="browser.type" value="chromium"/>
        <classes>
            <class name="org.example.tests.LoginTest"/>
        </classes>
    </test>

    <!-- Firefox -->
    <test name="Firefox - Login Tests">
        <parameter name="browser.type" value="firefox"/>
        <classes>
            <class name="org.example.tests.LoginTest"/>
        </classes>
    </test>

    <!-- WebKit -->
    <test name="WebKit - Login Tests">
        <parameter name="browser.type" value="webkit"/>
        <classes>
            <class name="org.example.tests.LoginTest"/>
        </classes>
    </test>

</suite>


### BaseTest update for TestNG parameters

Add `@Parameters` support to `BaseTest.setUp()`:

java
@Parameters("browser.type")
@BeforeMethod
public void setUp(@Optional String browser, Method method) {
    if (browser != null) {
        System.setProperty("browser.type", browser);
    }
    // existing setUp logic
}


---

## 5. TEST SCENARIOS

### CB-001: Login works on Firefox
**Priority**: P1 — run LoginTest on Firefox  

### CB-002: Login works on WebKit
**Priority**: P1 — run LoginTest on WebKit  

### CB-003: Add to cart and checkout on Firefox
**Priority**: P1 — run CartTest + CheckoutTest on Firefox  

### CB-004: Add to cart and checkout on WebKit
**Priority**: P1 — run CartTest + CheckoutTest on WebKit  

### CB-005: Product sort on Firefox
**Priority**: P2 — run ProductTest on Firefox  

### CB-006: Product sort on WebKit
**Priority**: P2 — run ProductTest on WebKit  

---

## 6. CI INTEGRATION

### Running all browsers in CI

bash
# Chromium (default)
./gradlew test -DsuiteFile=src/test/resources/testng/testng.xml

# Firefox
./gradlew test -Dbrowser.type=firefox -DsuiteFile=src/test/resources/testng/testng.xml

# WebKit
./gradlew test -Dbrowser.type=webkit -DsuiteFile=src/test/resources/testng/testng.xml


### GitHub Actions example

yaml
strategy:
  matrix:
    browser: [chromium, firefox, webkit]
steps:
  - run: ./gradlew test -Dbrowser.type=${{ matrix.browser }}


---

## 7. KNOWN BROWSER DIFFERENCES

| Scenario | Chromium | Firefox | WebKit |
|----------|----------|---------|--------|
| `selectOption` on sort dropdown | Works | Works | Works |
| `history.pushState` navigation | Instant | Instant | May need extra wait |
| `waitForURL` timeout | 5000ms | 5000ms | Increase to 8000ms |
| Cookie handling | Default | Default | May clear session faster |

### WebKit timeout recommendation

Increase `browser.timeout` to 15000 in `config-ci.properties` when running WebKit:

properties
browser.timeout=15000

