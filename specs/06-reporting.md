# SDD-Automation Framework - Reporting Specification (CORRECTED)

## 1. REPORTING OVERVIEW

### Strategy
- **Tool**: ExtentReports 5.0.9
- **Format**: HTML Dashboard
- **Screenshots**: Attached to failed tests
- **Categorization**: Tests grouped by category
- **Timeline**: Execution timeline visualization
- **CI/CD**: Report artifacts uploaded

### Features
- Test execution summary (Pass/Fail/Skip)
- Detailed test logs
- Screenshots on failure
- Execution time tracking
- Test categorization
- Environment details
- Dashboard metrics

---

## 2. GRADLE DEPENDENCIES

Add to build.gradle:


testImplementation 'com.aventstack:extentreports:5.0.9'
testImplementation 'com.aventstack:extentreports-testng-adapter:1.10.10'


---

## 3. CONFIGURATION

Add to config.properties:


extent.report.enabled=true
extent.report.title=SDD-Automation Test Report
extent.report.author=QA Team
extent.report.theme=standard
extent.report.path=build/reports/extentreports/index.html
screenshot.on.failure=true
screenshot.path=build/reports/screenshots


---

## 4. REPORT GENERATION

### Report Files Generated


build/reports/
├── extentreports/
│   ├── index.html (Main dashboard)
│   └── screenshots/
│       ├── test-name-1.png
│       ├── test-name-2.png
│       └── ...
└── testng-results/
    └── index.html


---

## 5. EXTENT TEST LISTENER

### Class: ExtentTestListener.java

**Implements**: ITestListener

**Key Methods**:
- onStart(ITestContext context): Initialize ExtentReports
- onTestStart(ITestResult result): Create ExtentTest
- onTestSuccess(ITestResult result): Log pass with screenshot
- onTestFailure(ITestResult result): Log failure with exception
- onTestSkipped(ITestResult result): Log skipped status
- onFinish(ITestContext context): Generate HTML report

**Features**:
- Thread-safe using ThreadLocal<ExtentTest>
- Automatic screenshot on failure
- Test categories from TestNG groups
- System information included
- Execution timestamps

### Implementation Outline

java
public class ExtentTestListener implements ITestListener {
    
    private static volatile ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    @Override
    public synchronized void onStart(ITestContext context) {
        if (extentReports == null) {
            initializeExtentReports(context);
        }
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        ExtentTest test = extentReports.createTest(testName, description);
        
        String[] groups = result.getMethod().getGroups();
        for (String group : groups) {
            test.assignCategory(group);
        }
        extentTest.set(test);
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        extentTest.get().pass("Test PASSED");
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        extentTest.get().fail(result.getThrowable());
        attachScreenshot();
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        extentTest.get().skip("Test SKIPPED");
    }
    
    @Override
    public void onFinish(ITestContext context) {
        extentReports.flush();
    }
    
    private void initializeExtentReports(ITestContext context) {
        String reportPath = ConfigReader.getConfig("extent.report.path");
        new java.io.File(reportPath).getParentFile().mkdirs();
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setReportName(ConfigReader.getConfig("extent.report.title", "SDD-Automation Report"));
        sparkReporter.config().setTheme(Theme.STANDARD);
        extentReports = new ExtentReports();
        extentReports.attachReporter(sparkReporter);
        addSystemInfo(context);
    }
    
    private void addSystemInfo(ITestContext context) {
        extentReports.setSystemInfo("Test Suite", context.getSuite().getName());
        extentReports.setSystemInfo("Browser", ConfigReader.getBrowserType());
        extentReports.setSystemInfo("Headless", String.valueOf(ConfigReader.isHeadless()));
        extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
        extentReports.setSystemInfo("OS", System.getProperty("os.name"));
    }
    
    private void attachScreenshot() {
        try {
            String screenshotPath = ScreenshotUtil.captureScreenshot(WebDriverManager.getPage(), "screenshot");
            if (screenshotPath != null) {
                extentTest.get().addScreenCaptureFromPath(screenshotPath);
            }
        } catch (Exception e) {
            System.err.println("Failed to attach screenshot: " + e.getMessage());
        }
    }
    
    public static ExtentTest getTest() {
        return extentTest.get();
    }
    
