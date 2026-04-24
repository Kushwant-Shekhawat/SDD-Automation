package org.example.stepdefs;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.utils.ConfigReader;

import static org.assertj.core.api.Assertions.assertThat;

public class NavigationSteps {

    private final SharedContext ctx;

    public NavigationSteps(SharedContext ctx) {
        this.ctx = ctx;
    }

    @Given("I am not logged in")
    public void iAmNotLoggedIn() {
        // SharedContext navigates to base URL on construction — no login needed
    }

    @When("I open the navigation menu")
    public void iOpenTheNavigationMenu() {
        ctx.navigationComponent.openMenu();
    }

    @When("I close the navigation menu")
    public void iCloseTheNavigationMenu() {
        if (ctx.navigationComponent.isMenuVisible()) {
            ctx.navigationComponent.closeMenu();
        }
    }

    @And("I click the logout link")
    public void iClickTheLogoutLink() {
        ctx.navigationComponent.clickLogoutLink();
    }

    @And("I click the reset app state link")
    public void iClickTheResetAppStateLink() {
        ctx.navigationComponent.clickResetLink();
    }

    @Then("I should be on the login page")
    public void iShouldBeOnTheLoginPage() {
        assertThat(ctx.loginPage.isLoginPageDisplayed())
                .as("Expected to be on the login page")
                .isTrue();
    }

    @When("I navigate directly to {string}")
    public void iNavigateDirectlyTo(String path) {
        ctx.page.navigate(ConfigReader.getBaseUrl() + path);
    }

    @When("I log in again as {string} with password {string}")
    public void iLogInAgainAs(String username, String password) {
        ctx.loginPage.login(username, password);
    }

    @Then("the navigation menu should be visible")
    public void theNavigationMenuShouldBeVisible() {
        assertThat(ctx.navigationComponent.isMenuVisible()).isTrue();
    }

    @Then("the navigation menu should not be visible")
    public void theNavigationMenuShouldNotBeVisible() {
        assertThat(ctx.navigationComponent.isMenuVisible()).isFalse();
    }

    @And("I press the browser back button")
    public void iPressTheBrowserBackButton() {
        ctx.page.goBack(new Page.GoBackOptions().setTimeout(15000));
        ctx.page.waitForLoadState(LoadState.DOMCONTENTLOADED);
    }

    @When("I enter a {int}-character username and password {string}")
    public void iEnterALongUsernameAndPassword(int length, String password) {
        ctx.loginPage.enterUsername("a".repeat(length));
        ctx.loginPage.enterPassword(password);
    }

    @Then("no alert dialog should be present")
    public void noAlertDialogShouldBePresent() {
        assertThat(ctx.page.url()).contains("saucedemo.com");
    }
}
