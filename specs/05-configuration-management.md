# SDD-Automation Framework - Configuration Management Specification

## 1. CONFIGURATION OVERVIEW

### Configuration Strategy
- **Multiple Environments**: Local development, CI/CD
- **Property Files**: .properties format for easy management
- **Environment Override**: CI/CD environment variables override local config
- **No Hardcoding**: All configuration externalized
- **Runtime Selection**: Automatic selection based on environment

### Configuration Hierarchy (Priority - Highest to Lowest)
1. Environment Variables (CI/CD)
2. System Properties (JVM flags)
3. config-ci.properties (CI/CD pipeline)
4. config-local.properties (Local machine)
5. config.properties (Default values)

---

## 2. CONFIGURATION FILES STRUCTURE

### Directory Structure
```
src/test/resources/config/
├── config.properties           # Default/base configuration
├── config-local.properties     # Local development overrides
└── config-ci.properties        # CI/CD pipeline overrides
```

---

## 3. BASE CONFIGURATION FILE

### File: config.properties

```properties
# ============================================
# BASE CONFIGURATION - DEFAULT VALUES
# ============================================

# Application Settings
base.url=https://www.saucedemo.com

# Browser Configuration
browser.type=chromium
browser.timeout=10000
browser.headless=false
browser.viewport.width=1280
browser.viewport.height=720
browser.trace=true
browser.video=false
browser.screenshot=true

# Playwright Settings
playwright.timeout=10000
playwright.navigation.timeout=30000
playwright.action.timeout=10000

# Logging
log.level=INFO
log.pattern=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n

# Screenshots
screenshot.on.failure=true
screenshot.path=build/reports/screenshots
screenshot.format=png

# Video Recording
video.record=false
video.path=build/reports/videos

# Test Execution
test.retry.count=1
test.timeout=60
parallel.thread.count=4

# Reporting
extent.report.enabled=true
extent.report.title=SDD-Automation Test Report
extent.report.author=QA Team
extent.report.theme=standard
extent.report.path=build/reports/extentreports/index.html

# Wait Strategies
wait.element.timeout=10
wait.page.load.timeout=15
wait.implicit.timeout=5

# Test Data Paths
testdata.path=src/test/resources/testdata/

# CI/CD Flags
ci.environment=false
ci.headless=true
ci.parallel=true
```

---

## 4. LOCAL DEVELOPMENT CONFIGURATION

### File: config-local.properties

**Purpose**: Override config.properties for local development
**When Used**: Running tests on local machine
**Usage**: For QA engineers and developers

```properties
# ============================================
# LOCAL DEVELOPMENT CONFIGURATION
# ============================================

# Browser - Headed mode for debugging
browser.headless=false
browser.trace=true
browser.video=false
browser.screenshot=true

# Logging - Verbose for debugging
log.level=DEBUG

# Screenshots - Save all for analysis
screenshot.on.failure=true
screenshot.path=build/reports/screenshots

# Test Execution - Sequential for debugging
parallel.thread.count=1
test.retry.count=0

# Wait Strategies - Longer timeouts for slow machines
wait.element.timeout=15
wait.page.load.timeout=20

# Reporting
extent.report.enabled=true
```

---

## 5. CI/CD PIPELINE CONFIGURATION

### File: config-ci.properties

**Purpose**: Override config.properties for CI/CD pipeline
**When Used**: Running tests in GitHub Actions/Jenkins
**Usage**: Automated test execution

```properties
# ============================================
# CI/CD PIPELINE CONFIGURATION
# ============================================

# Browser - Headless for CI/CD
browser.headless=true
browser.viewport.width=1920
browser.viewport.height=1080

# Logging - Info level (less noise)
log.level=INFO

# Screenshots - On failure only
screenshot.on.failure=true
screenshot.path=/tmp/screenshots

# Video Recording - For failed tests
video.record=false
video.path=/tmp/videos

# Test Execution - Parallel for speed
parallel.thread.count=4
test.retry.count=2
ci.environment=true
ci.headless=true
ci.parallel=true

# Timeouts - Shorter for CI (faster feedback)
wait.element.timeout=10
wait.page.load.timeout=15

# Reporting
extent.report.enabled=true
extent.report.path=/tmp/test-reports/index.html
```

