package org.example.stepdefs;

import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.example.utils.ScreenshotUtil;

public class Hooks {

    private final SharedContext ctx;

    public Hooks(SharedContext ctx) {
        this.ctx = ctx;
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        ScreenshotUtil.initScenarioFolder(scenario.getName());
    }

    @AfterStep
    public void afterStep() {
        ScreenshotUtil.captureStepScreenshot(ctx.page);
    }

    @After
    public void afterScenario(Scenario scenario) {
        if (scenario.isFailed()) {
            ScreenshotUtil.captureFailureScreenshot(ctx.page, scenario.getName());
        }
        ScreenshotUtil.cleanupScenario();
        ctx.tearDown();
    }
}
