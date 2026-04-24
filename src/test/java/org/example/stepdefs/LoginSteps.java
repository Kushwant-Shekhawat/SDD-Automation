package org.example.stepdefs;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginSteps {

    private final SharedContext ctx;

    public LoginSteps(SharedContext ctx) {
        this.ctx = ctx;
    }

    @Given("I am on the login page")
    public void iAmOnTheLoginPage() {
        ctx.loginPage.navigateToLogin();
    }

    @When("I enter username {string} and password {string}")
    public void iEnterUsernameAndPassword(String username, String password) {
        ctx.loginPage.enterUsername(username);
        ctx.loginPage.enterPassword(password);
    }

    @And("I click the login button")
    public void iClickTheLoginButton() {
        ctx.loginPage.clickLoginButton();
    }

    @Then("I should be on the products page")
    public void iShouldBeOnTheProductsPage() {
        assertThat(ctx.productsPage.isProductsPageDisplayed())
                .as("Expected to be on the products page")
                .isTrue();
    }

    @Then("I should see an error message containing {string}")
    public void iShouldSeeAnErrorMessageContaining(String expectedMessage) {
        boolean errorVisible = ctx.loginPage.isErrorDisplayed()
                || ctx.checkoutPage.isErrorDisplayed();
        assertThat(errorVisible).as("Error message should be visible").isTrue();

        String actualMessage = ctx.loginPage.isErrorDisplayed()
                ? ctx.loginPage.getErrorMessage()
                : ctx.checkoutPage.getErrorMessage();
        assertThat(actualMessage).contains(expectedMessage);
    }
}
