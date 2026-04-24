# SDD-Automation Framework - Test Data Management Specification

## 1. TEST DATA OVERVIEW

### Data Organization Strategy
- **Format**: CSV files for simplicity and data-driven testing
- **Location**: `src/test/resources/testdata/`
- **Loading**: TestNG @DataProvider + Apache Commons CSV
- **Scope**: All data-driven tests use externalized data
- **Maintenance**: Easy to update without code changes

---

## 2. CSV FILE SPECIFICATIONS

### Directory Structure

```
src/test/resources/testdata/
├── login-data.csv
├── product-data.csv
├── cart-data.csv
├── checkout-data.csv
└── invalid-checkout-data.csv
```

---

## 3. LOGIN TEST DATA

### File: login-data.csv

**Purpose**: Data for all login-related tests
**Usage**: LoginTests.java @DataProvider

```csv
username,password,expectedResult,description
standard_user,secret_sauce,SUCCESS,Valid standard user login
locked_user,secret_sauce,LOCKED,Locked user account
invalid_user,secret_sauce,INVALID,Non-existent user
standard_user,wrong_password,INVALID,Wrong password
standard_user,,EMPTY,Empty password
,secret_sauce,EMPTY,Empty username
visual_user,secret_sauce,SUCCESS,Visual user login
performance_glitchy_user,secret_sauce,SUCCESS,Performance user login
```

### Data Field Definitions

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| username | String | Login username | standard_user |
| password | String | Login password | secret_sauce |
| expectedResult | String | Expected outcome (SUCCESS, INVALID, LOCKED, EMPTY) | SUCCESS |
| description | String | Test case description | Valid standard user login |

### Test Data Categories

#### Valid Login Users
```csv
standard_user,secret_sauce,SUCCESS,Valid standard user
visual_user,secret_sauce,SUCCESS,Visual rendering user
performance_glitchy_user,secret_sauce,SUCCESS,Performance user
```

#### Invalid Login Attempts
```csv
invalid_user,secret_sauce,INVALID,Non-existent user
standard_user,wrong_password,INVALID,Wrong password
locked_user,secret_sauce,LOCKED,Locked user account
```

#### Empty Field Tests
```csv
standard_user,,EMPTY,Missing password
,secret_sauce,EMPTY,Missing username
,,,EMPTY,All fields empty
```

---

## 4. PRODUCT TEST DATA

### File: product-data.csv

**Purpose**: Product information for browse, filter, and sort tests
**Usage**: ProductsTests.java @DataProvider

```csv
productId,productName,price,category,description,availability
1,Sauce Labs Backpack,29.99,bags,carry.allTheThings() with the sleek, streamlined Sly Pack that melds uncompromising style with unnoticed utilitarian.,IN_STOCK
2,Sauce Labs Bike Light,9.99,lights,A red light isn't the desired state in testing but it sure helps when riding your bike at night and you have the Flashlight.,IN_STOCK
3,Sauce Labs Bolt T-Shirt,15.99,apparel,Get your testing superhero on with the Sauce Labs bolt T-shirt complete with duct tape adhesive for instant Winged Monkey application.,IN_STOCK
4,Sauce Labs Fleece Jacket,49.99,apparel,It's not just a fleece. It's an expression. And it leaves an impression.,IN_STOCK
5,Sauce Labs Onesie,7.99,apparel,Uh, matching onesies with the pet. So this is my thing now.,IN_STOCK
6,Test.allTheThings() T-Shirt (Red),15.99,apparel,This classic Sauce Labs t-shirt is perfect to wear when cozying up to your keyboard to do some testing.,IN_STOCK
```

### Data Field Definitions

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| productId | Integer | Unique product ID | 1 |
| productName | String | Product name as shown in UI | Sauce Labs Backpack |
| price | Double | Product price | 29.99 |
| category | String | Product category | bags, apparel, lights |
| description | String | Full product description | carry.allTheThings()... |
| availability | String | Stock status | IN_STOCK |

### Product Test Scenarios

#### Price Range Tests
```csv
# Low price products
2,Sauce Labs Onesie,7.99,apparel,Onesie,IN_STOCK
2,Sauce Labs Bike Light,9.99,lights,Light,IN_STOCK

# Mid price products
3,Sauce Labs Bolt T-Shirt,15.99,apparel,T-Shirt,IN_STOCK
1,Sauce Labs Backpack,29.99,bags,Backpack,IN_STOCK

# High price products
4,Sauce Labs Fleece Jacket,49.99,apparel,Jacket,IN_STOCK
```

