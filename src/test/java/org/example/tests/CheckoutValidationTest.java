package org.example.tests;

import org.example.base.BaseTest;
import org.example.pages.CartPage;
import org.example.pages.CheckoutPage;
import org.example.pages.LoginPage;
import org.example.pages.ProductsPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckoutValidationTest extends BaseTest {

    private CartPage cartPage;
    private CheckoutPage checkoutPage;

    @BeforeMethod
    public void setUpCheckout() {
        new LoginPage(page).login("standard_user", "secret_sauce");
        ProductsPage productsPage = new ProductsPage(page);
        productsPage.addProductToCart("Sauce Labs Backpack");
        productsPage.clickShoppingCart();
        cartPage = new CartPage(page);
        checkoutPage = new CheckoutPage(page);
        cartPage.clickCheckout();
    }

    @Test(description = "Missing first name shows validation error")
    public void testMissingFirstName() {
        checkoutPage.fillShippingInfo("", "Doe", "12345");
        checkoutPage.clickContinue();
        assertThat(checkoutPage.isErrorDisplayed()).isTrue();
        assertThat(checkoutPage.getErrorMessage()).contains("First Name is required");
    }

    @Test(description = "Missing last name shows validation error")
    public void testMissingLastName() {
        checkoutPage.fillShippingInfo("John", "", "12345");
        checkoutPage.clickContinue();
        assertThat(checkoutPage.isErrorDisplayed()).isTrue();
        assertThat(checkoutPage.getErrorMessage()).contains("Last Name is required");
    }

    @Test(description = "Missing postal code shows validation error")
    public void testMissingPostalCode() {
        checkoutPage.fillShippingInfo("John", "Doe", "");
        checkoutPage.clickContinue();
        assertThat(checkoutPage.isErrorDisplayed()).isTrue();
        assertThat(checkoutPage.getErrorMessage()).contains("Postal Code is required");
    }

    @Test(description = "All fields empty shows first name error")
    public void testAllFieldsEmpty() {
        checkoutPage.fillShippingInfo("", "", "");
        checkoutPage.clickContinue();
        assertThat(checkoutPage.isErrorDisplayed()).isTrue();
        assertThat(checkoutPage.getErrorMessage()).contains("First Name is required");
    }

    @Test(description = "Validation error can be dismissed with X button")
    public void testDismissValidationError() {
        checkoutPage.fillShippingInfo("", "Doe", "12345");
        checkoutPage.clickContinue();
        assertThat(checkoutPage.isErrorDisplayed()).isTrue();
        checkoutPage.dismissError();
        assertThat(checkoutPage.isErrorVisible()).isFalse();
    }

    @Test(description = "Cancel on checkout step one returns to cart")
    public void testCancelOnStepOneReturnsToCart() {
        checkoutPage.clickCancel();
        assertThat(cartPage.isCartPageDisplayed()).isTrue();
    }

    @Test(description = "Cancel on checkout step two returns to products page")
    public void testCancelOnStepTwoReturnsToProducts() {
        checkoutPage.fillShippingInfo("John", "Doe", "12345");
        checkoutPage.clickContinue();
        assertThat(checkoutPage.isCheckoutStepTwoDisplayed()).isTrue();
        checkoutPage.clickCancel();
        ProductsPage productsPage = new ProductsPage(page);
        assertThat(productsPage.isProductsPageDisplayed()).isTrue();
    }

    @Test(description = "Order total includes tax — total is greater than item subtotal")
    public void testOrderTotalIncludesTax() {
        checkoutPage.fillShippingInfo("John", "Doe", "12345");
        checkoutPage.clickContinue();
        double itemTotal = checkoutPage.parsePrice(checkoutPage.getItemTotal());
        double orderTotal = checkoutPage.parsePrice(checkoutPage.getOrderTotal());
        assertThat(orderTotal).isGreaterThan(itemTotal);
    }

    @Test(description = "Special characters in name fields pass validation")
    public void testSpecialCharactersInNameFields() {
        checkoutPage.fillShippingInfo("John-O'Brien", "Müller", "SW1A 1AA");
        checkoutPage.clickContinue();
        assertThat(checkoutPage.isCheckoutStepTwoDisplayed()).isTrue();
    }
}
