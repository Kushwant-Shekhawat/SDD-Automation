package org.example.stepdefs;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductSteps {

    private final SharedContext ctx;

    public ProductSteps(SharedContext ctx) {
        this.ctx = ctx;
    }

    @Given("I am logged in as {string} with password {string}")
    public void iAmLoggedInAs(String username, String password) {
        ctx.loginPage.login(username, password);
    }

    @Then("I should see {int} products on the page")
    public void iShouldSeeProductsOnThePage(int count) {
        assertThat(ctx.productsPage.getProductCount()).isEqualTo(count);
    }

    @When("I add {string} to the cart")
    public void iAddToTheCart(String productName) {
        ctx.productsPage.addProductToCart(productName);
    }

    @When("I remove {string} from the cart")
    public void iRemoveFromTheCart(String productName) {
        if (ctx.cartPage.isCartPageDisplayed()) {
            ctx.cartPage.removeItem(productName);
        } else {
            ctx.productsPage.removeProductFromCart(productName);
        }
    }

    @Then("the cart badge should show {string}")
    public void theCartBadgeShouldShow(String count) {
        assertThat(String.valueOf(ctx.productsPage.getCartItemCount())).isEqualTo(count);
    }

    @Then("the cart badge should not be visible")
    public void theCartBadgeShouldNotBeVisible() {
        assertThat(ctx.productsPage.getCartItemCount()).isEqualTo(0);
    }

    @When("I sort products by {string}")
    public void iSortProductsBy(String sortLabel) {
        String sortValue;
        switch (sortLabel) {
            case "Price (low to high)": sortValue = "lohi"; break;
            case "Price (high to low)": sortValue = "hilo"; break;
            case "Name (Z to A)": sortValue = "za"; break;
            default: sortValue = "az";
        }
        ctx.productsPage.selectSortOption(sortValue);
    }

    @Then("the first product price should be {string}")
    public void theFirstProductPriceShouldBe(String expectedPrice) {
        assertThat(ctx.productsPage.getProductPrices().get(0)).isEqualTo(expectedPrice);
    }

    @When("I click on product {string}")
    public void iClickOnProduct(String productName) {
        ctx.productsPage.clickProductName(productName);
    }

    @Then("I should be on the product details page")
    public void iShouldBeOnTheProductDetailsPage() {
        assertThat(ctx.productDetailsPage.isProductDetailsPageDisplayed()).isTrue();
    }

    @Then("the product name should be {string}")
    public void theProductNameShouldBe(String expectedName) {
        assertThat(ctx.productDetailsPage.getProductName()).isEqualTo(expectedName);
    }

    @Then("the first product name should be {string}")
    public void theFirstProductNameShouldBe(String expected) {
        assertThat(ctx.productsPage.getFirstProductName()).isEqualTo(expected);
    }

    @Then("the last product price should be {string}")
    public void theLastProductPriceShouldBe(String expected) {
        assertThat(ctx.productsPage.getLastProductPrice()).isEqualTo(expected);
    }

    @And("I click back to products")
    public void iClickBackToProducts() {
        ctx.productDetailsPage.clickBackToProducts();
    }

    @Then("the product price should be {string}")
    public void theProductPriceShouldBe(String expected) {
        assertThat(ctx.productDetailsPage.getProductPrice()).isEqualTo(expected);
    }

    @Then("the product description should not be empty")
    public void theProductDescriptionShouldNotBeEmpty() {
        assertThat(ctx.productDetailsPage.getProductDescription()).isNotEmpty();
    }

    @And("I click add to cart on the details page")
    public void iClickAddToCartOnTheDetailsPage() {
        ctx.productDetailsPage.addToCart();
    }

    @And("I click remove on the details page")
    public void iClickRemoveOnTheDetailsPage() {
        ctx.productDetailsPage.removeFromCart();
    }

    @Then("the remove button should be visible on details page")
    public void theRemoveButtonShouldBeVisibleOnDetailsPage() {
        assertThat(ctx.productDetailsPage.isRemoveButtonVisible()).isTrue();
    }

    @Then("the add to cart button should be visible on details page")
    public void theAddToCartButtonShouldBeVisibleOnDetailsPage() {
        assertThat(ctx.productDetailsPage.isAddToCartButtonVisible()).isTrue();
    }

    @Then("the product list should include {string}")
    public void theProductListShouldInclude(String productName) {
        assertThat(ctx.productsPage.getProductNames()).contains(productName);
    }

    @When("I add all products to the cart")
    public void iAddAllProductsToTheCart() {
        ctx.productsPage.addAllProductsToCart();
    }
}
