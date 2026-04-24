Feature: Visual regression testing
  As a QA engineer
  I want to compare page screenshots against baselines
  So that unintended UI changes are detected

  Scenario: Login page matches visual baseline
    Given I am on the login page
    Then the page should match the visual baseline "login_page.png"

  Scenario: Products page matches visual baseline
    Given I am logged in as "standard_user" with password "secret_sauce"
    Then the page should match the visual baseline "products_page.png"

  Scenario: Product details page matches visual baseline
    Given I am logged in as "standard_user" with password "secret_sauce"
    When I click on product "Sauce Labs Backpack"
    Then the page should match the visual baseline "product_details_backpack.png"

  Scenario: Cart page with one item matches visual baseline
    Given I am logged in as "standard_user" with password "secret_sauce"
    When I add "Sauce Labs Backpack" to the cart
    And I navigate to the cart
    Then the page should match the visual baseline "cart_one_item.png"

  Scenario: Checkout step one matches visual baseline
    Given I am logged in as "standard_user" with password "secret_sauce"
    And I have "Sauce Labs Backpack" in my cart
    And I am on the cart page
    When I click the checkout button
    Then the page should match the visual baseline "checkout_step1.png"

  Scenario: Order confirmation page matches visual baseline
    Given I am logged in as "standard_user" with password "secret_sauce"
    And I have "Sauce Labs Backpack" in my cart
    And I am on the cart page
    When I click the checkout button
    And I enter first name "John", last name "Doe", and postal code "12345"
    And I click continue on checkout
    And I click finish
    Then the page should match the visual baseline "checkout_complete.png"