---

## 6. CONFIGURATION READER IMPLEMENTATION

### Java Code: ConfigReader.java

```java
package org.example.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    
    private static Properties properties = null;
    private static final String CONFIG_PATH = "src/test/resources/config/";
    
    static {
        loadProperties();
    }
    
    /**
     * Load configuration files in priority order
     * 1. config.properties (default)
     * 2. config-local.properties or config-ci.properties (overrides)
     */
    private static void loadProperties() {
        try {
            properties = new Properties();
            
            // Load base configuration
            String basePath = CONFIG_PATH + "config.properties";
            properties.load(new FileInputStream(basePath));
            LoggerUtil.info("Loaded base configuration: " + basePath);
            
            // Determine environment
            String environment = System.getProperty("env", "local");
            if (System.getenv("CI") != null || System.getenv("GITHUB_ACTIONS") != null) {
                environment = "ci";
            }
            
            // Load environment-specific configuration
            String envConfigPath = CONFIG_PATH + "config-" + environment + ".properties";
            try {
                properties.load(new FileInputStream(envConfigPath));
                LoggerUtil.info("Loaded " + environment + " configuration: " + envConfigPath);
            } catch (IOException e) {
                LoggerUtil.warning("Environment config not found, using base config: " + envConfigPath);
            }
            
            // Load system properties (highest priority)
            properties.putAll(System.getProperties());
            
            // Load environment variables (highest priority)
            for (String key : System.getenv().keySet()) {
                properties.setProperty(key, System.getenv(key));
            }
            
            LoggerUtil.info("Configuration loaded successfully");
            
        } catch (IOException e) {
            LoggerUtil.error("Failed to load configuration: " + e.getMessage());
            throw new RuntimeException("Configuration loading failed", e);
        }
    }
    
    /**
     * Get configuration value with default fallback
     */
    public static String getConfig(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Configuration key not found: " + key);
        }
        return value;
    }
    
    /**
     * Get configuration value with default
     */
    public static String getConfig(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Get configuration as boolean
     */
    public static boolean getConfigBoolean(String key) {
        return Boolean.parseBoolean(getConfig(key));
    }
    
    /**
     * Get configuration as boolean with default
     */
    public static boolean getConfigBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(getConfig(key, String.valueOf(defaultValue)));
    }
    
    /**
     * Get configuration as integer
     */
    public static int getConfigInt(String key) {
        return Integer.parseInt(getConfig(key));
    }
    
    /**
     * Get configuration as integer with default
     */
    public static int getConfigInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(getConfig(key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Get configuration as long
     */
    public static long getConfigLong(String key) {
        return Long.parseLong(getConfig(key));
    }
    
    // ===== Common Configuration Getters =====
    
    public static String getBaseUrl() {
        return getConfig("base.url");
    }
    
    public static String getBrowserType() {
        return getConfig("browser.type", "chromium");
    }
    
    public static int getBrowserTimeout() {
        return getConfigInt("browser.timeout", 10000);
    }
    
    public static boolean isHeadless() {
        return getConfigBoolean("browser.headless", false);
    }
    
    public static boolean isHeadlessCi() {
        return getConfigBoolean("ci.headless", true);
    }
    
    public static int getViewportWidth() {
        return getConfigInt("browser.viewport.width", 1280);
    }
    
    public static int getViewportHeight() {
        return getConfigInt("browser.viewport.height", 720);
    }
    
    public static boolean shouldRecordVideo() {
        return getConfigBoolean("browser.video", false);
    }
    
    public static boolean shouldTakeScreenshots() {
        return getConfigBoolean("browser.screenshot", true);
    }
    
    public static boolean shouldScreenshotOnFailure() {
        return getConfigBoolean("screenshot.on.failure", true);
    }
    
    public static String getScreenshotPath() {
        return getConfig("screenshot.path", "build/reports/screenshots");
    }
    
    public static String getVideoPath() {
        return getConfig("video.path", "build/reports/videos");
    }
    
    public static int getWaitTimeout() {
        return getConfigInt("wait.element.timeout", 10);
    }
    
    public static int getPageLoadTimeout() {
        return getConfigInt("wait.page.load.timeout", 15);
    }
    
    public static int getParallelThreadCount() {
        return getConfigInt("parallel.thread.count", 4);
    }
    
    public static boolean isCiEnvironment() {
        return getConfigBoolean("ci.environment", false);
    }
    
    public static boolean shouldParallelize() {
        return getConfigBoolean("ci.parallel", true);
    }
    
    public static String getLogLevel() {
        return getConfig("log.level", "INFO");
    }
    
    public static String getExtentReportPath() {
        return getConfig("extent.report.path", "build/reports/extentreports/index.html");
    }
    
    public static boolean isExtentReportEnabled() {
        return getConfigBoolean("extent.report.enabled", true);
    }
    
    public static int getTestRetryCount() {
        return getConfigInt("test.retry.count", 1);
    }
    
    public static String getTestDataPath() {
        return getConfig("testdata.path", "src/test/resources/testdata/");
    }
}
```

