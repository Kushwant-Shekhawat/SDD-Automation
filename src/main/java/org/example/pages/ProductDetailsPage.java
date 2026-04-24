package org.example.pages;

import com.microsoft.playwright.Page;
import org.example.utils.LocatorStore;

public class ProductDetailsPage extends PlaywrightActions {

    public ProductDetailsPage(Page page) {
        super(page);
    }

    public boolean isProductDetailsPageDisplayed() {
        try {
            page.waitForURL("**/inventory-item.html**",
                    new Page.WaitForURLOptions().setTimeout(5000));
            return true;
        } catch (Exception e) {
            return isVisible(page.locator(LocatorStore.get("product-details", "productName")), 3000);
        }
    }

    public String getProductName() {
        return getText(page.locator(LocatorStore.get("product-details", "productName")));
    }

    public String getProductDescription() {
        return getText(page.locator(LocatorStore.get("product-details", "productDescription")));
    }

    public String getProductPrice() {
        return getText(page.locator(LocatorStore.get("product-details", "productPrice")));
    }

    public void addToCart() {
        click(page.locator(LocatorStore.get("product-details", "addToCartButton")));
    }

    public void removeFromCart() {
        click(page.locator(LocatorStore.get("product-details", "removeButton")));
    }

    public void clickBackToProducts() {
        click(page.locator(LocatorStore.get("product-details", "backToProducts")));
    }

    public boolean isAddToCartButtonVisible() {
        return isVisible(page.locator(LocatorStore.get("product-details", "addToCartButton")), 3000);
    }

    public boolean isRemoveButtonVisible() {
        return isVisible(page.locator(LocatorStore.get("product-details", "removeButton")), 3000);
    }
}
