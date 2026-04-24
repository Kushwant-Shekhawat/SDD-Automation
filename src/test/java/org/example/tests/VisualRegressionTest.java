package org.example.tests;

import org.example.base.BaseTest;
import org.example.pages.CartPage;
import org.example.pages.CheckoutPage;
import org.example.pages.LoginPage;
import org.example.pages.ProductDetailsPage;
import org.example.pages.ProductsPage;
import org.example.utils.VisualCompareUtil;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VisualRegressionTest extends BaseTest {

    @Test(description = "Login page visual regression")
    public void testLoginPageVisual() {
        assertThat(VisualCompareUtil.compareScreenshot(page, "login_page.png"))
                .as("Login page visual diff exceeded threshold")
                .isTrue();
    }

    @Test(description = "Products page visual regression")
    public void testProductsPageVisual() {
        new LoginPage(page).login("standard_user", "secret_sauce");
        assertThat(VisualCompareUtil.compareScreenshot(page, "products_page.png"))
                .as("Products page visual diff exceeded threshold")
                .isTrue();
    }

    @Test(description = "Product details page visual regression")
    public void testProductDetailsPageVisual() {
        new LoginPage(page).login("standard_user", "secret_sauce");
        new ProductsPage(page).clickProductName("Sauce Labs Backpack");
        new ProductDetailsPage(page).isProductDetailsPageDisplayed();
        assertThat(VisualCompareUtil.compareScreenshot(page, "product_details_backpack.png"))
                .as("Product details page visual diff exceeded threshold")
                .isTrue();
    }

    @Test(description = "Cart page with one item visual regression")
    public void testCartPageVisual() {
        new LoginPage(page).login("standard_user", "secret_sauce");
        ProductsPage productsPage = new ProductsPage(page);
        productsPage.addProductToCart("Sauce Labs Backpack");
        productsPage.clickShoppingCart();
        assertThat(VisualCompareUtil.compareScreenshot(page, "cart_one_item.png"))
                .as("Cart page visual diff exceeded threshold")
                .isTrue();
    }

    @Test(description = "Checkout step one visual regression")
    public void testCheckoutStepOneVisual() {
        new LoginPage(page).login("standard_user", "secret_sauce");
        ProductsPage productsPage = new ProductsPage(page);
        productsPage.addProductToCart("Sauce Labs Backpack");
        productsPage.clickShoppingCart();
        new CartPage(page).clickCheckout();
        assertThat(VisualCompareUtil.compareScreenshot(page, "checkout_step1.png"))
                .as("Checkout step one visual diff exceeded threshold")
                .isTrue();
    }

    @Test(description = "Order confirmation page visual regression")
    public void testCheckoutCompleteVisual() {
        new LoginPage(page).login("standard_user", "secret_sauce");
        ProductsPage productsPage = new ProductsPage(page);
        productsPage.addProductToCart("Sauce Labs Backpack");
        productsPage.clickShoppingCart();
        CartPage cartPage = new CartPage(page);
        cartPage.clickCheckout();
        CheckoutPage checkoutPage = new CheckoutPage(page);
        checkoutPage.fillShippingInfo("John", "Doe", "12345");
        checkoutPage.clickContinue();
        checkoutPage.clickFinish();
        assertThat(VisualCompareUtil.compareScreenshot(page, "checkout_complete.png"))
                .as("Order confirmation page visual diff exceeded threshold")
                .isTrue();
    }
}
