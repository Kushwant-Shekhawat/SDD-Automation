package org.example.pages;

import com.microsoft.playwright.Page;

public class NavigationComponent extends BasePage {

    public NavigationComponent(Page page) {
        super(page);
    }

    public void openMenu() {
        click(page.locator("#react-burger-menu-btn"));
    }

    public void closeMenu() {
        click(page.locator("#react-burger-cross-btn"));
    }

    public void logout() {
        openMenu();
        click(page.locator("#logout_sidebar_link"));
    }

    public void clickAllItems() {
        openMenu();
        click(page.locator("#inventory_sidebar_link"));
    }

    public void resetAppState() {
        openMenu();
        click(page.locator("#reset_sidebar_link"));
    }

    public boolean isMenuVisible() {
        String ariaHidden = page.locator(".bm-menu-wrap").getAttribute("aria-hidden");
        return "false".equals(ariaHidden);
    }
}
