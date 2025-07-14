package pages.actions;

import base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.locators.LoginPageLocators;

import java.time.Duration;

public class LoginPage extends BasePage {

    public LoginPageLocators login;

    public LoginPage() {
        this.login = new LoginPageLocators();
        PageFactory.initElements(getDriver(), this.login);
    }

    public void enterUsername(String userName) {
        log.info("enterUsername: " + userName);
        type(login.username, userName);

    }

    public void enterPassword(String password) {
        log.info("enterPassword: " + password);
        type(login.password, password);
    }

    public void clickLoginButton() {
        log.info("clickLoginButton");
        click(login.loginBtn);
    }

    public void verifyErrorMessage(String loginError) {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOf(login.errorMsg));
        Assert.assertEquals( login.errorMsg.getText(),loginError);
    }

    public void verifyLoginButtonDisabled() {
        Assert.assertFalse(login.loginBtn.isEnabled());
    }
}
