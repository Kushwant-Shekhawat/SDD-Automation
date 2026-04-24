package org.example.driver;

import com.microsoft.playwright.*;
import org.example.utils.ConfigReader;
import org.example.utils.LoggerUtil;

public class WebDriverManager {

    private static final ThreadLocal<Playwright> playwrightHolder = new ThreadLocal<>();
    private static final ThreadLocal<Browser> browserHolder = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> contextHolder = new ThreadLocal<>();
    private static final ThreadLocal<Page> pageHolder = new ThreadLocal<>();

    public static void initBrowser() {
        String browserType = ConfigReader.getBrowserType();
        boolean headless = ConfigReader.isHeadless();

        Playwright playwright = Playwright.create();
        playwrightHolder.set(playwright);

        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setSlowMo(ConfigReader.getInt("browser.slow.mo", 0));

        Browser browser;
        switch (browserType.toLowerCase()) {
            case "firefox":
                browser = playwright.firefox().launch(options);
                break;
            case "webkit":
                browser = playwright.webkit().launch(options);
                break;
            default:
                browser = playwright.chromium().launch(options);
        }
        browserHolder.set(browser);

        BrowserContext context = browser.newContext();
        contextHolder.set(context);

        Page page = context.newPage();
        pageHolder.set(page);

        LoggerUtil.info("Browser initialized: " + browserType + " headless=" + headless);
    }

    public static Page getPage() {
        return pageHolder.get();
    }

    public static void closeBrowser() {
        try {
            Page page = pageHolder.get();
            if (page != null) page.close();

            BrowserContext ctx = contextHolder.get();
            if (ctx != null) ctx.close();

            Browser browser = browserHolder.get();
            if (browser != null) browser.close();

            Playwright playwright = playwrightHolder.get();
            if (playwright != null) playwright.close();
        } finally {
            pageHolder.remove();
            contextHolder.remove();
            browserHolder.remove();
            playwrightHolder.remove();
        }
        LoggerUtil.info("Browser closed");
    }
}
