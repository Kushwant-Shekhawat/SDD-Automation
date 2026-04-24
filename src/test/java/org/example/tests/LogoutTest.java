package org.example.tests;

import org.example.base.BaseTest;
import org.example.pages.LoginPage;
import org.example.pages.NavigationComponent;
import org.example.pages.ProductsPage;
import org.example.utils.ConfigReader;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LogoutTest extends BaseTest {

    private LoginPage loginPage;
    private NavigationComponent nav;

    @BeforeMethod
    public void loginBeforeTest() {
        loginPage = new LoginPage(page);
        nav = new NavigationComponent(page);
        loginPage.login("standard_user", "secret_sauce");
    }

    @Test(description = "Successful logout returns to login page")
    public void testLogoutReturnsToLoginPage() {
        nav.logout();
        assertThat(loginPage.isLoginPageDisplayed()).isTrue();
    }

    @Test(description = "After logout direct URL access to inventory redirects to login")
    public void testAfterLogoutDirectUrlRedirects() {
        nav.logout();
        page.navigate(ConfigReader.getBaseUrl() + "/inventory.html");
        assertThat(loginPage.isLoginPageDisplayed()).isTrue();
    }

    @Test(description = "After logout back button does not restore session")
    public void testBackButtonAfterLogoutDoesNotRestoreSession() {
        nav.logout();
        page.goBack();
        assertThat(loginPage.isLoginPageDisplayed()).isTrue();
    }

    @Test(description = "Reset app state clears cart")
    public void testResetAppStateClearsCart() {
        ProductsPage productsPage = new ProductsPage(page);
        productsPage.addProductToCart("Sauce Labs Backpack");
        assertThat(productsPage.getCartItemCount()).isEqualTo(1);

        nav.resetAppState();
        nav.closeMenu();

        assertThat(productsPage.getCartItemCount()).isEqualTo(0);
    }

    @Test(description = "Navigation menu opens and closes correctly")
    public void testMenuOpenAndClose() {
        nav.openMenu();
        assertThat(nav.isMenuVisible()).isTrue();

        nav.closeMenu();
        assertThat(nav.isMenuVisible()).isFalse();
    }
}
