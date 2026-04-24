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
├── login_valid.csv
├── login_invalid.csv
├── products.csv
├── checkout.csv
└── checkout_invalid.csv
```

---

## 3. LOGIN TEST DATA

### File: login_valid.csv

**Purpose**: Valid credentials for positive login scenarios

```csv
username,password,expectedResult
standard_user,secret_sauce,success
performance_glitch_user,secret_sauce,success
```

### File: login_invalid.csv

**Purpose**: Negative login scenarios — locked user, wrong password, empty fields

```csv
username,password,expectedError
locked_out_user,secret_sauce,Epic sadface: Sorry, this user has been locked out.
standard_user,wrong_password,Epic sadface: Username and password do not match any user in this service
invalid_user,secret_sauce,Epic sadface: Username and password do not match any user in this service
,secret_sauce,Epic sadface: Username is required
standard_user,,Epic sadface: Password is required
```

---

## 4. PRODUCT TEST DATA

### File: products.csv

**Purpose**: Product reference data (names, prices) used in step definitions for assertions

```csv
productName,price,description
Sauce Labs Backpack,$29.99,carry.allTheThings() with the sleek
Sauce Labs Bike Light,$9.99,A red light isn't the desired state
Sauce Labs Bolt T-Shirt,$15.99,Get your testing superhero on
Sauce Labs Fleece Jacket,$49.99,It's not every day that you come across
Sauce Labs Onesie,$7.99,Rib snap infant onesie for the junior automation engineer
Test.allTheThings() T-Shirt (Red),$15.99,This classic Sauce Labs t-shirt
```

### Data Field Definitions

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| productName | String | Product name as shown in UI | Sauce Labs Backpack |
| price | String | Display price including `$` | `$29.99` |
| description | String | Partial product description | carry.allTheThings()... |

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

There is no `cart-data.csv` file. Cart test scenarios use inline product names from `products.csv` as reference — product names and prices are embedded directly in the Cucumber feature files (`cart.feature`, `multi_item_cart.feature`).

### Product prices for cart total assertions

| Product | Price |
|---------|-------|
| Sauce Labs Onesie | $7.99 |
| Sauce Labs Bike Light | $9.99 |
| Sauce Labs Bolt T-Shirt | $15.99 |
| Test.allTheThings() T-Shirt (Red) | $15.99 |
| Sauce Labs Backpack | $29.99 |
| Sauce Labs Fleece Jacket | $49.99 |

---

## 6. CHECKOUT TEST DATA

### File: checkout.csv

**Purpose**: Valid checkout data for successful order completion

```csv
firstName,lastName,postalCode
John,Doe,12345
Jane,Smith,67890
Alice,Johnson,10001
```

---

### File: checkout_invalid.csv

**Purpose**: Invalid checkout data for form validation negative tests

```csv
firstName,lastName,postalCode,expectedError
,,12345,Error: First Name is required
John,,12345,Error: Last Name is required
John,Doe,,Error: Postal Code is required
```

### Data Field Definitions

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| firstName | String | First name (or empty) | (empty) |
| lastName | String | Last name (or empty) | Doe |
| postalCode | String | Postal code (or empty) | 12345 |
| expectedError | String | Expected error message text | Error: First Name is required |

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

## 8. USAGE IN CUCUMBER STEP DEFINITIONS

CSV data is read by `TestDataProvider.java` and can be used in step definitions as needed. The framework is **Cucumber-only** — there are no standalone TestNG test classes. Test data from CSV files can be referenced in step definitions via `TestDataProvider`.

### Example: reading checkout data in a step definition

```java
// In CheckoutSteps.java or a data-driven scenario
List<CSVRecord> records = TestDataProvider.readCsv("checkout.csv");
for (CSVRecord record : records) {
    String firstName  = record.get("firstName");
    String lastName   = record.get("lastName");
    String postalCode = record.get("postalCode");
}
```

Most test scenarios use inline data in Cucumber `Examples:` tables rather than loading CSV at runtime. The CSV files serve as the master reference for expected values and for bulk data scenarios.

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

## 11. CHECKOUT VALIDATION DATA

### Step One Validation Rules

| Scenario | firstName | lastName | postalCode | Expected Error |
|----------|-----------|----------|------------|----------------|
| CV-001 | (empty) | Doe | 12345 | First Name is required |
| CV-002 | John | (empty) | 12345 | Last Name is required |
| CV-003 | John | Doe | (empty) | Postal Code is required |
| CV-004 | (empty) | (empty) | (empty) | First Name is required |

### Step Two — Order Summary Locators

| Element | Locator |
|---------|---------|
| Item Total | `.summary_subtotal_label` |
| Tax Amount | `.summary_tax_label` |
| Order Total | `.summary_total_label` |
| Error Close | `.error-button` |

### Rule: Order total must equal item total + tax (tax > 0)

---

## 12. NEXT PHASE: CLARIFY

This test data specification will be reviewed for:
1. Data accuracy and completeness
2. CSV format validity
3. Column alignment with code expectations
4. Test coverage through data combinations

**Status**: ✅ Test Data Management Specification Complete