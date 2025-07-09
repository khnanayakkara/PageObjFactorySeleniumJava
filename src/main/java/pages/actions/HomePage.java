package pages.actions;

import base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.locators.HomePageLocators;

public class HomePage extends BasePage {

    public HomePageLocators home;

    public HomePage(WebDriver driver) {
        super.driver = driver;
        this.home = new HomePageLocators();
        AjaxElementLocatorFactory factory = new AjaxElementLocatorFactory(driver, 10);
        PageFactory.initElements(factory, this.home);
    }

    public void gotoFlights() {
        click(home.loginWithFb);
    }
}
