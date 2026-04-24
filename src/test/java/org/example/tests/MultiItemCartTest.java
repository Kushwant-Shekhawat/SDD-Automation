package org.example.tests;

import org.example.base.BaseTest;
import org.example.pages.CartPage;
import org.example.pages.CheckoutPage;
import org.example.pages.LoginPage;
import org.example.pages.ProductDetailsPage;
import org.example.pages.ProductsPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MultiItemCartTest extends BaseTest {

    private ProductsPage productsPage;
    private CartPage cartPage;

    @BeforeMethod
    public void loginBeforeTest() {
        new LoginPage(page).login("standard_user", "secret_sauce");
        productsPage = new ProductsPage(page);
        cartPage = new CartPage(page);
    }

    @Test(description = "Cart badge increments correctly with each product added")
    public void testCartBadgeIncrements() {
        productsPage.addProductToCart("Sauce Labs Backpack");
        assertThat(productsPage.getCartItemCount()).isEqualTo(1);

        productsPage.addProductToCart("Sauce Labs Bike Light");
        assertThat(productsPage.getCartItemCount()).isEqualTo(2);

        productsPage.addProductToCart("Sauce Labs Bolt T-Shirt");
        assertThat(productsPage.getCartItemCount()).isEqualTo(3);
    }

    @Test(description = "Cart page lists all added products")
    public void testCartPageListsAllAddedProducts() {
        productsPage.addProductToCart("Sauce Labs Backpack");
        productsPage.addProductToCart("Sauce Labs Bike Light");
        productsPage.clickShoppingCart();

        assertThat(cartPage.isItemInCart("Sauce Labs Backpack")).isTrue();
        assertThat(cartPage.isItemInCart("Sauce Labs Bike Light")).isTrue();
        assertThat(cartPage.getCartItemCount()).isEqualTo(2);
    }

    @Test(description = "Add all 6 products — cart badge shows 6")
    public void testAddAllProductsToCart() {
        for (String name : new String[]{
                "Sauce Labs Backpack", "Sauce Labs Bike Light", "Sauce Labs Bolt T-Shirt",
                "Sauce Labs Fleece Jacket", "Sauce Labs Onesie", "Test.allTheThings() T-Shirt (Red)"}) {
            productsPage.addProductToCart(name);
        }
        assertThat(productsPage.getCartItemCount()).isEqualTo(6);
    }

    @Test(description = "Remove one item from multi-item cart decreases count")
    public void testRemoveOneFromMultiItemCart() {
        productsPage.addProductToCart("Sauce Labs Backpack");
        productsPage.addProductToCart("Sauce Labs Bike Light");
        productsPage.addProductToCart("Sauce Labs Bolt T-Shirt");
        productsPage.clickShoppingCart();

        cartPage.removeItem("Sauce Labs Backpack");

        assertThat(cartPage.getCartItemCount()).isEqualTo(2);
        assertThat(cartPage.isItemInCart("Sauce Labs Bike Light")).isTrue();
    }

    @Test(description = "Checkout item total is correct for two items")
    public void testCheckoutTotalForTwoItems() {
        productsPage.addProductToCart("Sauce Labs Backpack");
        productsPage.addProductToCart("Sauce Labs Bike Light");
        productsPage.clickShoppingCart();

        cartPage.clickCheckout();
        CheckoutPage checkoutPage = new CheckoutPage(page);
        checkoutPage.fillShippingInfo("John", "Doe", "12345");
        checkoutPage.clickContinue();

        assertThat(checkoutPage.getItemTotal()).contains("39.98");
    }

    @Test(description = "Cart count persists after visiting product detail page and returning")
    public void testCartPersistsAcrossNavigation() {
        productsPage.addProductToCart("Sauce Labs Backpack");
        productsPage.addProductToCart("Sauce Labs Bike Light");

        productsPage.clickProductName("Sauce Labs Bolt T-Shirt");
        new ProductDetailsPage(page).clickBackToProducts();

        assertThat(productsPage.getCartItemCount()).isEqualTo(2);
    }
}
