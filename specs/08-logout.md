# SDD-Automation Framework - Logout Specification

## 1. OVERVIEW

Logout verifies that authenticated sessions are properly terminated and users
cannot access protected pages after signing out.

---

## 2. PAGE ELEMENTS

### Hamburger Menu (available on all authenticated pages)

| Element | Locator | Type |
|---------|---------|------|
| Menu Button | #react-burger-menu-btn | Button |
| Menu Container | .bm-menu-wrap | Container |
| Logout Link | #logout_sidebar_link | Link |
| All Items Link | #inventory_sidebar_link | Link |
| About Link | #about_sidebar_link | Link |
| Reset App State Link | #reset_sidebar_link | Link |
| Close Menu Button | #react-burger-cross-btn | Button |

### Page Object Additions

Add `logout()` and `openMenu()` to a shared `NavigationPage` or directly to `ProductsPage`:

java
public void openMenu() {
    click(page.locator("#react-burger-menu-btn"));
    waitForVisible(page.locator(".bm-menu-wrap"), 3000);
}

public void logout() {
    openMenu();
    click(page.locator("#logout_sidebar_link"));
}

public boolean isMenuOpen() {
    return page.locator(".bm-menu-wrap").getAttribute("aria-hidden").equals("false");
}


---

## 3. TEST SCENARIOS

### LO-001: Successful logout navigates to login page
**Category**: Smoke  
**Priority**: P0

**Given**: User is logged in as standard_user  
**When**: User opens menu and clicks Logout  
**Then**: User is redirected to login page (URL = `/`)  
**And**: Login button is visible  

### LO-002: After logout, back button does not restore session
**Category**: Security  
**Priority**: P1

**Given**: User is logged in and navigates to `/inventory.html`  
**When**: User logs out  
**And**: User presses browser back button  
**Then**: User remains on login page or is redirected back to login  
**And**: Protected inventory page is not accessible  

### LO-003: After logout, direct URL access redirects to login
**Category**: Security / Negative  
**Priority**: P1

**Given**: User has logged out  
**When**: User navigates directly to `https://www.saucedemo.com/inventory.html`  
**Then**: User is redirected to login page  
**And**: Inventory content is not visible  

### LO-004: Logout clears cart state
**Category**: Regression  
**Priority**: P2

**Given**: User has items in cart  
**When**: User logs out and logs back in  
**Then**: Cart badge count is 0  
**And**: Cart page shows no items  

### LO-005: Menu opens and closes correctly
**Category**: Regression  
**Priority**: P2

**Given**: User is on products page  
**When**: User clicks the hamburger menu button  
**Then**: Menu slides open showing all menu options  
**When**: User clicks the close (X) button  
**Then**: Menu closes  

---

## 4. GHERKIN SCENARIOS

gherkin
Feature: Logout functionality
  As a logged-in user
  I want to be able to log out
  So that my session is securely terminated

  Background:
    Given I am logged in as "standard_user" with password "secret_sauce"

  Scenario: Successful logout returns to login page
    When I open the navigation menu
    And I click the logout link
    Then I should be on the login page

  Scenario: After logout direct URL access redirects to login
    When I open the navigation menu
    And I click the logout link
    And I navigate directly to "/inventory.html"
    Then I should be on the login page

  Scenario: Cart is reset after logout and re-login
    Given I add "Sauce Labs Backpack" to the cart
    When I open the navigation menu
    And I click the logout link
    And I log in again as "standard_user" with password "secret_sauce"
    Then the cart badge should not be visible

  Scenario: Menu can be opened and closed
    When I open the navigation menu
    Then the navigation menu should be visible
    When I close the navigation menu
    Then the navigation menu should not be visible


---

## 5. NEW PAGE OBJECT: NavigationComponent

File: `src/main/java/org/example/pages/NavigationComponent.java`

java
public class NavigationComponent extends BasePage {
    public NavigationComponent(Page page) { super(page); }

    public void openMenu() {
        click(page.locator("#react-burger-menu-btn"));
    }

    public void closeMenu() {
        click(page.locator("#react-burger-cross-btn"));
    }

    public void logout() {
        openMenu();
        click(page.locator("#logout_sidebar_link"));
    }

    public void clickAllItems() {
        openMenu();
        click(page.locator("#inventory_sidebar_link"));
    }

    public void resetAppState() {
        openMenu();
        click(page.locator("#reset_sidebar_link"));
    }

    public boolean isMenuVisible() {
        String ariaHidden = page.locator(".bm-menu-wrap").getAttribute("aria-hidden");
        return "false".equals(ariaHidden);
    }

    public boolean isOnLoginPage() {
        return isVisible(page.locator("[data-test='login-button']"), 5000);
    }
}


---

## 6. STEP DEFINITION ADDITIONS

File: `src/test/java/org/example/stepdefs/NavigationSteps.java`

java
@When("I open the navigation menu")
public void iOpenTheNavigationMenu() {
    ctx.navigationComponent.openMenu();
}

@And("I click the logout link")
public void iClickTheLogoutLink() {
    click(page.locator("#logout_sidebar_link"));
}

@Then("I should be on the login page")
public void iShouldBeOnTheLoginPage() {
    assertThat(ctx.loginPage.isLoginPageDisplayed()).isTrue();
}

@When("I navigate directly to {string}")
public void iNavigateDirectlyTo(String path) {
    ctx.page.navigate(ConfigReader.getBaseUrl() + path);
}

@When("I log in again as {string} with password {string}")
public void iLogInAgainAs(String username, String password) {
    ctx.loginPage.login(username, password);
}

@Then("the navigation menu should be visible")
public void theNavigationMenuShouldBeVisible() {
    assertThat(ctx.navigationComponent.isMenuVisible()).isTrue();
}

@Then("the navigation menu should not be visible")
public void theNavigationMenuShouldNotBeVisible() {
    assertThat(ctx.navigationComponent.isMenuVisible()).isFalse();
}

@When("I close the navigation menu")
public void iCloseTheNavigationMenu() {
    ctx.navigationComponent.closeMenu();
}

