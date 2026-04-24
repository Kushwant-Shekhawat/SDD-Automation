# SDD-Automation Framework - Configuration Management Specification

## 1. CONFIGURATION OVERVIEW

### Strategy
- Property files loaded via classpath (`ClassLoader.getResourceAsStream`)
- Three-level hierarchy: base → environment override → system properties
- No hardcoded values — all configuration via `ConfigReader`
- Environment override selected by `ENV` system property or env variable (`local` or `ci`)

### Configuration Hierarchy (Highest priority first)
1. System properties (`-Dkey=value` JVM flags)
2. `config/config-ci.properties` — when `ENV=ci`
3. `config/config-local.properties` — when `ENV=local` (default)
4. `config.properties` — base defaults (always loaded first)

---

## 2. CONFIGURATION FILES STRUCTURE

```
src/test/resources/
├── config.properties                # Base defaults (classpath root)
└── config/
    ├── config-local.properties      # Local machine overrides
    └── config-ci.properties         # CI/CD pipeline overrides
```

---

## 3. BASE CONFIGURATION FILE

### File: `config.properties`

```properties
base.url=https://www.saucedemo.com

browser.type=chromium
browser.headless=false
browser.timeout=10000
browser.slow.mo=0

user.standard=standard_user
user.locked=locked_out_user
user.problem=problem_user
user.performance=performance_glitch_user
user.password=secret_sauce

screenshot.on.failure=true
screenshot.path=build/reports/screenshots

extent.report.path=build/reports/extent/ExtentReport.html
extent.report.title=SauceDemo Automation Report
extent.report.name=SDD Automation Suite

cucumber.report.path=build/reports/cucumber

retry.count=1
```

---

## 4. LOCAL DEVELOPMENT CONFIGURATION

### File: `config/config-local.properties`

Activated when `ENV=local` (the default when `ENV` is not set).

```properties
browser.headless=true
browser.timeout=10000
browser.slow.mo=0
```

---

## 5. CI/CD PIPELINE CONFIGURATION

### File: `config/config-ci.properties`

Activated when `ENV=ci` is set as a system property or environment variable.

```properties
browser.headless=true
browser.timeout=15000
screenshot.on.failure=true
```

---

## 6. CONFIGURATION READER IMPLEMENTATION

### Actual implementation in `ConfigReader.java`

```java
static {
    loadProperties("config.properties");
    String env = System.getProperty("ENV", System.getenv("ENV") != null
            ? System.getenv("ENV") : "local");
    if ("ci".equalsIgnoreCase(env)) {
        loadProperties("config/config-ci.properties");
    } else {
        loadProperties("config/config-local.properties");
    }
    properties.putAll(System.getProperties());
}
```

### Public API

| Method | Returns | Description |
|--------|---------|-------------|
| `getConfig(key)` | `String` | Throws `ConfigurationException` if missing |
| `getConfig(key, default)` | `String` | Returns default if missing |
| `getBoolean(key)` | `boolean` | Parses `"true"`/`"false"` |
| `getBoolean(key, default)` | `boolean` | With fallback |
| `getInt(key)` | `int` | Parses integer value |
| `getInt(key, default)` | `int` | With fallback |
| `getBaseUrl()` | `String` | `base.url` |
| `getBrowserType()` | `String` | `browser.type`, default `chromium` |
| `isHeadless()` | `boolean` | `browser.headless`, default `false` |
| `getTimeout()` | `int` | `browser.timeout`, default `10000` |

---

## 7. USAGE IN CODE

### WebDriverManager
```java
String browserType = ConfigReader.getBrowserType();
boolean headless = ConfigReader.isHeadless();
int slowMo = ConfigReader.getInt("browser.slow.mo", 0);
```

### BasePage
```java
this.timeout = ConfigReader.getTimeout();
```

### BaseTest
```java
page.navigate(ConfigReader.getBaseUrl());
```

---

## 8. RUNNING TESTS WITH DIFFERENT CONFIGURATIONS

```bash
# Default (local overrides)
./gradlew clean test

# CI mode (headless, longer timeout)
ENV=ci ./gradlew clean test

# Override browser at runtime
./gradlew clean test -Dbrowser.type=firefox

# Override headless at runtime
./gradlew clean test -Dbrowser.headless=false
```

---

## 9. BEST PRACTICES

- Never hardcode URLs, timeouts, or credentials in Java classes
- Use `getConfig(key, default)` for optional keys to avoid exceptions on missing values
- Use `getConfig(key)` (no default) for required keys — will throw `ConfigurationException` with a clear message
- System property overrides (`-Dkey=value`) take highest priority — useful for one-off CI overrides

---

**Status**: ✅ Configuration Management Specification — accurate as of 2026-04-24
