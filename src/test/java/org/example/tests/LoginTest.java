package org.example.tests;

import org.example.base.BaseTest;
import org.example.pages.LoginPage;
import org.example.pages.ProductsPage;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginTest extends BaseTest {

    @Test(description = "Valid login with standard user navigates to products page")
    public void testValidLoginStandardUser() {
        LoginPage loginPage = new LoginPage(page);
        ProductsPage productsPage = new ProductsPage(page);

        loginPage.login("standard_user", "secret_sauce");

        assertThat(productsPage.isProductsPageDisplayed())
                .as("Products page should be displayed after login")
                .isTrue();
    }

    @Test(description = "Locked out user cannot login")
    public void testLockedOutUser() {
        LoginPage loginPage = new LoginPage(page);

        loginPage.login("locked_out_user", "secret_sauce");

        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage())
                .contains("Sorry, this user has been locked out");
    }

    @Test(description = "Invalid password shows error message")
    public void testInvalidPassword() {
        LoginPage loginPage = new LoginPage(page);

        loginPage.login("standard_user", "wrong_password");

        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage())
                .contains("Username and password do not match");
    }

    @Test(description = "Empty username shows required field error")
    public void testEmptyUsername() {
        LoginPage loginPage = new LoginPage(page);

        loginPage.login("", "secret_sauce");

        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage()).contains("Username is required");
    }

    @Test(description = "Empty password shows required field error")
    public void testEmptyPassword() {
        LoginPage loginPage = new LoginPage(page);

        loginPage.login("standard_user", "");

        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage()).contains("Password is required");
    }

    @Test(description = "Login page is accessible via URL")
    public void testLoginPageAccessible() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.navigateToLogin();

        assertThat(loginPage.isLoginPageDisplayed()).isTrue();
    }

    @Test(description = "Performance glitch user can login successfully")
    public void testPerformanceGlitchUserLogin() {
        LoginPage loginPage = new LoginPage(page);
        ProductsPage productsPage = new ProductsPage(page);

        loginPage.login("performance_glitch_user", "secret_sauce");

        assertThat(productsPage.isProductsPageDisplayed())
                .as("Products page should display for performance_glitch_user")
                .isTrue();
    }
}
