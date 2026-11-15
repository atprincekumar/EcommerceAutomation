# EcommerceAutomation - Test Automation Framework

An enterprise-grade UI test automation framework built using **Java 17**, **Selenium WebDriver 4**, **TestNG**, and **Maven**, following the **Page Object Model (POM)** architectural design pattern.

The framework automates and validates full-funnel e-commerce journeys against the SauceDemo Single Page Application (SPA): [https://www.saucedemo.com/](https://www.saucedemo.com/).

---

## Table of Contents
- [Project Overview](#project-overview)
- [Architecture & Design Highlights](#architecture--design-highlights)
- [Test Automation Matrix (18 Test Executions)](#test-automation-matrix-18-test-executions)
- [Requirements](#requirements)
- [Quick Start & Test Execution](#quick-start--test-execution)
- [Framework Structure](#framework-structure)
- [Reporting & Diagnostics](#reporting--diagnostics)
- [Demo Credentials](#demo-credentials)

---

## Project Overview

This repository demonstrates modern web automation testing best practices for modern web applications built on client-side routing (React/Vite). It handles asynchronous DOM mutations, dynamic component updates, robust selector strategies, data-driven validations, and financial calculation integrity.

---

## Architecture & Design Highlights

1. **Page Object Model (POM)**:
   - Encapsulates UI elements, interactions, and assertions within dedicated page classes (`LoginPage`, `ProductsPage`, `CartPage`, `CheckoutPage`).
   - Keeps test scripts readable, modular, and resilient against UI changes.

2. **SPA Client-Side Route Synchronization**:
   - Replaces fragile sleep calls with dynamic `WebDriverWait` explicit conditions (`ExpectedConditions.textToBe`, `visibilityOfElementLocated`, etc.).
   - Guarantees reliability during client-side route transitions without full page reloads.

3. **Financial & Numeric Precision**:
   - Uses `BigDecimal` calculations for all price and tax assertions (`Subtotal == Item Price`, `Total == Subtotal + Tax`) to prevent floating-point rounding errors.

4. **Data-Driven Parameterization**:
   - Leverages TestNG `@DataProvider` to exhaustively test required checkout fields (First Name, Last Name, Postal Code) with distinct negative assertions.

5. **Automatic Failure Diagnostics**:
   - Automatically captures full-page PNG screenshots upon test failure via TestNG `ITestResult` and saves them to `target/failure-screenshots/`.

6. **Cross-Platform Chrome Sizing**:
   - Enforces standard 1920x1080 resolution on both macOS and Linux environments, with seamless headless mode support (`HEADLESS=true`).

---

## Test Automation Matrix (18 Test Executions)

| Test ID | Area | Execution Type | Scenario Description | Priority |
|---|---|---|---|---|
| **TC01** | Authentication | Positive | Standard user logs in successfully and sees products catalog | P1 |
| **TC02** | Authentication | Negative | Locked-out user receives clear account locked error | P1 |
| **TC03** | Authentication | Negative | Invalid password displays credentials mismatch error | P1 |
| **TC04** | Authentication | Negative | Empty username and password submission triggers required field error | P2 |
| **TC05** | Catalog | Positive | Product names sorted alphabetically A to Z across entire list | P1 |
| **TC06** | Catalog | Positive | Product names sorted alphabetically Z to A across entire list | P1 |
| **TC07** | Catalog | Positive | Product prices sorted ascending (Low to High) via numeric checks | P1 |
| **TC08** | Catalog | Positive | Product prices sorted descending (High to Low) via numeric checks | P1 |
| **TC09** | Cart | Positive | Adding a product updates cart badge count to 1 | P1 |
| **TC10** | Cart | Positive | Add two items, remove first; verifies badge updates (2 → 1) and exact item retained | P1 |
| **TC11** | Cart | Positive | Removing item directly from Cart view transitions cart to empty state | P2 |
| **TC12** | Checkout | Positive (E2E) | Complete checkout: verifies subtotal, tax, grand total calculation, order confirmation, and emptied cart | P1 |
| **TC13a** | Checkout | Data-Driven | Missing First Name validation (`"Error: First Name is required"`) | P1 |
| **TC13b** | Checkout | Data-Driven | Missing Last Name validation (`"Error: Last Name is required"`) | P1 |
| **TC13c** | Checkout | Data-Driven | Missing Postal Code validation (`"Error: Postal Code is required"`) | P1 |
| **TC14** | Checkout | Positive | Canceling checkout overview preserves cart badge count and items | P2 |
| **TC15** | Security | Negative | Unauthenticated direct navigation to `/inventory.html` redirects to login | P1 |
| **TC16** | Security | Negative | Unauthenticated direct navigation to `/cart.html` redirects to login | P1 |

---

## Requirements

- **Java Development Kit (JDK)**: 17 or higher
- **Maven**: 3.9+ (or use IntelliJ IDEA bundled Maven)
- **Google Chrome**: Latest stable release (Selenium 4 Selenium Manager resolves `chromedriver` automatically)

---

## Quick Start & Test Execution

### 1. Run Complete Test Suite
Open your terminal in the project directory and run:

```bash
mvn clean test
```

### 2. Run in Headless Mode (CI / Background)
```bash
HEADLESS=true mvn clean test
```

### 3. Run Specific Test Class or Single Test
```bash
# Run entire test class
mvn test -Dtest=EcommerceTests

# Run a specific test method
mvn test -Dtest=EcommerceTests#TC12_checkoutCalculatesTotalAndEmptiesCart
```

### 4. Open in IntelliJ IDEA
1. Open IntelliJ IDEA → **File → Open**.
2. Select the `EcommerceAutomation` directory.
3. Allow IntelliJ to import Maven dependencies automatically.
4. Verify the Project SDK is set to JDK 17 or higher (**File → Project Structure → Project SDK**).

---

## Framework Structure

```text
EcommerceAutomation
├── pom.xml                               # Project dependencies and Maven Surefire configuration
├── README.md                             # Comprehensive project documentation
└── src
    ├── main/java/com/prince/qa/pages
    │   ├── LoginPage.java                # Authentication actions, inputs, and error locators
    │   ├── ProductsPage.java             # Catalog browsing, sorting, adding/removing items, logout
    │   ├── CartPage.java                 # Cart contents verification, item removal, checkout trigger
    │   └── CheckoutPage.java             # Customer details entry, overview calculations, finish/cancel
    └── test
        ├── java/com/prince/qa
        │   ├── base/TestBase.java        # WebDriver initialization, options, and failure screenshot hook
        │   └── tests/EcommerceTests.java # 16 test methods (18 executions) covering all scenarios
        └── resources
            └── testng.xml                # TestNG XML test suite runner configuration
```

---

## Reporting & Diagnostics

- **Surefire Reports**: XML and text reports generated in `target/surefire-reports/`.
- **Failure Screenshots**: If any assertion or step fails, a timestamped PNG screenshot is automatically saved to:
  ```text
  target/failure-screenshots/<testName>_<parameters>_<timestamp>_<uuid>.png
  ```
- **CLI Summary**: Maven Surefire logs execution status, elapsed time, and test counts directly in the console.

---

## Demo Credentials

The framework runs against SauceDemo test accounts:
- **Standard User**: `standard_user`
- **Locked User**: `locked_out_user`
- **Default Password**: `secret_sauce`
