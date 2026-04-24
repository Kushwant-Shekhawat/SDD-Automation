package org.example.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.example.utils.ConfigReader;
import org.example.utils.LoggerUtil;
import org.example.utils.WaitUtil;

public abstract class BasePage {

    protected final Page page;
    protected final int timeout;

    protected BasePage(Page page) {
        this.page = page;
        this.timeout = ConfigReader.getTimeout();
    }

    protected void navigateTo(String path) {
        String url = ConfigReader.getBaseUrl() + path;
        page.navigate(url);
        LoggerUtil.debug("Navigated to: " + url);
    }

    protected void click(Locator locator) {
        WaitUtil.waitForVisible(locator);
        locator.click();
    }

    protected void fill(Locator locator, String text) {
        WaitUtil.waitForVisible(locator);
        locator.fill(text);
    }

    protected String getText(Locator locator) {
        WaitUtil.waitForVisible(locator);
        return locator.textContent().trim();
    }

    protected boolean isVisible(Locator locator) {
        return locator.isVisible();
    }

    protected boolean isVisible(Locator locator, int timeoutMs) {
        try {
            WaitUtil.waitForVisible(locator, timeoutMs);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected void waitForPageLoad() {
        page.waitForLoadState();
    }
}
