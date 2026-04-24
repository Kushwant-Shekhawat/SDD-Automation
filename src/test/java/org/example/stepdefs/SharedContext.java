package org.example.stepdefs;

import com.microsoft.playwright.Page;
import org.example.driver.WebDriverManager;
import org.example.pages.*;

public class SharedContext {

    public final Page page;
    public final LoginPage loginPage;
    public final ProductsPage productsPage;
    public final ProductDetailsPage productDetailsPage;
    public final CartPage cartPage;
    public final CheckoutPage checkoutPage;

    public SharedContext() {
        WebDriverManager.initBrowser();
        this.page = WebDriverManager.getPage();
        this.loginPage = new LoginPage(page);
        this.productsPage = new ProductsPage(page);
        this.productDetailsPage = new ProductDetailsPage(page);
        this.cartPage = new CartPage(page);
        this.checkoutPage = new CheckoutPage(page);
        page.navigate(org.example.utils.ConfigReader.getBaseUrl());
    }

    public void tearDown() {
        WebDriverManager.closeBrowser();
    }
}
