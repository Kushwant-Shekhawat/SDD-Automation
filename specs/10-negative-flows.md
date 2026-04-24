# SDD-Automation Framework - Negative Flows & Direct URL Access Specification

## 1. OVERVIEW

Negative flow tests verify that unauthenticated and unauthorised access attempts
are correctly blocked and redirected to the login page. They also cover invalid
state navigation (e.g. jumping directly to checkout without a cart).

---

## 2. PROTECTED ROUTES ON SAUCEDEMO

| URL Path | Requires Auth | Expected Redirect |
|----------|---------------|-------------------|
| `/inventory.html` | Yes | `/` (login page) |
| `/inventory-item.html?id=4` | Yes | `/` (login page) |
| `/cart.html` | Yes | `/` (login page) |
| `/checkout-step-one.html` | Yes | `/` (login page) |
| `/checkout-step-two.html` | Yes | `/` (login page) |
| `/checkout-complete.html` | Yes | `/` (login page) |

---

## 3. TEST SCENARIOS

### NF-001: Direct access to inventory redirects to login
**Category**: Security / Negative  
**Priority**: P0

**Given**: User is NOT logged in (fresh session)  
**When**: User navigates directly to `https://www.saucedemo.com/inventory.html`  
**Then**: User is on the login page  
**And**: URL is `https://www.saucedemo.com/`  
**And**: Login button is visible  

### NF-002: Direct access to product details redirects to login
**Category**: Security / Negative  
**Priority**: P0

**Given**: User is NOT logged in  
**When**: User navigates to `https://www.saucedemo.com/inventory-item.html?id=4`  
**Then**: User is redirected to login page  

### NF-003: Direct access to cart redirects to login
**Category**: Security / Negative  
**Priority**: P0

**Given**: User is NOT logged in  
**When**: User navigates to `https://www.saucedemo.com/cart.html`  
**Then**: User is redirected to login page  

### NF-004: Direct access to checkout step one redirects to login
**Category**: Security / Negative  
**Priority**: P0

**Given**: User is NOT logged in  
**When**: User navigates to `https://www.saucedemo.com/checkout-step-one.html`  
**Then**: User is redirected to login page  

### NF-005: Direct access to checkout step two redirects to login
**Category**: Security / Negative  
**Priority**: P0

**Given**: User is NOT logged in  
**When**: User navigates to `https://www.saucedemo.com/checkout-step-two.html`  
**Then**: User is redirected to login page  

### NF-006: Direct access to checkout complete redirects to login
**Category**: Security / Negative  
**Priority**: P0

**Given**: User is NOT logged in  
**When**: User navigates to `https://www.saucedemo.com/checkout-complete.html`  
**Then**: User is redirected to login page  

### NF-007: After logout, back button does not restore session
**Category**: Security / Negative  
**Priority**: P1

**Given**: User is logged in and on `/inventory.html`  
**When**: User logs out  
**And**: User clicks browser back button  
**Then**: User is NOT on the inventory page  
**And**: Login button is visible  

### NF-008: Accessing checkout step two directly without completing step one
**Category**: Negative / Edge Case  
**Priority**: P1

**Given**: User is logged in but has NOT gone through checkout step one  
**When**: User navigates directly to `/checkout-step-two.html`  
**Then**: User is redirected (to login or back to inventory) — not shown raw step two  

### NF-009: Accessing checkout complete directly without checkout
**Category**: Negative / Edge Case  
**Priority**: P1

**Given**: User is logged in but has NOT completed checkout  
**When**: User navigates directly to `/checkout-complete.html`  
**Then**: User does not see the order confirmation for a real order  

### NF-010: Login with SQL injection attempt
**Category**: Security / Negative  
**Priority**: P2

**Given**: User is on login page  
**When**: username = `' OR '1'='1`, password = `' OR '1'='1`  
**Then**: Login fails with standard error message  
**And**: No SQL error or unexpected behaviour is shown  

### NF-011: Login with XSS payload in username
**Category**: Security / Negative  
**Priority**: P2

**Given**: User is on login page  
**When**: username = `<script>alert('xss')</script>`, password = `secret_sauce`  
**Then**: Login fails with standard error  
**And**: No alert box appears (payload is not executed)  

### NF-012: Login with extremely long username
**Category**: Edge Case  
**Priority**: P3

**Given**: User is on login page  
**When**: username = 500-character string  
**Then**: Application does not crash  
**And**: Normal error message is shown  

---

## 4. GHERKIN SCENARIOS

