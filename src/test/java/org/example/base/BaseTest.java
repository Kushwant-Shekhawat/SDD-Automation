package org.example.base;

import com.microsoft.playwright.Page;
import org.example.driver.WebDriverManager;
import org.example.utils.ConfigReader;
import org.example.utils.LoggerUtil;
import org.example.utils.ScreenshotUtil;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.lang.reflect.Method;

public class BaseTest {

    protected Page page;

    @Parameters("browser")
    @BeforeTest
    public void setBrowser(@Optional("chromium") String browser) {
        System.setProperty("browser.type", browser);
    }

    @BeforeMethod
    public void setUp(Method method) {
        LoggerUtil.info("Starting test: " + method.getName());
        WebDriverManager.initBrowser();
        page = WebDriverManager.getPage();
        page.navigate(ConfigReader.getBaseUrl());
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            if (ConfigReader.getBoolean("screenshot.on.failure", true)) {
                ScreenshotUtil.captureScreenshot(page, result.getName());
            }
            LoggerUtil.error("Test FAILED: " + result.getName());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            LoggerUtil.info("Test PASSED: " + result.getName());
        } else {
            LoggerUtil.warn("Test SKIPPED: " + result.getName());
        }
        WebDriverManager.closeBrowser();
    }
}
