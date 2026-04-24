package org.example.tests;

import org.example.base.BaseTest;
import org.example.pages.CartPage;
import org.example.pages.CheckoutPage;
import org.example.pages.LoginPage;
import org.example.pages.ProductsPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckoutTest extends BaseTest {

    private CartPage cartPage;
    private CheckoutPage checkoutPage;

    @BeforeMethod
    public void setUpCheckout() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login("standard_user", "secret_sauce");

        ProductsPage productsPage = new ProductsPage(page);
        productsPage.addProductToCart("Sauce Labs Backpack");
        productsPage.clickShoppingCart();

        cartPage = new CartPage(page);
        checkoutPage = new CheckoutPage(page);
    }

    @Test(description = "Checkout step one is displayed after clicking checkout")
    public void testCheckoutStepOneDisplayed() {
        cartPage.clickCheckout();
        assertThat(checkoutPage.isCheckoutStepOneDisplayed()).isTrue();
    }

    @Test(description = "Completing checkout info proceeds to step two")
    public void testCheckoutStepTwo() {
        cartPage.clickCheckout();
        checkoutPage.fillShippingInfo("John", "Doe", "12345");
        checkoutPage.clickContinue();

        assertThat(checkoutPage.isCheckoutStepTwoDisplayed()).isTrue();
    }

    @Test(description = "Order summary shows correct item total")
    public void testOrderSummaryItemTotal() {
        cartPage.clickCheckout();
        checkoutPage.fillShippingInfo("John", "Doe", "12345");
        checkoutPage.clickContinue();

        assertThat(checkoutPage.getItemTotal()).contains("29.99");
    }

    @Test(description = "Finishing checkout shows confirmation page")
    public void testOrderConfirmation() {
        cartPage.clickCheckout();
        checkoutPage.fillShippingInfo("John", "Doe", "12345");
        checkoutPage.clickContinue();
        checkoutPage.clickFinish();

        assertThat(checkoutPage.isOrderConfirmationDisplayed()).isTrue();
        assertThat(checkoutPage.getOrderConfirmationText())
                .contains("Thank you for your order");
    }

    @Test(description = "Empty first name shows validation error")
    public void testCheckoutEmptyFirstName() {
        cartPage.clickCheckout();
        checkoutPage.fillShippingInfo("", "Doe", "12345");
        checkoutPage.clickContinue();

        assertThat(checkoutPage.isErrorDisplayed()).isTrue();
        assertThat(checkoutPage.getErrorMessage()).contains("First Name is required");
    }

    @Test(description = "Cancel checkout returns to cart")
    public void testCancelCheckoutReturnsToCart() {
        cartPage.clickCheckout();
        checkoutPage.clickCancel();

        assertThat(cartPage.isCartPageDisplayed()).isTrue();
    }
}
