package base;

import org.openqa.selenium.WebDriver;
import pages.actions.LoginPage;

public class Pages {

    public LoginPage loginPage;

    public Pages(){
        loginPage = new LoginPage();
    }
}
