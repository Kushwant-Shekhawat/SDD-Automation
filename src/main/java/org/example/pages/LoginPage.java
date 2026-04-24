package org.example.pages;

import com.microsoft.playwright.Page;
import org.example.utils.LocatorStore;

public class LoginPage extends PlaywrightActions {

    public LoginPage(Page page) {
        super(page);
    }

    public void navigateToLogin() {
        navigateTo("/");
    }

    public void enterUsername(String username) {
        fill(page.locator(LocatorStore.get("login", "username")), username);
    }

    public void enterPassword(String password) {
        fill(page.locator(LocatorStore.get("login", "password")), password);
    }

    public void clickLoginButton() {
        click(page.locator(LocatorStore.get("login", "loginButton")));
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    public String getErrorMessage() {
        return getText(page.locator(LocatorStore.get("login", "errorMessage")));
    }

    public boolean isErrorDisplayed() {
        return isVisible(page.locator(LocatorStore.get("login", "errorMessage")), 3000);
    }

    public boolean isLoginPageDisplayed() {
        return isVisible(page.locator(LocatorStore.get("login", "loginButton")), timeout);
    }
}
