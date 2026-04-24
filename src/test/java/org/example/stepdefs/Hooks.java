package org.example.stepdefs;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.example.listeners.ExtentTestListener;
import org.example.utils.ConfigReader;
import org.example.utils.LoggerUtil;
import org.example.utils.ScreenshotUtil;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Hooks {

    private static final Path REPORT_DIR = Paths.get(
            ConfigReader.getConfig("extent.report.path", "build/reports/extent/ExtentReport.html"))
            .toAbsolutePath().getParent();

    private final SharedContext ctx;

    public Hooks(SharedContext ctx) {
        this.ctx = ctx;
    }

    private static String toReportRelative(String absolutePath) {
        try {
            return REPORT_DIR.relativize(Paths.get(absolutePath).toAbsolutePath()).toString();
        } catch (Exception e) {
            return absolutePath;
        }
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        ScreenshotUtil.initScenarioFolder(scenario.getName());
    }

    @AfterStep
    public void afterStep(Scenario scenario) {
        String path = ScreenshotUtil.captureStepScreenshot(ctx.page);
        if (path == null) return;
        ExtentTest test = ExtentTestListener.getCurrentTest();
        if (test == null) return;
        try {
            int step = ScreenshotUtil.getStepCount();
            test.info("Step " + String.format("%02d", step),
                    MediaEntityBuilder.createScreenCaptureFromPath(toReportRelative(path)).build());
        } catch (Exception e) {
            LoggerUtil.warn("Could not attach step screenshot to report: " + e.getMessage());
        }
    }

    @After
    public void afterScenario(Scenario scenario) {
        if (scenario.isFailed()) {
            String path = ScreenshotUtil.captureFailureScreenshot(ctx.page, scenario.getName());
            ExtentTest test = ExtentTestListener.getCurrentTest();
            if (test != null && path != null) {
                try {
                    test.fail("Failed at this state",
                            MediaEntityBuilder.createScreenCaptureFromPath(toReportRelative(path)).build());
                } catch (Exception e) {
                    LoggerUtil.warn("Could not attach failure screenshot to report: " + e.getMessage());
                }
            }
        }
        ScreenshotUtil.cleanupScenario();
        ctx.tearDown();
    }
}