#### Sort Test Data
```csv
# For A-Z sort test
productName_sorted
Sauce Labs Backpack
Sauce Labs Bike Light
Sauce Labs Bolt T-Shirt
Sauce Labs Fleece Jacket
Sauce Labs Onesie
Test.allTheThings() T-Shirt (Red)

# For Price Low-to-High sort test
productName_by_price_asc
Sauce Labs Onesie (7.99)
Sauce Labs Bike Light (9.99)
Sauce Labs Bolt T-Shirt (15.99)
Test.allTheThings() T-Shirt (15.99)
Sauce Labs Backpack (29.99)
Sauce Labs Fleece Jacket (49.99)
```

---

## 5. SHOPPING CART TEST DATA

### File: cart-data.csv

**Purpose**: Cart operations test data (add, remove, quantity)
**Usage**: ShoppingCartTests.java @DataProvider

```csv
testCase,productNames,quantities,expectedCount,expectedTotal,operation
add_single,Sauce Labs Backpack,1,1,29.99,ADD
add_multiple,"Sauce Labs Backpack,Sauce Labs Bike Light,Sauce Labs Bolt T-Shirt",1,3,55.97,ADD
add_duplicate,Sauce Labs Backpack,2,2,59.98,ADD
remove_from_cart,Sauce Labs Backpack,1,0,0.00,REMOVE
multiple_products_remove,"Sauce Labs Backpack,Sauce Labs Bike Light",1,1,29.99,REMOVE
verify_total,"Sauce Labs Backpack,Sauce Labs Bike Light",1,2,39.98,VERIFY
```

### Data Field Definitions

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| testCase | String | Descriptive test case name | add_single |
| productNames | String (CSV) | Product names to add/remove | Sauce Labs Backpack,Sauce Labs Bike Light |
| quantities | Integer | Quantity per product | 1 |
| expectedCount | Integer | Expected items in cart | 1 |
| expectedTotal | Double | Expected cart total | 29.99 |
| operation | String | ADD, REMOVE, VERIFY | ADD |

### Cart Scenario Variations

#### Single Product Scenarios
```csv
testCase,productNames,quantities,expectedCount,expectedTotal,operation
add_cheapest,Sauce Labs Onesie,1,1,7.99,ADD
add_expensive,Sauce Labs Fleece Jacket,1,1,49.99,ADD
add_mid_price,Sauce Labs Backpack,1,1,29.99,ADD
```

#### Multiple Product Scenarios
```csv
testCase,productNames,quantities,expectedCount,expectedTotal,operation
two_products,"Sauce Labs Backpack,Sauce Labs Bike Light",1,2,39.98,ADD
three_products,"Sauce Labs Backpack,Sauce Labs Bike Light,Sauce Labs Bolt T-Shirt",1,3,55.97,ADD
all_products,"Sauce Labs Backpack,Sauce Labs Bike Light,Sauce Labs Bolt T-Shirt,Sauce Labs Fleece Jacket,Sauce Labs Onesie,Test.allTheThings() T-Shirt (Red)",1,6,128.95,ADD
```

#### Remove Scenarios
```csv
testCase,productNames,quantities,expectedCount,expectedTotal,operation
remove_one,Sauce Labs Backpack,1,0,0.00,REMOVE
remove_all,"Sauce Labs Backpack,Sauce Labs Bike Light",1,0,0.00,REMOVE
remove_one_of_two,"Sauce Labs Backpack,Sauce Labs Bike Light",1,1,9.99,REMOVE
```

---

## 6. CHECKOUT TEST DATA

### File: checkout-data.csv

**Purpose**: Valid checkout data for successful order completion
**Usage**: CheckoutTests.java @DataProvider

```csv
firstName,lastName,postalCode,expectedResult,description,testUser
John,Doe,12345,SUCCESS,Valid standard format,standard_user
Jane,Smith,54321,SUCCESS,Valid data set 2,standard_user
Robert,Johnson,99999,SUCCESS,Valid data set 3,standard_user
Alice,Williams,00001,SUCCESS,Valid with leading zeros,standard_user
Jean-Paul,O'Reilly,12345,SUCCESS,Special characters in name,standard_user
Maria,Garcia-Lopez,67890,SUCCESS,Hyphenated last name,standard_user
```

### Data Field Definitions

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| firstName | String | First name for checkout | John |
| lastName | String | Last name for checkout | Doe |
| postalCode | String | Postal code (5+ digits) | 12345 |
| expectedResult | String | Expected outcome (SUCCESS, ERROR, TIMEOUT) | SUCCESS |
| description | String | Test case description | Valid standard format |
| testUser | String | User account to use | standard_user |

### Checkout Scenario Variations

