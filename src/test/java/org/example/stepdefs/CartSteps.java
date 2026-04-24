package org.example.stepdefs;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class CartSteps {

    private final SharedContext ctx;

    public CartSteps(SharedContext ctx) {
        this.ctx = ctx;
    }

    @When("I navigate to the cart")
    public void iNavigateToTheCart() {
        ctx.productsPage.clickShoppingCart();
    }

    @Then("the cart should be empty")
    public void theCartShouldBeEmpty() {
        assertThat(ctx.cartPage.isCartPageDisplayed()).isTrue();
        assertThat(ctx.cartPage.getCartItemCount()).isEqualTo(0);
    }

    @Then("{string} should be in the cart")
    public void shouldBeInTheCart(String itemName) {
        assertThat(ctx.cartPage.isItemInCart(itemName)).isTrue();
    }

    @And("I click continue shopping")
    public void iClickContinueShopping() {
        ctx.cartPage.clickContinueShopping();
    }

    @Then("I should be on the cart page")
    public void iShouldBeOnTheCartPage() {
        assertThat(ctx.cartPage.isCartPageDisplayed()).isTrue();
    }

    @Then("the cart item count should be {int}")
    public void theCartItemCountShouldBe(int expected) {
        assertThat(ctx.cartPage.getCartItemCount()).isEqualTo(expected);
    }
}
