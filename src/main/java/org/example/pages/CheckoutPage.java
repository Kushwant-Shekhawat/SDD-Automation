package org.example.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.example.utils.LocatorStore;

public class CheckoutPage extends PlaywrightActions {

    public CheckoutPage(Page page) {
        super(page);
    }

    public boolean isCheckoutStepOneDisplayed() {
        return isVisible(
                page.locator(LocatorStore.get("checkout", "stepOneTitle"))
                        .filter(new Locator.FilterOptions().setHasText("Checkout: Your Information")),
                5000);
    }

    public boolean isCheckoutStepTwoDisplayed() {
        return isVisible(
                page.locator(LocatorStore.get("checkout", "stepTwoTitle"))
                        .filter(new Locator.FilterOptions().setHasText("Checkout: Overview")),
                5000);
    }

    public boolean isOrderConfirmationDisplayed() {
        return isVisible(
                page.locator(LocatorStore.get("checkout", "completeTitle"))
                        .filter(new Locator.FilterOptions().setHasText("Checkout: Complete!")),
                5000);
    }

    public void enterFirstName(String firstName) {
        fill(page.locator(LocatorStore.get("checkout", "firstName")), firstName);
    }

    public void enterLastName(String lastName) {
        fill(page.locator(LocatorStore.get("checkout", "lastName")), lastName);
    }

    public void enterPostalCode(String postalCode) {
        fill(page.locator(LocatorStore.get("checkout", "postalCode")), postalCode);
    }

    public void fillShippingInfo(String firstName, String lastName, String postalCode) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterPostalCode(postalCode);
    }

    public void clickContinue() {
        click(page.locator(LocatorStore.get("checkout", "continueButton")));
    }

    public void clickFinish() {
        click(page.locator(LocatorStore.get("checkout", "finishButton")));
    }

    public void clickCancel() {
        click(page.locator(LocatorStore.get("checkout", "cancelButton")));
    }

    public String getOrderConfirmationText() {
        return getText(page.locator(LocatorStore.get("checkout", "thankYouHeader")));
    }

    public String getItemTotal() {
        return getText(page.locator(LocatorStore.get("checkout", "subtotalLabel")));
    }

    public String getTaxAmount() {
        return getText(page.locator(LocatorStore.get("checkout", "taxLabel")));
    }

    public String getOrderTotal() {
        return getText(page.locator(LocatorStore.get("checkout", "totalLabel")));
    }

    public String getErrorMessage() {
        return getText(page.locator(LocatorStore.get("checkout", "errorMessage")));
    }

    public boolean isErrorDisplayed() {
        return isVisible(page.locator(LocatorStore.get("checkout", "errorMessage")), 3000);
    }

    public boolean isErrorVisible() {
        return isVisible(page.locator(LocatorStore.get("checkout", "errorMessage")), 2000);
    }

    public void clickBackHome() {
        click(page.locator(LocatorStore.get("checkout", "backHomeButton")));
    }

    public void dismissError() {
        click(page.locator(LocatorStore.get("checkout", "errorDismiss")));
    }

    public double parsePrice(String priceLabel) {
        return Double.parseDouble(priceLabel.replaceAll("[^0-9.]", ""));
    }

    public boolean isThankYouMessageDisplayed() {
        return isVisible(page.locator(LocatorStore.get("checkout", "thankYouHeader")), 5000);
    }

    public String getThankYouMessage() {
        return getText(page.locator(LocatorStore.get("checkout", "thankYouHeader")));
    }
}