#### Valid Data Variations
```csv
firstName,lastName,postalCode,expectedResult,description
John,Doe,12345,SUCCESS,Standard US format
Maria,Garcia,67890,SUCCESS,Spanish name
Jean-Pierre,Dupont,75001,SUCCESS,French format
```

#### Special Character Tests
```csv
firstName,lastName,postalCode,expectedResult,description
Jean-Paul,O'Reilly,12345,SUCCESS,Hyphens and apostrophes
Mary-Jane,Smith-Jones,54321,SUCCESS,Multiple hyphens
Jose,Perez-Garcia,99999,SUCCESS,Spanish hyphenated
```

#### Edge Case Tests
```csv
firstName,lastName,postalCode,expectedResult,description
A,B,12345,SUCCESS,Single character names
Alexander,Schwarzenegger,12345,SUCCESS,Long names
Mary,Smith-Johnson-Williams,54321,SUCCESS,Very long last name
```

---

### File: invalid-checkout-data.csv

**Purpose**: Invalid checkout data for negative tests
**Usage**: CheckoutTests.java negative test cases

```csv
firstName,lastName,postalCode,expectedError,description
,Doe,12345,First Name is required,Empty first name
John,,12345,Last Name is required,Empty last name
John,Doe,,Postal Code is required,Empty postal code
,,,First Name is required,All fields empty
```

### Data Field Definitions

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| firstName | String | First name (or empty) | (empty) |
| lastName | String | Last name (or empty) | Doe |
| postalCode | String | Postal code (or empty) | 12345 |
| expectedError | String | Expected error message | First Name is required |
| description | String | Test case description | Empty first name |

---

## 7. TEST DATA PROVIDER IMPLEMENTATION

### Java Code Example: TestDataProvider.java

```java
package org.example.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class TestDataProvider {
    
    private static final String TEST_DATA_PATH = "src/test/resources/testdata/";
    
    // ===== LOGIN DATA =====
    public static Object[][] getLoginData() throws Exception {
        return readCSV("login-data.csv", 4); // 4 columns
    }
    
    public static Object[][] getValidLoginData() throws Exception {
        // Filter only SUCCESS records
        List<CSVRecord> records = readCSVRecords("login-data.csv");
        List<Object[]> validData = new ArrayList<>();
        
        for (CSVRecord record : records) {
            if ("SUCCESS".equals(record.get("expectedResult"))) {
                validData.add(new Object[]{
                    record.get("username"),
                    record.get("password"),
                    record.get("expectedResult"),
                    record.get("description")
                });
            }
        }
        
        return validData.toArray(new Object[0][]);
    }
    
    // ===== PRODUCT DATA =====
    public static Object[][] getProductData() throws Exception {
        return readCSV("product-data.csv", 6); // 6 columns
    }
    
    public static List<String> getAllProductNames() throws Exception {
        List<CSVRecord> records = readCSVRecords("product-data.csv");
        List<String> names = new ArrayList<>();
        
        for (CSVRecord record : records) {
            names.add(record.get("productName"));
        }
        
        return names;
    }
    
    public static Map<String, Double> getProductPrices() throws Exception {
        List<CSVRecord> records = readCSVRecords("product-data.csv");
        Map<String, Double> prices = new HashMap<>();
        
        for (CSVRecord record : records) {
            prices.put(
                record.get("productName"),
                Double.parseDouble(record.get("price"))
            );
        }
        
        return prices;
    }
    
    // ===== CART DATA =====
    public static Object[][] getCartData() throws Exception {
        return readCSV("cart-data.csv", 6); // 6 columns
    }
    
    // ===== CHECKOUT DATA =====
    public static Object[][] getCheckoutData() throws Exception {
        return readCSV("checkout-data.csv", 6); // 6 columns
    }
    
    public static Object[][] getInvalidCheckoutData() throws Exception {
        return readCSV("invalid-checkout-data.csv", 5); // 5 columns
    }
    
    // ===== HELPER METHODS =====
    private static Object[][] readCSV(String fileName, int expectedColumns) throws Exception {
        List<CSVRecord> records = readCSVRecords(fileName);
        Object[][] data = new Object[records.size()][expectedColumns];
        
        int rowIndex = 0;
        for (CSVRecord record : records) {
            for (int colIndex = 0; colIndex < expectedColumns; colIndex++) {
                data[rowIndex][colIndex] = record.get(colIndex);
            }
            rowIndex++;
        }
        
        return data;
    }
    
    private static List<CSVRecord> readCSVRecords(String fileName) throws Exception {
        String filePath = TEST_DATA_PATH + fileName;
        Reader reader = Files.newBufferedReader(Paths.get(filePath));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
        
        List<CSVRecord> records = new ArrayList<>(csvParser.getRecords());
        csvParser.close();
        
        return records;
    }
}
```