    public static void removeTest() {
        extentTest.remove();
    }
}


---

## 6. TESTNG.XML LISTENER CONFIGURATION

xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "http://testng.org/testng-current.dtd">
<suite name="SDD-Automation Suite" parallel="classes" thread-count="1">
    
    <listeners>
        <listener class-name="org.example.listeners.ExtentTestListener"/>
        <listener class-name="com.aventstack.extentreports.testng.adapter.ExtentITestListenerAdapter"/>
    </listeners>
    
    <test name="SDD-Automation Tests">
        <classes>
            <class name="org.example.runners.CucumberRunner"/>
            <class name="org.example.tests.LoginTests"/>
            <class name="org.example.tests.ProductsTests"/>
            <class name="org.example.tests.ShoppingCartTests"/>
            <class name="org.example.tests.CheckoutTests"/>
        </classes>
    </test>
    
</suite>


---

## 7. SCREENSHOT UTILITY

### Class: ScreenshotUtil.java

java
public class ScreenshotUtil {
    
    public static String captureScreenshot(Page page, String testName) {
        if (!ConfigReader.shouldTakeScreenshots()) {
            return null;
        }
        
        String timestamp = System.currentTimeMillis() + "";
        String fileName = testName + "_" + timestamp + ".png";
        String path = ConfigReader.getScreenshotPath() + "/" + fileName;
        
        new File(ConfigReader.getScreenshotPath()).mkdirs();
        
        page.screenshot(new Page.ScreenshotOptions()
            .setPath(Paths.get(path))
        );
        
        return path;
    }
    
    public static void attachScreenshotToReport(String filePath) {
        if (filePath != null) {
            ExtentTestListener.getTest()
                .addScreenCaptureFromPath(filePath);
        }
    }
}


---

## 8. USAGE IN TEST CLASSES

### TestNG Test with Reporting

java
public class LoginTests extends BaseTest {
    
    @Test(groups = {"smoke"}, description = "User can login with valid credentials")
    public void testLoginWithValidCredentials() {
        ExtentTestListener.getTest()
            .info("Test: User login with valid credentials");
        
        LoginPage loginPage = new LoginPage(page);
        ExtentTestListener.getTest()
            .info("Entering username: standard_user");
        loginPage.enterUsername("standard_user");
        
        ExtentTestListener.getTest()
            .info("Entering password");
        loginPage.enterPassword("secret_sauce");
        
        ExtentTestListener.getTest()
            .info("Clicking login button");
        loginPage.clickLoginButton();
        
        assertTrue(page.url().contains("inventory.html"));
        ExtentTestListener.getTest()
            .pass("Login successful");
    }
    
    @Test(description = "User cannot login with invalid password")
    public void testLoginWithInvalidPassword() {
        ExtentTestListener.getTest()
            .info("Testing invalid password scenario");
        
        LoginPage loginPage = new LoginPage(page);
        loginPage.login("standard_user", "wrong_password");
        
        assertTrue(loginPage.isErrorMessageDisplayed());
        ExtentTestListener.getTest()
            .pass("Error message displayed as expected");
    }
}


---

## 9. USAGE IN CUCUMBER STEPS

### Step Definitions with Reporting

java
@When("User enters username {string}")
public void userEntersUsername(String username) {
    ExtentTestListener.getTest()
        .info("Entering username: " + username);
    loginPage.enterUsername(username);
}

@Then("User should see the products page")
public void userShouldSeeProductsPage() {
    ExtentTestListener.getTest()
        .info("Verifying products page is displayed");
    assertTrue(page.url().contains("inventory.html"));
    ExtentTestListener.getTest()
        .pass("Products page verified");
}


---

## 10. REPORT DASHBOARD FEATURES

### Dashboard Displays

- Test Summary: Total, Passed, Failed, Skipped
- Pass Rate Percentage
- Execution Time
- Test Timeline
- Category Distribution
- Environment Information
- System Details

### Test Details View

- Test name and description
- Execution time
- Pass/Fail/Skip status
- Detailed logs
- Screenshots
- Exception details
- Stack traces

---

## 11. REPORT CUSTOMIZATION

### Report Theme Options


Standard (default blue)
Dark (dark theme)
Light (light minimal)


