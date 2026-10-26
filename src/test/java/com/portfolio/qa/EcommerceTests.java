
package com.portfolio.qa;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EcommerceTests {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String URL =
            "https://www.saucedemo.com/";

    private static final String USERNAME = "standard_user";
    private static final String PASSWORD = "secret_sauce";

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get(URL);
    }

    private void login(String username, String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("user-name"))).sendKeys(username);

        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("login-button")).click();
    }

    @Test
    public void validLoginTest() {
        login(USERNAME, PASSWORD);

        Assert.assertTrue(
                wait.until(ExpectedConditions.urlContains("inventory.html")),
                "Successful login should open the products page."
        );
    }

    @Test
    public void invalidLoginTest() {
        login("wrong_user", "wrong_password");

        WebElement error = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("[data-test='error']")));

        Assert.assertTrue(
                error.getText().contains(
                        "Username and password do not match"),
                "An appropriate error should appear for invalid credentials."
        );
    }

    @Test
    public void addProductToCartTest() {
        login(USERNAME, PASSWORD);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.id("add-to-cart-sauce-labs-backpack"))).click();

        WebElement cartBadge = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.className("shopping_cart_badge")));

        Assert.assertEquals(
                cartBadge.getText(),
                "1",
                "Cart should contain one product."
        );

        driver.findElement(By.className("shopping_cart_link")).click();

        Assert.assertTrue(
                wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.className("cart_item"))).isDisplayed(),
                "The selected product should appear in the cart."
        );
    }

    @Test
    public void checkoutTest() {
        login(USERNAME, PASSWORD);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.id("add-to-cart-sauce-labs-backpack"))).click();

        driver.findElement(By.className("shopping_cart_link")).click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.id("checkout"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("first-name"))).sendKeys("QA");

        driver.findElement(By.id("last-name")).sendKeys("Tester");
        driver.findElement(By.id("postal-code")).sendKeys("411001");
        driver.findElement(By.id("continue")).click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.id("finish"))).click();

        WebElement confirmation = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.className("complete-header")));

        Assert.assertEquals(
                confirmation.getText(),
                "Thank you for your order!",
                "Checkout should show an order confirmation."
        );
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

