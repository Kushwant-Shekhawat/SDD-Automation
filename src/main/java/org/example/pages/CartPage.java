package org.example.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.List;

public class CartPage extends BasePage {

    public CartPage(Page page) {
        super(page);
    }

    public boolean isCartPageDisplayed() {
        return isVisible(page.locator(".title").filter(new Locator.FilterOptions().setHasText("Your Cart")), 5000);
    }

    public List<String> getCartItemNames() {
        return page.locator(".inventory_item_name").allTextContents();
    }

    public int getCartItemCount() {
        return page.locator(".cart_item").count();
    }

    public void removeItem(String itemName) {
        Locator cartItem = page.locator(".cart_item")
                .filter(new Locator.FilterOptions().setHasText(itemName));
        click(cartItem.locator("button"));
    }

    public void clickContinueShopping() {
        click(page.locator("[data-test='continue-shopping']"));
    }

    public void clickCheckout() {
        click(page.locator("[data-test='checkout']"));
    }

    public boolean isItemInCart(String itemName) {
        return page.locator(".inventory_item_name")
                .filter(new Locator.FilterOptions().setHasText(itemName))
                .isVisible();
    }
}
