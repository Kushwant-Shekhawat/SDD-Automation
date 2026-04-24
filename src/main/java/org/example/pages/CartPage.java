package org.example.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.example.utils.LocatorStore;

import java.util.List;

public class CartPage extends PlaywrightActions {

    public CartPage(Page page) {
        super(page);
    }

    public boolean isCartPageDisplayed() {
        return isVisible(
                page.locator(LocatorStore.get("cart", "cartTitle"))
                        .filter(new Locator.FilterOptions().setHasText("Your Cart")),
                5000);
    }

    public List<String> getCartItemNames() {
        return getAllTexts(page.locator(LocatorStore.get("cart", "itemName")));
    }

    public int getCartItemCount() {
        return getCount(page.locator(LocatorStore.get("cart", "cartItem")));
    }

    public void removeItem(String itemName) {
        Locator cartItem = page.locator(LocatorStore.get("cart", "cartItem"))
                .filter(new Locator.FilterOptions().setHasText(itemName));
        click(cartItem.locator("button"));
    }

    public void clickContinueShopping() {
        click(page.locator(LocatorStore.get("cart", "continueShopping")));
    }

    public void clickCheckout() {
        click(page.locator(LocatorStore.get("cart", "checkoutButton")));
    }

    public boolean isItemInCart(String itemName) {
        return page.locator(LocatorStore.get("cart", "itemName"))
                .filter(new Locator.FilterOptions().setHasText(itemName))
                .isVisible();
    }
}
