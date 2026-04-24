package org.example.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.List;

public class ProductsPage extends BasePage {

    public ProductsPage(Page page) {
        super(page);
    }

    public boolean isProductsPageDisplayed() {
        return isVisible(page.locator(".title").filter(new Locator.FilterOptions().setHasText("Products")), 5000);
    }

    public List<String> getProductNames() {
        return page.locator(".inventory_item_name").allTextContents();
    }

    public void addProductToCart(String productName) {
        Locator product = page.locator(".inventory_item")
                .filter(new Locator.FilterOptions().setHasText(productName));
        click(product.locator("button"));
    }

    public void removeProductFromCart(String productName) {
        Locator product = page.locator(".inventory_item")
                .filter(new Locator.FilterOptions().setHasText(productName));
        click(product.locator("button"));
    }

    public void clickProductName(String productName) {
        Locator item = page.locator(".inventory_item")
                .filter(new Locator.FilterOptions().setHasText(productName));
        click(item.locator("a[id*='title_link']"));
    }

    public int getCartItemCount() {
        Locator badge = page.locator(".shopping_cart_badge");
        if (!badge.isVisible()) return 0;
        return Integer.parseInt(badge.textContent().trim());
    }

    public void clickShoppingCart() {
        click(page.locator(".shopping_cart_link"));
    }

    public void selectSortOption(String sortValue) {
        page.locator(".product_sort_container").selectOption(sortValue);
    }

    public List<String> getProductPrices() {
        return page.locator(".inventory_item_price").allTextContents();
    }

    public int getProductCount() {
        return page.locator(".inventory_item").count();
    }

    public String getFirstProductName() {
        return page.locator(".inventory_item_name").first().textContent().trim();
    }

    public String getLastProductName() {
        List<String> names = getProductNames();
        return names.get(names.size() - 1);
    }

    public String getFirstProductPrice() {
        return page.locator(".inventory_item_price").first().textContent().trim();
    }

    public String getLastProductPrice() {
        List<String> prices = getProductPrices();
        return prices.get(prices.size() - 1);
    }

    public boolean arePricesInAscendingOrder() {
        List<Double> prices = getProductPrices().stream()
                .map(p -> Double.parseDouble(p.replace("$", "").trim()))
                .collect(java.util.stream.Collectors.toList());
        for (int i = 0; i < prices.size() - 1; i++) {
            if (prices.get(i) > prices.get(i + 1)) return false;
        }
        return true;
    }

    public boolean arePricesInDescendingOrder() {
        List<Double> prices = getProductPrices().stream()
                .map(p -> Double.parseDouble(p.replace("$", "").trim()))
                .collect(java.util.stream.Collectors.toList());
        for (int i = 0; i < prices.size() - 1; i++) {
            if (prices.get(i) < prices.get(i + 1)) return false;
        }
        return true;
    }
}
