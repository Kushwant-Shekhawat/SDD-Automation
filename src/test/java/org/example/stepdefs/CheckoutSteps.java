package org.example.stepdefs;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckoutSteps {

    private final SharedContext ctx;

    public CheckoutSteps(SharedContext ctx) {
        this.ctx = ctx;
    }

    @Given("I have {string} in my cart")
    public void iHaveInMyCart(String productName) {
        ctx.productsPage.addProductToCart(productName);
    }

    @Given("I am on the cart page")
    public void iAmOnTheCartPage() {
        ctx.productsPage.clickShoppingCart();
    }

    @When("I click the checkout button")
    public void iClickTheCheckoutButton() {
        ctx.cartPage.clickCheckout();
    }

    @Then("I should be on checkout step one")
    public void iShouldBeOnCheckoutStepOne() {
        assertThat(ctx.checkoutPage.isCheckoutStepOneDisplayed()).isTrue();
    }

    @When("I enter first name {string}, last name {string}, and postal code {string}")
    public void iEnterShippingInfo(String firstName, String lastName, String postalCode) {
        ctx.checkoutPage.fillShippingInfo(firstName, lastName, postalCode);
    }

    @When("I click continue on checkout")
    public void iClickContinueOnCheckout() {
        ctx.checkoutPage.clickContinue();
    }

    @Then("I should be on checkout step two")
    public void iShouldBeOnCheckoutStepTwo() {
        assertThat(ctx.checkoutPage.isCheckoutStepTwoDisplayed()).isTrue();
    }

    @When("I click finish")
    public void iClickFinish() {
        ctx.checkoutPage.clickFinish();
    }

    @Then("I should see the order confirmation page")
    public void iShouldSeeTheOrderConfirmationPage() {
        assertThat(ctx.checkoutPage.isOrderConfirmationDisplayed()).isTrue();
    }

    @Then("the confirmation message should contain {string}")
    public void theConfirmationMessageShouldContain(String expectedText) {
        assertThat(ctx.checkoutPage.getOrderConfirmationText()).contains(expectedText);
    }

    @When("I click cancel on checkout")
    public void iClickCancelOnCheckout() {
        ctx.checkoutPage.clickCancel();
    }

    @And("I dismiss the checkout error")
    public void iDismissTheCheckoutError() {
        ctx.checkoutPage.dismissError();
    }

    @Then("the checkout error should not be visible")
    public void theCheckoutErrorShouldNotBeVisible() {
        assertThat(ctx.checkoutPage.isErrorVisible()).isFalse();
    }

    @Then("the order total should be greater than the item total")
    public void theOrderTotalShouldBeGreaterThanTheItemTotal() {
        double itemTotal = ctx.checkoutPage.parsePrice(ctx.checkoutPage.getItemTotal());
        double orderTotal = ctx.checkoutPage.parsePrice(ctx.checkoutPage.getOrderTotal());
        assertThat(orderTotal).isGreaterThan(itemTotal);
    }

    @Then("the item total should contain {string}")
    public void theItemTotalShouldContain(String expected) {
        assertThat(ctx.checkoutPage.getItemTotal()).contains(expected);
    }
}
