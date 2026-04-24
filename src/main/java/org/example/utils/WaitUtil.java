package org.example.utils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public class WaitUtil {

    public static void waitForVisible(Locator locator, int timeoutMs) {
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(timeoutMs));
    }

    public static void waitForVisible(Locator locator) {
        waitForVisible(locator, ConfigReader.getTimeout());
    }

    public static void waitForHidden(Locator locator, int timeoutMs) {
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.HIDDEN)
                .setTimeout(timeoutMs));
    }

    public static void waitForUrl(Page page, String urlPattern, int timeoutMs) {
        page.waitForURL(urlPattern, new Page.WaitForURLOptions().setTimeout(timeoutMs));
    }

    public static void waitForUrl(Page page, String urlPattern) {
        waitForUrl(page, urlPattern, ConfigReader.getTimeout());
    }
}
