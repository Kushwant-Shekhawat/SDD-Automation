package org.example.pages;

import com.microsoft.playwright.Page;

public class ProductDetailsPage extends BasePage {

    public ProductDetailsPage(Page page) {
        super(page);
    }

    public boolean isProductDetailsPageDisplayed() {
        try {
            page.waitForURL("**/inventory-item.html**",
                    new Page.WaitForURLOptions().setTimeout(5000));
            return true;
        } catch (Exception e) {
            return isVisible(page.locator(".inventory_details_name"), 3000);
        }
    }

    public String getProductName() {
        return getText(page.locator(".inventory_details_name"));
    }

    public String getProductDescription() {
        return getText(page.locator(".inventory_details_desc"));
    }

    public String getProductPrice() {
        return getText(page.locator(".inventory_details_price"));
    }

    public void addToCart() {
        click(page.locator("button.btn_primary"));
    }

    public void removeFromCart() {
        click(page.locator("button.btn_secondary"));
    }

    public void clickBackToProducts() {
        click(page.locator("[data-test='back-to-products']"));
    }

    public boolean isAddToCartButtonVisible() {
        return isVisible(page.locator("button.btn_primary"), 3000);
    }

    public boolean isRemoveButtonVisible() {
        return isVisible(page.locator("button.btn_secondary"), 3000);
    }
}
