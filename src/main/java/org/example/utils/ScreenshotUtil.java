package org.example.utils;

import com.microsoft.playwright.Page;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class ScreenshotUtil {

    private static volatile String runFolder;
    private static final ThreadLocal<String> scenarioFolder = new ThreadLocal<>();
    private static final ThreadLocal<AtomicInteger> stepCounter = new ThreadLocal<>();
    private static final ThreadLocal<String> lastFailurePath = new ThreadLocal<>();

    public static synchronized String getRunFolder() {
        if (runFolder == null) {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            runFolder = "build/reports/screenshots/" + timestamp;
            new File(runFolder).mkdirs();
        }
        return runFolder;
    }

    public static void initScenarioFolder(String scenarioName) {
        String safeName = sanitize(scenarioName);
        String path = getRunFolder() + "/" + safeName;
        new File(path).mkdirs();
        scenarioFolder.set(path);
        stepCounter.set(new AtomicInteger(0));
        lastFailurePath.remove();
    }

    public static String captureStepScreenshot(Page page) {
        String folder = scenarioFolder.get();
        if (page == null || folder == null) return null;
        try {
            int n = stepCounter.get().incrementAndGet();
            Path path = Paths.get(folder, String.format("step_%02d.png", n));
            page.screenshot(new Page.ScreenshotOptions().setPath(path).setFullPage(true));
            return path.toAbsolutePath().toString();
        } catch (Exception e) {
            LoggerUtil.warn("Step screenshot failed: " + e.getMessage());
            return null;
        }
    }

    public static String captureFailureScreenshot(Page page, String scenarioName) {
        String folder = scenarioFolder.get();
        if (page == null || folder == null) return null;
        try {
            String safeName = sanitize(scenarioName);
            Path path = Paths.get(folder, "Failed_Step_" + safeName + ".png");
            page.screenshot(new Page.ScreenshotOptions().setPath(path).setFullPage(true));
            lastFailurePath.set(path.toString());
            LoggerUtil.info("Failure screenshot saved: " + path);
            return path.toString();
        } catch (Exception e) {
            LoggerUtil.warn("Failure screenshot failed for: " + scenarioName + " — " + e.getMessage());
            return null;
        }
    }

    public static String getLastFailurePath() {
        return lastFailurePath.get();
    }

    public static int getStepCount() {
        AtomicInteger counter = stepCounter.get();
        return counter != null ? counter.get() : 0;
    }

    public static void cleanupScenario() {
        scenarioFolder.remove();
        stepCounter.remove();
        lastFailurePath.remove();
    }

    private static String sanitize(String name) {
        String safe = name.replaceAll("[^a-zA-Z0-9_-]", "_").replaceAll("_+", "_");
        return safe.length() > 80 ? safe.substring(0, 80) : safe;
    }
}
