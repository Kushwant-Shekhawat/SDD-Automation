package org.example.tests;

import org.example.base.BaseTest;
import org.example.pages.LoginPage;
import org.example.pages.ProductDetailsPage;
import org.example.pages.ProductsPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductSortingTest extends BaseTest {

    private ProductsPage productsPage;

    @BeforeMethod
    public void loginBeforeTest() {
        new LoginPage(page).login("standard_user", "secret_sauce");
        productsPage = new ProductsPage(page);
    }

    @Test(description = "Default sort shows products A to Z — first is Sauce Labs Backpack")
    public void testDefaultSortIsAToZ() {
        assertThat(productsPage.getFirstProductName()).isEqualTo("Sauce Labs Backpack");
    }

    @Test(description = "Sort by Name Z to A — first product is Test.allTheThings()")
    public void testSortByNameZtoA() {
        productsPage.selectSortOption("za");
        assertThat(productsPage.getFirstProductName())
                .isEqualTo("Test.allTheThings() T-Shirt (Red)");
    }

    @Test(description = "Sort by Price low to high — prices in ascending order")
    public void testSortByPriceLowToHigh() {
        productsPage.selectSortOption("lohi");
        assertThat(productsPage.getFirstProductPrice()).isEqualTo("$7.99");
        assertThat(productsPage.getLastProductPrice()).isEqualTo("$49.99");
        assertThat(productsPage.arePricesInAscendingOrder()).isTrue();
    }

    @Test(description = "Sort by Price high to low — prices in descending order")
    public void testSortByPriceHighToLow() {
        productsPage.selectSortOption("hilo");
        assertThat(productsPage.getFirstProductPrice()).isEqualTo("$49.99");
        assertThat(productsPage.getLastProductPrice()).isEqualTo("$7.99");
        assertThat(productsPage.arePricesInDescendingOrder()).isTrue();
    }

    @Test(description = "Product count stays 6 after every sort option")
    public void testProductCountStaysAfterSort() {
        for (String option : new String[]{"az", "za", "lohi", "hilo"}) {
            productsPage.selectSortOption(option);
            assertThat(productsPage.getProductCount())
                    .as("Product count should be 6 for sort option: " + option)
                    .isEqualTo(6);
        }
    }

    @Test(description = "Back to products after viewing details returns to products page")
    public void testBackFromDetailsReturnsToProducts() {
        productsPage.selectSortOption("lohi");
        assertThat(productsPage.getFirstProductPrice()).isEqualTo("$7.99");

        productsPage.clickProductName("Sauce Labs Onesie");
        ProductDetailsPage detailsPage = new ProductDetailsPage(page);
        detailsPage.clickBackToProducts();

        assertThat(productsPage.isProductsPageDisplayed()).isTrue();
    }
}
