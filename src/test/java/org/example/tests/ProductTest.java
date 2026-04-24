package org.example.tests;

import org.example.base.BaseTest;
import org.example.pages.LoginPage;
import org.example.pages.ProductDetailsPage;
import org.example.pages.ProductsPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductTest extends BaseTest {

    private ProductsPage productsPage;

    @BeforeMethod
    public void loginBeforeTest() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login("standard_user", "secret_sauce");
        productsPage = new ProductsPage(page);
    }

    @Test(description = "Products page displays 6 products")
    public void testProductCountIs6() {
        assertThat(productsPage.getProductCount()).isEqualTo(6);
    }

    @Test(description = "All product names are visible")
    public void testProductNamesVisible() {
        List<String> names = productsPage.getProductNames();
        assertThat(names).hasSize(6);
        assertThat(names).contains("Sauce Labs Backpack", "Sauce Labs Bike Light");
    }

    @Test(description = "Adding a product updates cart badge")
    public void testAddProductToCart() {
        productsPage.addProductToCart("Sauce Labs Backpack");
        assertThat(productsPage.getCartItemCount()).isEqualTo(1);
    }

    @Test(description = "Removing a product clears cart badge")
    public void testRemoveProductFromCart() {
        productsPage.addProductToCart("Sauce Labs Backpack");
        productsPage.removeProductFromCart("Sauce Labs Backpack");
        assertThat(productsPage.getCartItemCount()).isEqualTo(0);
    }

    @Test(description = "Clicking product name opens product details page")
    public void testClickProductOpensDetails() {
        productsPage.clickProductName("Sauce Labs Backpack");
        ProductDetailsPage detailsPage = new ProductDetailsPage(page);
        assertThat(detailsPage.isProductDetailsPageDisplayed()).isTrue();
        assertThat(detailsPage.getProductName()).isEqualTo("Sauce Labs Backpack");
    }

    @Test(description = "Sort by price low to high changes order")
    public void testSortByPriceLowToHigh() {
        productsPage.selectSortOption("lohi");
        List<String> prices = productsPage.getProductPrices();
        assertThat(prices.get(0)).isEqualTo("$7.99");
    }

    @Test(description = "Sort by name Z to A changes order")
    public void testSortByNameZtoA() {
        productsPage.selectSortOption("za");
        List<String> names = productsPage.getProductNames();
        assertThat(names.get(0)).isEqualTo("Test.allTheThings() T-Shirt (Red)");
    }
}
