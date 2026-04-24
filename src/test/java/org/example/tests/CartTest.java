package org.example.tests;

import org.example.base.BaseTest;
import org.example.pages.CartPage;
import org.example.pages.LoginPage;
import org.example.pages.ProductsPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CartTest extends BaseTest {

    private ProductsPage productsPage;
    private CartPage cartPage;

    @BeforeMethod
    public void loginAndAddProducts() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login("standard_user", "secret_sauce");
        productsPage = new ProductsPage(page);
        cartPage = new CartPage(page);
    }

    @Test(description = "Cart is empty before adding items")
    public void testCartIsInitiallyEmpty() {
        productsPage.clickShoppingCart();
        assertThat(cartPage.isCartPageDisplayed()).isTrue();
        assertThat(cartPage.getCartItemCount()).isEqualTo(0);
    }

    @Test(description = "Added product appears in cart")
    public void testAddedProductInCart() {
        productsPage.addProductToCart("Sauce Labs Backpack");
        productsPage.clickShoppingCart();

        assertThat(cartPage.isCartPageDisplayed()).isTrue();
        assertThat(cartPage.isItemInCart("Sauce Labs Backpack")).isTrue();
    }

    @Test(description = "Multiple products can be added to cart")
    public void testMultipleProductsInCart() {
        productsPage.addProductToCart("Sauce Labs Backpack");
        productsPage.addProductToCart("Sauce Labs Bike Light");
        productsPage.clickShoppingCart();

        assertThat(cartPage.getCartItemCount()).isEqualTo(2);
    }

    @Test(description = "Removing item from cart decreases count")
    public void testRemoveItemFromCart() {
        productsPage.addProductToCart("Sauce Labs Backpack");
        productsPage.clickShoppingCart();
        cartPage.removeItem("Sauce Labs Backpack");

        assertThat(cartPage.getCartItemCount()).isEqualTo(0);
    }

    @Test(description = "Continue shopping returns to products page")
    public void testContinueShoppingFromCart() {
        productsPage.clickShoppingCart();
        cartPage.clickContinueShopping();

        assertThat(productsPage.isProductsPageDisplayed()).isTrue();
    }
}
