package com.prince.qa.tests;

import com.prince.qa.base.TestBase;
import com.prince.qa.pages.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class EcommerceTests extends TestBase {

    @Test
    public void TC01_standardUserCanLogIn() {
        LoginPage login = new LoginPage(driver).open(baseUrl);
        login.login(standardUser, validPassword);
        ProductsPage products = new ProductsPage(driver);
        Assert.assertTrue(products.isDisplayed(), "Products page should open upon valid login");
    }

    @Test
    public void TC02_lockedOutUserCannotLogIn() {
        LoginPage login = new LoginPage(driver).open(baseUrl);
        login.login(lockedUser, validPassword);
        Assert.assertTrue(login.errorText().toLowerCase().contains("locked"),
                "Locked-out user should receive an account locked error");
        Assert.assertTrue(login.isLoginButtonDisplayed(),
                "User should remain on the login page");
    }

    @Test
    public void TC03_invalidPasswordIsRejected() {
        LoginPage login = new LoginPage(driver).open(baseUrl);
        login.login(standardUser, "incorrect-password");
        Assert.assertTrue(login.errorText().toLowerCase().contains("do not match"),
                "Invalid password should display credentials do not match error");
        Assert.assertTrue(login.isLoginButtonDisplayed(),
                "User should remain on the login page");
    }

    @Test
    public void TC04_emptyCredentialsAreRejected() {
        LoginPage login = new LoginPage(driver).open(baseUrl);
        login.login("", "");
        Assert.assertTrue(login.errorText().contains("Username is required"),
                "Empty login should display Username is required error");
        Assert.assertTrue(login.isLoginButtonDisplayed(),
                "User should remain on the login page");
    }

    @Test
    public void TC05_sortProductNamesAtoZ() {
        LoginPage login = new LoginPage(driver).open(baseUrl);
        login.login(standardUser, validPassword);
        ProductsPage products = new ProductsPage(driver);
        products.sortBy("az");
        List<String> actual = products.getNames();
        List<String> expected = new ArrayList<>(actual);
        Collections.sort(expected);
        Assert.assertFalse(actual.isEmpty(), "Product list should not be empty");
        Assert.assertEquals(actual, expected, "Products should be sorted A to Z");
    }

    @Test
    public void TC06_sortProductNamesZtoA() {
        LoginPage login = new LoginPage(driver).open(baseUrl);
        login.login(standardUser, validPassword);
        ProductsPage products = new ProductsPage(driver);
        products.sortBy("za");
        List<String> actual = products.getNames();
        List<String> expected = new ArrayList<>(actual);
        expected.sort(Collections.reverseOrder());
        Assert.assertFalse(actual.isEmpty(), "Product list should not be empty");
        Assert.assertEquals(actual, expected, "Products should be sorted Z to A");
    }

    @Test
    public void TC07_sortPriceLowToHigh() {
        LoginPage login = new LoginPage(driver).open(baseUrl);
        login.login(standardUser, validPassword);
        ProductsPage products = new ProductsPage(driver);
        products.sortBy("lohi");
        List<BigDecimal> actual = products.getPrices();
        List<BigDecimal> expected = new ArrayList<>(actual);
        Collections.sort(expected);
        Assert.assertFalse(actual.isEmpty(), "Price list should not be empty");
        Assert.assertEquals(actual, expected, "Prices must be sorted in ascending order");
    }

    @Test
    public void TC08_sortPriceHighToLow() {
        LoginPage login = new LoginPage(driver).open(baseUrl);
        login.login(standardUser, validPassword);
        ProductsPage products = new ProductsPage(driver);
        products.sortBy("hilo");
        List<BigDecimal> actual = products.getPrices();
        List<BigDecimal> expected = new ArrayList<>(actual);
        expected.sort(Collections.reverseOrder());
        Assert.assertFalse(actual.isEmpty(), "Price list should not be empty");
        Assert.assertEquals(actual, expected, "Prices must be sorted in descending order");
    }

    @Test
    public void TC09_addProductToCartUpdatesBadge() {
        LoginPage login = new LoginPage(driver).open(baseUrl);
        login.login(standardUser, validPassword);
        ProductsPage products = new ProductsPage(driver);
        products.addFirstProductToCart();
        Assert.assertEquals(products.cartBadgeCount(), "1", "Cart badge should display 1");
    }

    @Test
    public void TC10_addTwoRemoveOneChecksBadgeAndContents() {
        LoginPage login = new LoginPage(driver).open(baseUrl);
        login.login(standardUser, validPassword);
        ProductsPage products = new ProductsPage(driver);
        Assert.assertTrue(products.isDisplayed(), "Products page should be displayed");
        List<String> initialNames = products.getNames();
        Assert.assertTrue(initialNames.size() >= 2, "At least two products are required");

        products.addFirstNItems(2);
        Assert.assertEquals(products.cartBadgeCount(), "2", "Badge should show 2 after adding 2 items");

        products.removeFirstItem();
        Assert.assertEquals(products.cartBadgeCount(), "1", "Badge should show 1 after removing 1 item");

        CartPage cart = products.openCart();
        Assert.assertEquals(cart.itemCount(), 1, "Cart should contain 1 item");
        Assert.assertEquals(cart.itemNames(), List.of(initialNames.get(1)),
                "Cart should contain the second item after removing the first");
    }

    @Test
    public void TC11_removeItemFromCartPage() {
        LoginPage login = new LoginPage(driver).open(baseUrl);
        login.login(standardUser, validPassword);
        ProductsPage products = new ProductsPage(driver);
        products.addFirstProductToCart();
        CartPage cart = products.openCart();
        Assert.assertEquals(cart.itemCount(), 1, "Cart should have 1 item before removal");

        cart.removeFirstItem();
        Assert.assertTrue(cart.isEmpty(), "Cart should be empty after removing the item");
    }

    @Test
    public void TC12_checkoutCalculatesTotalAndEmptiesCart() {
        LoginPage login = new LoginPage(driver).open(baseUrl);
        login.login(standardUser, validPassword);
        ProductsPage products = new ProductsPage(driver);
        List<BigDecimal> prices = products.getPrices();
        Assert.assertFalse(prices.isEmpty(), "Products list should have prices");
        BigDecimal expectedItemPrice = prices.get(0);

        products.addFirstProductToCart();
        CartPage cart = products.openCart();
        Assert.assertEquals(cart.itemCount(), 1);

        CheckoutPage checkout = cart.proceedToCheckout();
        checkout.fillCustomer("Prince", "Kumar", "834001");
        checkout.continueCheckout();

        BigDecimal subtotal = money(checkout.subtotalText());
        BigDecimal tax = money(checkout.taxText());
        BigDecimal total = money(checkout.totalText());

        Assert.assertEquals(subtotal.compareTo(expectedItemPrice), 0,
                "Subtotal must match item price");
        Assert.assertEquals(total.compareTo(subtotal.add(tax)), 0,
                "Total must equal subtotal plus tax");

        checkout.finish();
        Assert.assertTrue(checkout.isOrderConfirmationDisplayed(),
                "Order confirmation message should be visible");

        cart.open(baseUrl);
        Assert.assertTrue(cart.isEmpty(), "Cart should be empty after completing checkout");
    }

    @DataProvider(name = "missingCheckoutField")
    public Object[][] missingCheckoutField() {
        return new Object[][] {
            {"first name", "", "Kumar", "834001", "Error: First Name is required"},
            {"last name", "Prince", "", "834001", "Error: Last Name is required"},
            {"postal code", "Prince", "Kumar", "", "Error: Postal Code is required"}
        };
    }

    @Test(dataProvider = "missingCheckoutField")
    public void TC13_checkoutRequiredFieldsValidation(
            String missingField, String first, String last, String postal, String expectedError) {
        LoginPage login = new LoginPage(driver).open(baseUrl);
        login.login(standardUser, validPassword);
        ProductsPage products = new ProductsPage(driver);
        products.addFirstProductToCart();
        CartPage cart = products.openCart();
        CheckoutPage checkout = cart.proceedToCheckout();

        checkout.fillCustomer(first, last, postal);
        checkout.continueCheckout();

        Assert.assertEquals(checkout.errorText().trim(), expectedError,
                "Validation message should match for missing " + missingField);
    }

    @Test
    public void TC14_cancelCheckoutOverviewPreservesCart() {
        LoginPage login = new LoginPage(driver).open(baseUrl);
        login.login(standardUser, validPassword);
        ProductsPage products = new ProductsPage(driver);
        products.addFirstProductToCart();
        CartPage cart = products.openCart();
        CheckoutPage checkout = cart.proceedToCheckout();
        checkout.fillCustomer("Prince", "Kumar", "834001");
        checkout.continueCheckout();

        ProductsPage returnedProducts = checkout.cancelCheckout();
        Assert.assertTrue(returnedProducts.isDisplayed(),
                "Canceling checkout overview should return to Products page");
        Assert.assertEquals(returnedProducts.cartBadgeCount(), "1",
                "Cart badge count should still be 1 after canceling");

        CartPage returnedCart = returnedProducts.openCart();
        Assert.assertEquals(returnedCart.itemCount(), 1,
                "Cart item must be preserved after canceling checkout overview");
    }

    @Test
    public void TC15_loggedOutUserCannotOpenProductsUrl() {
        LoginPage login = new LoginPage(driver).open(baseUrl);
        login.login(standardUser, validPassword);
        ProductsPage products = new ProductsPage(driver);
        Assert.assertTrue(products.isDisplayed(), "Products page should be displayed");

        products.logout();
        products.openDirectUrl(baseUrl);

        LoginPage redirectedLogin = new LoginPage(driver);
        Assert.assertTrue(redirectedLogin.isLoginButtonDisplayed(),
                "Logged-out user should be redirected to login page");
        Assert.assertTrue(redirectedLogin.errorText().toLowerCase().contains("can only access"),
                "User should see access denied error message");
    }

    @Test
    public void TC16_loggedOutUserCannotOpenCartUrl() {
        LoginPage login = new LoginPage(driver).open(baseUrl);
        login.login(standardUser, validPassword);
        ProductsPage products = new ProductsPage(driver);
        products.logout();

        driver.get(baseUrl + "/cart.html");

        LoginPage redirectedLogin = new LoginPage(driver);
        Assert.assertTrue(redirectedLogin.isLoginButtonDisplayed(),
                "Logged-out user navigating to cart should be redirected to login page");
        Assert.assertTrue(redirectedLogin.errorText().toLowerCase().contains("can only access"),
                "User should see access denied error message");
    }

    private BigDecimal money(String label) {
        String numeric = label.replaceAll("[^0-9.]", "");
        return new BigDecimal(numeric);
    }
}
