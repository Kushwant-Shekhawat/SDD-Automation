package org.example.pages;

import com.microsoft.playwright.Page;

public class LoginPage extends BasePage {

    public LoginPage(Page page) {
        super(page);
    }

    public void navigateToLogin() {
        navigateTo("/");
    }

    public void enterUsername(String username) {
        fill(page.locator("[data-test='username']"), username);
    }

    public void enterPassword(String password) {
        fill(page.locator("[data-test='password']"), password);
    }

    public void clickLoginButton() {
        click(page.locator("[data-test='login-button']"));
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    public String getErrorMessage() {
        return getText(page.locator("[data-test='error']"));
    }

    public boolean isErrorDisplayed() {
        return isVisible(page.locator("[data-test='error']"), 3000);
    }

    public boolean isLoginPageDisplayed() {
        return isVisible(page.locator("[data-test='login-button']"), timeout);
    }
}
