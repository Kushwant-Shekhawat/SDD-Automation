package org.example.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.example.utils.ConfigReader;
import org.example.utils.LoggerUtil;
import org.example.utils.WaitUtil;

import java.nio.file.Paths;
import java.util.List;

public abstract class PlaywrightActions {

    protected final Page page;
    protected final int timeout;

    protected PlaywrightActions(Page page) {
        this.page = page;
        this.timeout = ConfigReader.getTimeout();
    }

    // ─── Navigation ───────────────────────────────────────────────────────────

    protected void navigateTo(String path) {
        String url = ConfigReader.getBaseUrl() + path;
        page.navigate(url);
        LoggerUtil.debug("Navigated to: " + url);
    }

    protected void navigateToUrl(String fullUrl) {
        page.navigate(fullUrl);
        LoggerUtil.debug("Navigated to: " + fullUrl);
    }

    protected void goBack() {
        page.goBack();
    }

    protected void goForward() {
        page.goForward();
    }

    protected void reload() {
        page.reload();
    }

    protected String getCurrentUrl() {
        return page.url();
    }

    protected void waitForUrl(String urlPattern) {
        page.waitForURL(urlPattern);
    }

    // ─── Click ────────────────────────────────────────────────────────────────

    protected void click(Locator locator) {
        WaitUtil.waitForVisible(locator);
        locator.click();
    }

    protected void click(String selector) {
        click(page.locator(selector));
    }

    protected void doubleClick(Locator locator) {
        WaitUtil.waitForVisible(locator);
        locator.dblclick();
    }

    protected void rightClick(Locator locator) {
        WaitUtil.waitForVisible(locator);
        locator.click(new Locator.ClickOptions().setButton(
                com.microsoft.playwright.options.MouseButton.RIGHT));
    }

    protected void clickIfVisible(Locator locator) {
        if (locator.isVisible()) {
            locator.click();
        }
    }

    protected void clickByText(String text) {
        click(page.getByText(text).first());
    }

    // ─── Input ────────────────────────────────────────────────────────────────

    protected void fill(Locator locator, String text) {
        WaitUtil.waitForVisible(locator);
        locator.fill(text);
    }

    protected void fill(String selector, String text) {
        fill(page.locator(selector), text);
    }

    protected void type(Locator locator, String text) {
        WaitUtil.waitForVisible(locator);
        locator.pressSequentially(text);
    }

    protected void clear(Locator locator) {
        WaitUtil.waitForVisible(locator);
        locator.clear();
    }

    protected void pressKey(Locator locator, String key) {
        WaitUtil.waitForVisible(locator);
        locator.press(key);
    }

    protected void pressKey(String key) {
        page.keyboard().press(key);
    }

    protected void uploadFile(Locator locator, String filePath) {
        locator.setInputFiles(Paths.get(filePath));
    }

    // ─── Select / Checkbox ────────────────────────────────────────────────────

    protected void selectByValue(Locator locator, String value) {
        WaitUtil.waitForVisible(locator);
        locator.selectOption(value);
    }

    protected void selectByLabel(Locator locator, String label) {
        WaitUtil.waitForVisible(locator);
        locator.selectOption(new com.microsoft.playwright.options.SelectOption().setLabel(label));
    }

    protected void selectByIndex(Locator locator, int index) {
        WaitUtil.waitForVisible(locator);
        locator.selectOption(new com.microsoft.playwright.options.SelectOption().setIndex(index));
    }

    protected void check(Locator locator) {
        WaitUtil.waitForVisible(locator);
        locator.check();
    }

    protected void uncheck(Locator locator) {
        WaitUtil.waitForVisible(locator);
        locator.uncheck();
    }

    protected boolean isChecked(Locator locator) {
        return locator.isChecked();
    }

    // ─── Read / Query ─────────────────────────────────────────────────────────

    protected String getText(Locator locator) {
        WaitUtil.waitForVisible(locator);
        return locator.textContent().trim();
    }

    protected String getText(String selector) {
        return getText(page.locator(selector));
    }

    protected String getInputValue(Locator locator) {
        WaitUtil.waitForVisible(locator);
        return locator.inputValue();
    }

    protected String getAttribute(Locator locator, String attribute) {
        return locator.getAttribute(attribute);
    }

    protected List<String> getAllTexts(Locator locator) {
        return locator.allTextContents();
    }

    protected int getCount(Locator locator) {
        return locator.count();
    }

    protected String getInnerHtml(Locator locator) {
        return locator.innerHTML();
    }

    // ─── State Checks ─────────────────────────────────────────────────────────

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

    protected boolean isEnabled(Locator locator) {
        return locator.isEnabled();
    }

    protected boolean isEditable(Locator locator) {
        return locator.isEditable();
    }

    protected boolean isHidden(Locator locator) {
        return locator.isHidden();
    }

    protected boolean hasText(Locator locator, String text) {
        return locator.textContent().contains(text);
    }

    // ─── Wait ─────────────────────────────────────────────────────────────────

    protected void waitForVisible(Locator locator) {
        WaitUtil.waitForVisible(locator);
    }

    protected void waitForVisible(Locator locator, int timeoutMs) {
        WaitUtil.waitForVisible(locator, timeoutMs);
    }

    protected void waitForHidden(Locator locator) {
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.HIDDEN)
                .setTimeout(timeout));
    }

    protected void waitForEnabled(Locator locator) {
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(timeout));
    }

    protected void waitForPageLoad() {
        page.waitForLoadState(LoadState.LOAD);
    }

    protected void waitForNetworkIdle() {
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    protected void waitForSelector(String selector) {
        page.waitForSelector(selector);
    }

    // ─── Hover / Focus / Scroll ───────────────────────────────────────────────

    protected void hover(Locator locator) {
        WaitUtil.waitForVisible(locator);
        locator.hover();
    }

    protected void focus(Locator locator) {
        locator.focus();
    }

    protected void scrollIntoView(Locator locator) {
        locator.scrollIntoViewIfNeeded();
    }

    protected void scrollTo(int x, int y) {
        page.evaluate(String.format("window.scrollTo(%d, %d)", x, y));
    }

    // ─── Drag and Drop ────────────────────────────────────────────────────────

    protected void dragAndDrop(Locator source, Locator target) {
        source.dragTo(target);
    }

    // ─── Dialog Handling ──────────────────────────────────────────────────────

    protected void acceptDialog() {
        page.onDialog(dialog -> dialog.accept());
    }

    protected void dismissDialog() {
        page.onDialog(dialog -> dialog.dismiss());
    }

    // ─── Screenshot ───────────────────────────────────────────────────────────

    protected void takeScreenshot(String fileName) {
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get(fileName))
                .setFullPage(true));
        LoggerUtil.debug("Screenshot saved: " + fileName);
    }
}