---

## 8. USAGE IN TEST CLASSES

### Example: LoginTests with Data Provider

```java
package org.example.tests;

import org.example.base.BaseTest;
import org.example.pages.LoginPage;
import org.example.utils.TestDataProvider;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LoginTests extends BaseTest {
    
    @DataProvider(name = "loginData")
    public Object[][] getLoginData() throws Exception {
        return TestDataProvider.getLoginData();
    }
    
    @Test(dataProvider = "loginData", 
          description = "Login with various credentials")
    public void testLoginWithDataProvider(String username, 
                                          String password, 
                                          String expectedResult, 
                                          String description) {
        LoginPage loginPage = new LoginPage(page);
        
        loginPage.login(username, password);
        
        if ("SUCCESS".equals(expectedResult)) {
            // Assert successful login
        } else if ("INVALID".equals(expectedResult)) {
            // Assert error message
        } else if ("LOCKED".equals(expectedResult)) {
            // Assert locked user message
        }
    }
}
```

### Example: CheckoutTests with Data Provider

```java
package org.example.tests;

import org.example.base.BaseTest;
import org.example.pages.LoginPage;
import org.example.pages.ProductsPage;
import org.example.pages.CartPage;
import org.example.pages.CheckoutPage;
import org.example.utils.TestDataProvider;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CheckoutTests extends BaseTest {
    
    @DataProvider(name = "checkoutData")
    public Object[][] getCheckoutData() throws Exception {
        return TestDataProvider.getCheckoutData();
    }
    
    @Test(dataProvider = "checkoutData",
          description = "Complete checkout with valid data")
    public void testCompleteCheckout(String firstName,
                                      String lastName,
                                      String postalCode,
                                      String expectedResult,
                                      String description,
                                      String testUser) {
        // Login
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(testUser, "secret_sauce");
        
        // Add product to cart
        ProductsPage productsPage = new ProductsPage(page);
        productsPage.addProductToCart("Sauce Labs Backpack");
        
        // Checkout
        productsPage.goToCart();
        CartPage cartPage = new CartPage(page);
        cartPage.proceedToCheckout();
        
        CheckoutPage checkoutPage = new CheckoutPage(page);
        checkoutPage.enterFirstName(firstName);
        checkoutPage.enterLastName(lastName);
        checkoutPage.enterPostalCode(postalCode);
        checkoutPage.clickContinueButton();
        
        // Assertions based on expectedResult
        if ("SUCCESS".equals(expectedResult)) {
            checkoutPage.clickFinishButton();
            assert checkoutPage.isThankYouMessageDisplayed();
        }
    }
}
```

---

## 9. DATA VALIDATION RULES

### Login Data Rules
- ✅ Username must not be null
- ✅ Password must not be null (can be empty for empty field tests)
- ✅ expectedResult must be one of: SUCCESS, INVALID, LOCKED, EMPTY
- ✅ description must be meaningful

### Product Data Rules
- ✅ productId must be unique
- ✅ productName must match exact UI text
- ✅ price must be positive decimal
- ✅ category must be valid category name
- ✅ availability must be IN_STOCK or OUT_OF_STOCK

### Checkout Data Rules
- ✅ firstName and lastName must be strings (can be empty for negative tests)
- ✅ postalCode must be numeric or alphanumeric (can be empty for negative tests)
- ✅ expectedResult must be SUCCESS or ERROR
- ✅ testUser must be valid SauceDemo user

---

## 10. DATA MAINTENANCE GUIDELINES

### Adding New Test Data

1. **Identify the requirement** - Which test case needs data?
2. **Choose appropriate file** - login-data.csv, product-data.csv, etc.
3. **Add row to CSV** - Include all required columns
4. **Update documentation** - Add to this specification
5. **Verify format** - Ensure columns align with code expectations
6. **Test** - Run affected test cases to verify data loads correctly

### Updating Existing Data

1. **Edit CSV file** - Make changes directly
2. **No code changes needed** - Data provider automatically picks up changes
3. **Verify** - Run tests to ensure changes don't break anything
4. **Document** - Update this specification if rules change

### Best Practices

- ✅ Keep CSV files in version control
- ✅ Use meaningful descriptions
- ✅ Include comments for complex data
- ✅ Maintain data consistency across files
- ✅ Use actual product names/prices from SauceDemo
- ✅ Document any special character handling
- ✅ Test data changes before committing

---

## 11. NEXT PHASE: CLARIFY

This test data specification will be reviewed for:
1. Data accuracy and completeness
2. CSV format validity
3. Column alignment with code expectations
4. Test coverage through data combinations

**Status**: ✅ Test Data Management Specification Complete