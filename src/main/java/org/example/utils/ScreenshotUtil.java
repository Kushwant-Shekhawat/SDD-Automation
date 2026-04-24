package org.example.utils;

import com.microsoft.playwright.Page;

import java.io.File;
import java.nio.file.Paths;

public class ScreenshotUtil {

    public static String captureScreenshot(Page page, String testName) {
        if (page == null) return null;
        try {
            String dir = ConfigReader.getConfig("screenshot.path", "build/reports/screenshots");
            new File(dir).mkdirs();
            String fileName = testName + "_" + System.currentTimeMillis() + ".png";
            String fullPath = dir + "/" + fileName;
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(fullPath)));
            LoggerUtil.info("Screenshot saved: " + fullPath);
            return fullPath;
        } catch (Exception e) {
            LoggerUtil.error("Screenshot failed for: " + testName, e);
            return null;
        }
    }
}
