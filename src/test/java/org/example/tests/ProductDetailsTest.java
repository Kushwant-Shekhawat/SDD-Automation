package org.example.tests;

import org.example.base.BaseTest;
import org.example.pages.LoginPage;
import org.example.pages.ProductDetailsPage;
import org.example.pages.ProductsPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductDetailsTest extends BaseTest {

    private ProductsPage productsPage;
    private ProductDetailsPage detailsPage;

    @BeforeMethod
    public void loginBeforeTest() {
        new LoginPage(page).login("standard_user", "secret_sauce");
        productsPage = new ProductsPage(page);
        detailsPage = new ProductDetailsPage(page);
    }

    @Test(description = "Product details page shows correct name")
    public void testProductDetailsName() {
        productsPage.clickProductName("Sauce Labs Backpack");
        assertThat(detailsPage.isProductDetailsPageDisplayed()).isTrue();
        assertThat(detailsPage.getProductName()).isEqualTo("Sauce Labs Backpack");
    }

    @Test(description = "Product details page shows correct price")
    public void testProductDetailsPrice() {
        productsPage.clickProductName("Sauce Labs Backpack");
        assertThat(detailsPage.getProductPrice()).isEqualTo("$29.99");
    }

    @Test(description = "Product details page shows non-empty description")
    public void testProductDetailsDescription() {
        productsPage.clickProductName("Sauce Labs Bike Light");
        assertThat(detailsPage.getProductDescription()).isNotEmpty();
    }

    @Test(description = "Add to cart from product details updates cart badge")
    public void testAddToCartFromDetailsPage() {
        productsPage.clickProductName("Sauce Labs Backpack");
        detailsPage.addToCart();
        assertThat(productsPage.getCartItemCount()).isEqualTo(1);
        assertThat(detailsPage.isRemoveButtonVisible()).isTrue();
    }

    @Test(description = "Remove from cart on product details clears cart badge")
    public void testRemoveFromCartOnDetailsPage() {
        productsPage.clickProductName("Sauce Labs Backpack");
        detailsPage.addToCart();
        detailsPage.removeFromCart();
        assertThat(productsPage.getCartItemCount()).isEqualTo(0);
        assertThat(detailsPage.isAddToCartButtonVisible()).isTrue();
    }

    @Test(description = "Back to products from detail page returns to inventory")
    public void testBackToProductsFromDetailsPage() {
        productsPage.clickProductName("Sauce Labs Backpack");
        detailsPage.clickBackToProducts();
        assertThat(productsPage.isProductsPageDisplayed()).isTrue();
    }
}
