package pages.locators;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;

public class HomePageLocators {

    @FindBy(css = "span.xvs91rp.xwhw2v2")
    public WebElement loginWithFb;

    // find elemnt with either locators
    @FindAll({
            @FindBy(xpath = "//span[normalize-space(text())='Hotels']"),
            @FindBy(id = "Hotels"),
    })
    public WebElement hotelTab;

    // find elemnt with first and find the send one in it
    @FindBys({
            @FindBy(xpath = "//span[normalize-space(text())='Cars']"),
            @FindBy(id = "Hotels"),
    })
    public WebElement carsTab;


}