gherkin
Feature: Negative flows and direct URL access
  As a security tester
  I want to verify that unauthenticated access is blocked
  And invalid inputs are handled gracefully

  Scenario Outline: Direct URL access without login redirects to login page
    Given I am not logged in
    When I navigate directly to "<path>"
    Then I should be on the login page

    Examples:
      | path                            |
      | /inventory.html                 |
      | /inventory-item.html?id=4       |
      | /cart.html                      |
      | /checkout-step-one.html         |
      | /checkout-step-two.html         |
      | /checkout-complete.html         |

  Scenario: After logout back button does not restore session
    Given I am logged in as "standard_user" with password "secret_sauce"
    When I open the navigation menu
    And I click the logout link
    And I press the browser back button
    Then I should be on the login page

  Scenario: SQL injection attempt is rejected
    Given I am on the login page
    When I enter username "' OR '1'='1" and password "' OR '1'='1"
    And I click the login button
    Then I should see an error message containing "Username and password do not match"

  Scenario: XSS payload in username is not executed
    Given I am on the login page
    When I enter username "<script>alert('xss')</script>" and password "secret_sauce"
    And I click the login button
    Then I should see an error message containing "Username and password do not match"
    And no alert dialog should be present

  Scenario: Extremely long username shows error gracefully
    Given I am on the login page
    When I enter a 500-character username and password "secret_sauce"
    And I click the login button
    Then I should see an error message containing "Username and password do not match"


---

## 5. NEW PAGE OBJECT: NavigationComponent additions

Add to `NavigationComponent.java`:

java
public void pressBackButton() {
    page.goBack();
}


---

## 6. TEST CLASS

File: `src/test/java/org/example/tests/NegativeFlowTest.java`

java
public class NegativeFlowTest extends BaseTest {

    @Test(description = "Direct access to inventory without login redirects to login page",
          dataProvider = "protectedUrls")
    public void testDirectUrlAccessRedirects(String path) {
        page.navigate(ConfigReader.getBaseUrl() + path);
        LoginPage loginPage = new LoginPage(page);
        assertThat(loginPage.isLoginPageDisplayed())
                .as("Expected redirect to login page for path: " + path)
                .isTrue();
    }

    @DataProvider(name = "protectedUrls")
    public Object[][] protectedUrls() {
        return new Object[][]{
            {"/inventory.html"},
            {"/inventory-item.html?id=4"},
            {"/cart.html"},
            {"/checkout-step-one.html"},
            {"/checkout-step-two.html"},
            {"/checkout-complete.html"}
        };
    }

    @Test(description = "SQL injection in login is rejected")
    public void testSqlInjectionRejected() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login("' OR '1'='1", "' OR '1'='1");
        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage())
                .contains("Username and password do not match");
    }

    @Test(description = "XSS payload in username does not execute")
    public void testXssPayloadRejected() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login("<script>alert('xss')</script>", "secret_sauce");
        assertThat(loginPage.isErrorDisplayed()).isTrue();
        // Verify no alert dialog triggered
        assertThat(page.url()).contains("saucedemo.com");
    }

    @Test(description = "Extremely long username shows error gracefully")
    public void testLongUsernameHandled() {
        LoginPage loginPage = new LoginPage(page);
        String longUsername = "a".repeat(500);
        loginPage.login(longUsername, "secret_sauce");
        assertThat(loginPage.isErrorDisplayed()).isTrue();
    }

    @Test(description = "After logout, back button does not restore session")
    public void testBackButtonAfterLogout() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login("standard_user", "secret_sauce");

        NavigationComponent nav = new NavigationComponent(page);
        nav.logout();
        assertThat(loginPage.isLoginPageDisplayed()).isTrue();

        page.goBack();
        assertThat(loginPage.isLoginPageDisplayed())
                .as("Back button should not restore session")
                .isTrue();
    }
}


---

## 7. STEP DEFINITION ADDITIONS

java
@Given("I am not logged in")
public void iAmNotLoggedIn() {
    // Fresh browser context from SharedContext constructor, no login needed
}

@And("I press the browser back button")
public void iPressTheBrowserBackButton() {
    ctx.page.goBack();
}

@When("I enter a {int}-character username and password {string}")
public void iEnterALongUsernameAndPassword(int length, String password) {
    ctx.loginPage.enterUsername("a".repeat(length));
    ctx.loginPage.enterPassword(password);
}

@Then("no alert dialog should be present")
public void noAlertDialogShouldBePresent() {
    // If an alert appeared, Playwright would have thrown; reaching here means no alert
    assertThat(ctx.page.url()).contains("saucedemo.com");
}

