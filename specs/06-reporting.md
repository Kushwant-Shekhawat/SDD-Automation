# SDD-Automation Framework - Reporting Specification

## 1. REPORTING OVERVIEW

Each test run produces two HTML reports:

| Report | Tool | Output Path |
|--------|------|-------------|
| Extent Report | ExtentReports 5.0.9 (custom `ITestListener`) | `build/reports/extent/ExtentReport.html` |
| Cucumber Report | Masterthought `cucumber-reporting:5.7.5` | `build/reports/cucumber/html/cucumber-html-reports/overview-features.html` |

Plus intermediate outputs:
- **Cucumber JSON**: `build/reports/cucumber/cucumber-report.json` — source for Masterthought
- **Screenshots**: `build/reports/screenshots/<timestamp>/<scenario>/step_NN.png`
- **Visual diffs**: `build/reports/visual/diffs/` (only when `@visual` runs)

---

## 2. EXTENT REPORT

### How it works
`ExtentTestListener` implements TestNG's `ITestListener`. It is registered in all `testng.xml` files via `<listeners>`. Since all scenarios run through `CucumberRunner`, each scenario maps to one `ExtentTest`.

### Screenshot attachment
Screenshots are **not** captured inside `ExtentTestListener` itself. They are attached in `Hooks.java`:
- `@AfterStep` — captures a step screenshot and attaches it to the current `ExtentTest` via `MediaEntityBuilder.createScreenCaptureFromPath(relativePath)`
- `@After` — captures failure screenshot if scenario failed and attaches to `ExtentTest`

Paths must be **relative from the report HTML file location** (`build/reports/extent/`). `Hooks.java` computes this with:
```java
REPORT_DIR.relativize(Paths.get(absolutePath).toAbsolutePath())
```

### Key methods in `ExtentTestListener.java`

```java
public static ExtentTest getCurrentTest()   // thread-safe via ThreadLocal
```

### `onTestFailure`
```java
@Override
public void onTestFailure(ITestResult result) {
    extentTest.get().fail(result.getThrowable());
    // Screenshot already attached by Hooks.java @After
}
```

### Config keys used
```properties
extent.report.path=build/reports/extent/ExtentReport.html
extent.report.title=SauceDemo Automation Report
extent.report.name=SDD Automation Suite
```

### Gradle dependency
```groovy
testImplementation 'com.aventstack:extentreports:5.0.9'
```
No adapter library — `ExtentTestListener` is a hand-written `ITestListener`.

---

## 3. CUCUMBER REPORT (MASTERTHOUGHT)

### How it works
`CucumberRunner` writes a JSON report during the test run:
```java
plugin = { "pretty", "json:build/reports/cucumber/cucumber-report.json" }
```
After the `test` task completes, the Gradle task `generateCucumberReport` processes the JSON and generates a full HTML report.

The built-in Cucumber `html:` plugin is **not used** — it generates JavaScript that triggers browser XSS security dialogs and produces inflated (~76 MB) HTML files.

### Gradle task
```groovy
buildscript {
    dependencies {
        classpath('net.masterthought:cucumber-reporting:5.7.5') {
            exclude group: 'com.fasterxml.jackson.core'
        }
        classpath 'com.fasterxml.jackson.core:jackson-databind:2.15.2'
    }
}

task generateCucumberReport {
    doLast {
        def jsonFile = file('build/reports/cucumber/cucumber-report.json')
        if (!jsonFile.exists()) return
        def outputDir = file('build/reports/cucumber/html')
        outputDir.mkdirs()
        def config = new net.masterthought.cucumber.Configuration(outputDir, 'SDD Automation Suite')
        config.setBuildNumber('1')
        new net.masterthought.cucumber.ReportBuilder([jsonFile.absolutePath], config)
                .generateReports()
    }
}

test { finalizedBy 'generateCucumberReport' }
```

### Screenshot embedding
`Hooks.java @AfterStep` attaches step screenshots via `scenario.attach(byte[], "image/png", name)`. Masterthought saves these as external files in `build/reports/cucumber/html/cucumber-html-reports/embeddings/` — keeps individual HTML pages small while still showing all screenshots.

### Why Masterthought is in `buildscript` (not `testImplementation`)
Masterthought is used by the Gradle build script itself (in `doLast`), not by test code. Using `testImplementation` would make it unavailable at Gradle script evaluation time. Jackson exclusion is needed because Masterthought pulls `jackson-core:2.17.0` which conflicts with the project's `jackson-databind:2.15.2`.

---

## 4. SCREENSHOT STRATEGY

### Classes involved
- `ScreenshotUtil.java` — captures and saves PNG files, manages run/scenario/step folder structure
- `Hooks.java` — calls `ScreenshotUtil`, attaches results to both reports

### Folder structure per run
```
build/reports/screenshots/
└── 2026-04-24_14-30-00/          ← run timestamp folder (one per JVM)
    └── Valid_login_standard_user/ ← scenario folder (sanitized name)
        ├── step_01.png
        ├── step_02.png
        ├── step_03.png
        └── Failed_Step_Valid_login....png   ← only on failure
```

### Flow
1. `Hooks @Before` → `ScreenshotUtil.initScenarioFolder(scenario.getName())`
2. `Hooks @AfterStep` → `ScreenshotUtil.captureStepScreenshot(page)` → attach to Extent + Cucumber
3. `Hooks @After` (if failed) → `ScreenshotUtil.captureFailureScreenshot(page, name)` → attach to both
4. `Hooks @After` → `ScreenshotUtil.cleanupScenario()` (removes ThreadLocals)

---

## 5. OPENING REPORTS

```bash
# Extent Report
open build/reports/extent/ExtentReport.html

# Cucumber Masterthought Report
open build/reports/cucumber/html/cucumber-html-reports/overview-features.html
```

---

## 6. CI/CD

GitHub Actions uploads the entire `build/reports/` directory as an artifact after each run (see `.github/workflows/ci.yml`). Reports are accessible from the Actions run page.

---

## 7. TROUBLESHOOTING

| Symptom | Cause | Fix |
|---------|-------|-----|
| Images broken in Extent report | Absolute path used in `createScreenCaptureFromPath` | Compute relative path from `REPORT_DIR` using `relativize()` |
| Cucumber HTML won't open / XSS error | Built-in `html:` plugin generates unsafe JS | Use Masterthought; remove `html:` from `CucumberOptions.plugin` |
| Cucumber HTML is 76 MB | Base64 screenshots inlined by `html:` plugin | Use Masterthought; embeddings are saved as external files |
| `generateCucumberReport` fails with Jackson conflict | Masterthought pulls `jackson-core:2.17.0` | Exclude `com.fasterxml.jackson.core` from Masterthought classpath, re-add `jackson-databind:2.15.2` explicitly |
| No screenshots in Cucumber report | `scenario.attach()` not called | `Hooks @AfterStep` and `@After` call `attachToCucumber()` |

---

**Status**: ✅ Reporting Specification — accurate as of 2026-04-24
