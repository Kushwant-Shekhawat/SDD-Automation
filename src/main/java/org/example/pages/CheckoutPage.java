package org.example.pages;

import com.microsoft.playwright.Page;

public class CheckoutPage extends BasePage {

    public CheckoutPage(Page page) {
        super(page);
    }

    public boolean isCheckoutStepOneDisplayed() {
        return isVisible(page.locator(".title").filter(
                new com.microsoft.playwright.Locator.FilterOptions().setHasText("Checkout: Your Information")), 5000);
    }

    public boolean isCheckoutStepTwoDisplayed() {
        return isVisible(page.locator(".title").filter(
                new com.microsoft.playwright.Locator.FilterOptions().setHasText("Checkout: Overview")), 5000);
    }

    public boolean isOrderConfirmationDisplayed() {
        return isVisible(page.locator(".title").filter(
                new com.microsoft.playwright.Locator.FilterOptions().setHasText("Checkout: Complete!")), 5000);
    }

    public void enterFirstName(String firstName) {
        fill(page.locator("[data-test='firstName']"), firstName);
    }

    public void enterLastName(String lastName) {
        fill(page.locator("[data-test='lastName']"), lastName);
    }

    public void enterPostalCode(String postalCode) {
        fill(page.locator("[data-test='postalCode']"), postalCode);
    }

    public void fillShippingInfo(String firstName, String lastName, String postalCode) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterPostalCode(postalCode);
    }

    public void clickContinue() {
        click(page.locator("[data-test='continue']"));
    }

    public void clickFinish() {
        click(page.locator("[data-test='finish']"));
    }

    public void clickCancel() {
        click(page.locator("[data-test='cancel']"));
    }

    public String getOrderConfirmationText() {
        return getText(page.locator(".complete-header"));
    }

    public String getItemTotal() {
        return getText(page.locator(".summary_subtotal_label"));
    }

    public String getTaxAmount() {
        return getText(page.locator(".summary_tax_label"));
    }

    public String getOrderTotal() {
        return getText(page.locator(".summary_total_label"));
    }

    public String getErrorMessage() {
        return getText(page.locator("[data-test='error']"));
    }

    public boolean isErrorDisplayed() {
        return isVisible(page.locator("[data-test='error']"), 3000);
    }

    public void clickBackHome() {
        click(page.locator("[data-test='back-to-products']"));
    }

    public void dismissError() {
        click(page.locator(".error-button"));
    }

    public boolean isErrorVisible() {
        return isVisible(page.locator("[data-test='error']"), 2000);
    }

    public double parsePrice(String priceLabel) {
        return Double.parseDouble(priceLabel.replaceAll("[^0-9.]", ""));
    }
}