---

## 7. ENVIRONMENT VARIABLE MAPPING

### CI/CD Environment Variables (GitHub Actions / Jenkins)

```bash
# Browser Settings
CI_BASE_URL=https://www.saucedemo.com
CI_BROWSER_TYPE=chromium
CI_BROWSER_HEADLESS=true
CI_BROWSER_VIEWPORT_WIDTH=1920
CI_BROWSER_VIEWPORT_HEIGHT=1080

# Timeouts
CI_BROWSER_TIMEOUT=10000
CI_WAIT_ELEMENT_TIMEOUT=10
CI_WAIT_PAGE_LOAD_TIMEOUT=15

# Execution
CI_PARALLEL_THREAD_COUNT=4
CI_TEST_RETRY_COUNT=2

# Paths
CI_SCREENSHOT_PATH=/tmp/screenshots
CI_VIDEO_PATH=/tmp/videos
CI_EXTENT_REPORT_PATH=/tmp/test-reports

# Flags
CI_ENVIRONMENT=true
CI_HEADLESS=true
CI_PARALLEL=true

# Logging
CI_LOG_LEVEL=INFO
```

---

## 8. USAGE IN CODE

### Example 1: WebDriverManager using Configuration

```java
public class WebDriverManager {
    
    public static void launchBrowser() {
        String browserType = ConfigReader.getBrowserType();
        boolean headless = ConfigReader.isCiEnvironment() 
            ? ConfigReader.isHeadlessCi() 
            : ConfigReader.isHeadless();
        int timeout = ConfigReader.getBrowserTimeout();
        int width = ConfigReader.getViewportWidth();
        int height = ConfigReader.getViewportHeight();
        
        BrowserType type = BrowserType.valueOf(browserType.toUpperCase());
        
        LaunchOptions options = new BrowserType.LaunchOptions()
            .setHeadless(headless)
            .setTimeout(timeout);
        
        Browser browser = playwright.chromium().launch(options);
        
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(width, height));
        
        page = context.newPage();
        page.navigate(ConfigReader.getBaseUrl());
    }
}
```

### Example 2: BaseTest using Configuration

```java
public class BaseTest {
    
    protected Page page;
    protected ExtentTest test;
    
    @BeforeMethod
    public void setUp() {
        int timeout = ConfigReader.getWaitTimeout() * 1000;
        int threadCount = ConfigReader.getParallelThreadCount();
        
        WebDriverManager.launchBrowser();
        page = WebDriverManager.getPage();
        
        page.setDefaultTimeout(timeout);
        page.setDefaultNavigationTimeout(ConfigReader.getPageLoadTimeout() * 1000);
    }
    
    @AfterMethod
    public void tearDown() {
        if (ConfigReader.shouldScreenshotOnFailure()) {
            // Take screenshot on failure
        }
        
        WebDriverManager.closeBrowser();
    }
}
```

### Example 3: Screenshot Utility using Configuration

```java
public class ScreenshotUtil {
    
    public static void captureScreenshot(Page page, String fileName) {
        if (ConfigReader.shouldTakeScreenshots()) {
            String path = ConfigReader.getScreenshotPath() + "/" + fileName + ".png";
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(path)));
        }
    }
}
```

---

## 9. GRADLE BUILD CONFIGURATION

