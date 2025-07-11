package pages.locators;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;

public class LoginPageLocators {

    @FindBy(xpath = "//form[@id='loginForm']//input[@name='username']")
    public WebElement username;

    @FindBys({
            @FindBy(id = "loginForm"),
            @FindBy(name = "password"),
    })
    public WebElement password;

   @FindBy(xpath = "//div[contains(text(),'Log in')]/parent::button")
   public WebElement loginBtn;

    // find elemnt with either locators
    @FindAll({
            @FindBy(xpath = "//span[normalize-space(text())='Hotels']"),
            @FindBy(id = "Hotels"),
    })
    public WebElement hotelTab;

    // find element with first and find the send one in it
    @FindBys({
            @FindBy(xpath = "//span[normalize-space(text())='Cars']"),
            @FindBy(id = "Hotels"),
    })
    public WebElement carsTab;

    @FindBy(css = "#loginForm span div")
    public WebElement errorMsg;



}