### Customize Report

java
sparkReporter.config().setTheme(Theme.DARK);
sparkReporter.config().setDocumentTitle("Company Test Report");
sparkReporter.config().setReportName("Automation Test Results");


---

## 12. RUNNING TESTS AND GENERATING REPORTS

### Command: Generate Reports

bash
# Sequential execution with reports
gradle test -DsuiteFile=testng.xml

# Parallel execution with reports
gradle test --parallel --max-workers=4 -DsuiteFile=testng-parallel.xml

# Smoke tests with reports
gradle test -DsuiteFile=testng-smoke.xml


### View Reports

bash
# Open in browser
open build/reports/extentreports/index.html

# Or navigate to
file:///path/to/build/reports/extentreports/index.html


---

## 13. CI/CD INTEGRATION

### GitHub Actions

yaml
- name: Run tests
  run: gradle test

- name: Upload test reports
  if: always()
  uses: actions/upload-artifact@v2
  with:
    name: test-reports
    path: build/reports/


### Jenkins

groovy
post {
    always {
        archiveArtifacts artifacts: 'build/reports/**'
        publishHTML([
            reportDir: 'build/reports/extentreports',
            reportFiles: 'index.html',
            reportName: 'ExtentReports'
        ])
    }
}


---

## 14. CUCUMBER REPORTS

### Cucumber HTML Report


build/reports/cucumber/
├── cucumber-report.html
├── cucumber-report.json
└── cucumber-report.xml


### Generate Cucumber Report

bash
gradle test -Dtest=CucumberRunner


### View Cucumber Report

bash
open build/reports/cucumber/cucumber-report.html


---

## 15. COMBINED REPORTS

### Execution produces

1. **ExtentReports**: build/reports/extentreports/index.html
   - TestNG test results
   - With screenshots
   - System information
   
2. **Cucumber Reports**: build/reports/cucumber/cucumber-report.html
   - BDD feature execution
   - Step-by-step details
   - Scenario summaries

3. **TestNG Reports**: build/reports/testng-results/
   - JUnit XML format
   - For CI/CD integration

---

## 16. SCREENSHOT MANAGEMENT

### Automatic Capture

java
@Override
public void onTestFailure(ITestResult result) {
    String screenshotPath = ScreenshotUtil.captureScreenshot(
        WebDriverManager.getPage(), 
        result.getMethod().getMethodName()
    );
    ScreenshotUtil.attachScreenshotToReport(screenshotPath);
}


### Manual Capture

java
// In test class
ScreenshotUtil.captureScreenshot(page, "my-screenshot");

// In step definition
ExtentTestListener.getTest()
    .addScreenCaptureFromPath(screenshotPath);


---

## 17. REPORT TROUBLESHOOTING

### Report Not Generated

**Solution**: Check testng.xml includes listeners

xml
<listeners>
    <listener class-name="org.example.listeners.ExtentTestListener"/>
</listeners>


### Screenshots Missing

**Solution**: Verify config.properties has:


screenshot.on.failure=true
screenshot.path=build/reports/screenshots


### Slow Report Loading

**Solution**: Reduce screenshot frequency or size

---

## 18. BEST PRACTICES

### DO:
- Log important test steps
- Attach screenshots to failed tests
- Use test descriptions
- Categorize tests with groups
- Review reports regularly
- Archive reports in CI/CD
- Include system information

### DON'T:
- Log every single action (too verbose)
- Capture screenshot for every action (slow)
- Forget to flush reports (incomplete data)
- Ignore report errors in CI/CD
- Store uncompressed reports (storage)

---

## 19. REPORT RETENTION

### Local Machine

Keep last 5 test runs
Archive older reports to dated folders


### CI/CD

Keep reports as artifacts
Archive for 90 days
Delete reports older than 90 days


---

## 20. MONITORING AND ANALYSIS

### Track Over Time
- Test pass rate trends
- Execution time trends
- Flaky test identification
- Test failure patterns
- Performance degradation

### Tools for Analysis
- ExtentReports Dashboard
- Cucumber Report Portal (optional)
- Custom analytics dashboard

---

**Status**: ✅ Reporting Specification - CORRECTED and SIMPLIFIED

All specifications now error-free and ready for implementation!