### build.gradle with Configuration Support

```gradle
test {
    useTestNG {
        // Select TestNG suite based on environment
        String suiteFile = System.getProperty("suiteFile", "testng.xml")
        suites "src/test/resources/testng/" + suiteFile
    }
    
    // System property configuration
    systemProperty 'env', System.getProperty('env', 'local')
    systemProperty 'browser.headless', System.getProperty('browser.headless', 'false')
    
    // Environment variable pass-through
    environment System.getenv()
    
    testLogging {
        events "passed", "skipped", "failed"
        exceptionFormat "full"
    }
}

task runParallel {
    doFirst {
        System.setProperty('parallel', 'true')
    }
    dependsOn test
}

task runSeq {
    doFirst {
        System.setProperty('parallel', 'false')
    }
    dependsOn test
}
```

---

## 10. GITHUB ACTIONS WORKFLOW

### File: .github/workflows/test.yml

```yaml
name: Automated Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v2
      
      - name: Set up Java
        uses: actions/setup-java@v2
        with:
          java-version: '11'
      
      - name: Run tests
        run: gradle test
        env:
          CI: true
          GITHUB_ACTIONS: true
          CI_BASE_URL: https://www.saucedemo.com
          CI_BROWSER_HEADLESS: true
          CI_PARALLEL_THREAD_COUNT: 4
          CI_SCREENSHOT_PATH: /tmp/screenshots
          CI_EXTENT_REPORT_PATH: /tmp/test-reports
      
      - name: Upload test reports
        if: always()
        uses: actions/upload-artifact@v2
        with:
          name: test-reports
          path: build/reports/
```

---

## 11. RUNNING TESTS WITH DIFFERENT CONFIGURATIONS

### Command Examples

```bash
# Run with default local configuration
gradle test

# Run with specific environment
gradle test -Denv=local
gradle test -Denv=ci

# Run with headless mode
gradle test -Dbrowser.headless=true

# Run parallel (4 threads)
gradle test -Dparallel=true -Dparallel.thread.count=4

# Run sequential
gradle test -Dparallel=false

# Run with custom timeout
gradle test -Dwait.element.timeout=20

# Run smoke tests with CI configuration
gradle test -DsuiteFile=testng-smoke.xml -Denv=ci

# Run with video recording
gradle test -Dbrowser.video=true
```

---

## 12. CONFIGURATION VALIDATION

### Startup Validation (in BaseTest)

```java
public class BaseTest {
    
    @BeforeClass
    public static void validateConfiguration() {
        try {
            String baseUrl = ConfigReader.getBaseUrl();
            String browserType = ConfigReader.getBrowserType();
            int timeout = ConfigReader.getBrowserTimeout();
            
            LoggerUtil.info("Configuration validated:");
            LoggerUtil.info("  Base URL: " + baseUrl);
            LoggerUtil.info("  Browser: " + browserType);
            LoggerUtil.info("  Timeout: " + timeout);
            
            // Validate URL format
            if (!baseUrl.startsWith("http")) {
                throw new RuntimeException("Invalid base URL: " + baseUrl);
            }
            
        } catch (Exception e) {
            LoggerUtil.error("Configuration validation failed: " + e.getMessage());
            throw e;
        }
    }
}
```

---

## 13. BEST PRACTICES

### Configuration Management Best Practices

✅ **Externalize All Configuration**
- Never hardcode URLs, timeouts, paths
- Use ConfigReader for all configuration access

✅ **Environment Parity**
- Keep local and CI configurations similar
- Differences should be minimal

✅ **Meaningful Defaults**
- All config.properties values should have sensible defaults
- Should work out-of-box for local development

✅ **Documentation**
- Document each configuration parameter
- Include examples for common scenarios

✅ **Version Control**
- Keep config files in version control
- Comment changes in commits

✅ **No Secrets in Code**
- Use environment variables for sensitive data
- Pass credentials through CI/CD secrets

---

## 14. NEXT PHASE: CLARIFY

This configuration specification will be reviewed for:
1. Completeness of configuration parameters
2. Environment variable naming consistency
3. Default value appropriateness
4. CI/CD integration requirements

**Status**: ✅ Configuration Management Specification Complete