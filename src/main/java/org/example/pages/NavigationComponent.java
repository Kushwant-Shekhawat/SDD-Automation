package org.example.pages;

import com.microsoft.playwright.Page;
import org.example.utils.ConfigReader;
import org.example.utils.LocatorStore;

public class NavigationComponent extends PlaywrightActions {

    public NavigationComponent(Page page) {
        super(page);
    }

    public void openMenu() {
        click(page.locator(LocatorStore.get("navigation", "menuButton")));
    }

    public void closeMenu() {
        click(page.locator(LocatorStore.get("navigation", "closeButton")));
    }

    public void logout() {
        openMenu();
        click(page.locator(LocatorStore.get("navigation", "logoutLink")));
    }

    public void clickAllItems() {
        openMenu();
        click(page.locator(LocatorStore.get("navigation", "allItemsLink")));
    }

    public void resetAppState() {
        openMenu();
        click(page.locator(LocatorStore.get("navigation", "resetLink")));
    }

    public boolean isMenuVisible() {
        String ariaHidden = page.locator(LocatorStore.get("navigation", "menuContainer"))
                .getAttribute("aria-hidden");
        return "false".equals(ariaHidden);
    }

    // Click logout link when menu is already open (does not call openMenu first)
    public void clickLogoutLink() {
        click(page.locator(LocatorStore.get("navigation", "logoutLink")));
    }

    // Click reset link when menu is already open, then wait for DOM to settle
    public void clickResetLink() {
        click(page.locator(LocatorStore.get("navigation", "resetLink")));
        // Wait for React to re-render the menu after state reset (critical on slow CI)
        page.locator(LocatorStore.get("navigation", "menuOpen"))
                .waitFor(new com.microsoft.playwright.Locator.WaitForOptions()
                        .setTimeout(ConfigReader.getTimeout()));
    }
}
