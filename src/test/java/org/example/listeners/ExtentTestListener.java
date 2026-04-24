package org.example.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.example.utils.ConfigReader;
import org.example.utils.LoggerUtil;
import org.example.utils.ScreenshotUtil;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ExtentTestListener implements ITestListener {

    private static volatile ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    @Override
    public synchronized void onStart(ITestContext context) {
        if (extentReports == null) {
            String reportPath = ConfigReader.getConfig("extent.report.path",
                    "build/reports/extent/ExtentReport.html");
            String reportTitle = ConfigReader.getConfig("extent.report.title",
                    "SauceDemo Automation Report");
            String reportName = ConfigReader.getConfig("extent.report.name",
                    "SDD Automation Suite");

            new java.io.File(reportPath).getParentFile().mkdirs();

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setDocumentTitle(reportTitle);
            sparkReporter.config().setReportName(reportName);

            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            extentReports.setSystemInfo("Environment", ConfigReader.getConfig("ENV", "local"));
            extentReports.setSystemInfo("Browser", ConfigReader.getBrowserType());
            extentReports.setSystemInfo("Base URL", ConfigReader.getBaseUrl());
        }
    }

    @Override
    public synchronized void onTestStart(ITestResult result) {
        String testName = result.getMethod().getDescription();
        if (testName == null || testName.isEmpty()) {
            testName = result.getName();
        }
        ExtentTest test = extentReports.createTest(testName);
        extentTest.set(test);
        LoggerUtil.info("ExtentReport: started test - " + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        extentTest.get().pass("Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        extentTest.get().fail(result.getThrowable());
        String screenshotPath = ScreenshotUtil.getLastFailurePath();
        if (screenshotPath != null) {
            try {
                extentTest.get().addScreenCaptureFromPath(screenshotPath, "Failure Screenshot");
            } catch (Exception e) {
                LoggerUtil.warn("Could not attach screenshot to report: " + e.getMessage());
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        extentTest.get().skip(result.getThrowable() != null
                ? result.getThrowable().getMessage() : "Test skipped");
    }

    @Override
    public synchronized void onFinish(ITestContext context) {
        if (extentReports != null) {
            extentReports.flush();
        }
    }
}
