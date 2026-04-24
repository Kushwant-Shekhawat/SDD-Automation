package org.example.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.example.utils.LocatorStore;

import java.util.List;

public class ProductsPage extends PlaywrightActions {

    public ProductsPage(Page page) {
        super(page);
    }

    public boolean isProductsPageDisplayed() {
        return isVisible(
                page.locator(LocatorStore.get("products", "pageTitle"))
                        .filter(new Locator.FilterOptions().setHasText("Products")),
                5000);
    }

    public List<String> getProductNames() {
        return getAllTexts(page.locator(LocatorStore.get("products", "productNameLink")));
    }

    public void addProductToCart(String productName) {
        // Filter-based chaining: base selector from JSON, product match via Playwright filter
        Locator product = page.locator(LocatorStore.get("products", "inventoryItem"))
                .filter(new Locator.FilterOptions().setHasText(productName));
        click(product.locator("button"));
    }

    public void removeProductFromCart(String productName) {
        Locator product = page.locator(LocatorStore.get("products", "inventoryItem"))
                .filter(new Locator.FilterOptions().setHasText(productName));
        click(product.locator("button"));
    }

    public void clickProductName(String productName) {
        Locator item = page.locator(LocatorStore.get("products", "inventoryItem"))
                .filter(new Locator.FilterOptions().setHasText(productName));
        click(item.locator("a[id*='title_link']"));
    }

    public int getCartItemCount() {
        Locator badge = page.locator(LocatorStore.get("products", "cartBadge"));
        if (!badge.isVisible()) return 0;
        return Integer.parseInt(badge.textContent().trim());
    }

    public void clickShoppingCart() {
        click(page.locator(LocatorStore.get("products", "cartLink")));
    }

    public void selectSortOption(String sortValue) {
        selectByValue(page.locator(LocatorStore.get("products", "sortDropdown")), sortValue);
    }

    public List<String> getProductPrices() {
        return getAllTexts(page.locator(LocatorStore.get("products", "productPrice")));
    }

    public int getProductCount() {
        return getCount(page.locator(LocatorStore.get("products", "inventoryItem")));
    }

    public String getFirstProductName() {
        return page.locator(LocatorStore.get("products", "productNameLink")).first().textContent().trim();
    }

    public String getLastProductName() {
        List<String> names = getProductNames();
        return names.get(names.size() - 1);
    }

    public String getFirstProductPrice() {
        return page.locator(LocatorStore.get("products", "productPrice")).first().textContent().trim();
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

    public void addAllProductsToCart() {
        page.locator(LocatorStore.get("products", "allAddToCartBtns")).all().forEach(btn -> btn.click());
    }
}
