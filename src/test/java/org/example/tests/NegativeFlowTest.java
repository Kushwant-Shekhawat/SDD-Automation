package org.example.tests;

import org.example.base.BaseTest;
import org.example.pages.LoginPage;
import org.example.pages.NavigationComponent;
import org.example.utils.ConfigReader;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NegativeFlowTest extends BaseTest {

    @DataProvider(name = "protectedUrls")
    public Object[][] protectedUrls() {
        return new Object[][]{
                {"/inventory.html"},
                {"/inventory-item.html?id=4"},
                {"/cart.html"},
                {"/checkout-step-one.html"},
                {"/checkout-step-two.html"},
                {"/checkout-complete.html"}
        };
    }

    @Test(description = "Direct URL access without login redirects to login page",
          dataProvider = "protectedUrls")
    public void testDirectUrlAccessRedirects(String path) {
        page.navigate(ConfigReader.getBaseUrl() + path);
        LoginPage loginPage = new LoginPage(page);
        assertThat(loginPage.isLoginPageDisplayed())
                .as("Expected login page redirect for path: " + path)
                .isTrue();
    }

    @Test(description = "After logout back button does not restore session")
    public void testBackButtonAfterLogoutDoesNotRestoreSession() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login("standard_user", "secret_sauce");

        NavigationComponent nav = new NavigationComponent(page);
        nav.logout();
        assertThat(loginPage.isLoginPageDisplayed()).isTrue();

        page.goBack();
        assertThat(loginPage.isLoginPageDisplayed())
                .as("Back button should not restore authenticated session")
                .isTrue();
    }

    @Test(description = "SQL injection attempt in login is rejected")
    public void testSqlInjectionRejected() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login("' OR '1'='1", "' OR '1'='1");
        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage())
                .contains("Username and password do not match");
    }

    @Test(description = "XSS payload in username field does not execute")
    public void testXssPayloadRejected() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login("<script>alert('xss')</script>", "secret_sauce");
        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(page.url()).contains("saucedemo.com");
    }

    @Test(description = "Extremely long username shows error gracefully")
    public void testLongUsernameHandledGracefully() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login("a".repeat(500), "secret_sauce");
        assertThat(loginPage.isErrorDisplayed()).isTrue();
    }
}
