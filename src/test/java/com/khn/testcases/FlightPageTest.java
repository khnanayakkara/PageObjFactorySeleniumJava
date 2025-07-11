package com.khn.testcases;

import base.BasePage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;
import utilities.DataUtil;
import utilities.JsonFile;

import java.util.HashMap;

public class FlightPageTest extends BasePage{


    @Test(dataProvider = "testData", dataProviderClass = DataUtil.class)
    @JsonFile("src/test/resources/testData/loginData.json")
    public void loginTest(HashMap<String, String> data) {

        pages.loginPage.enterUsername(data.get("username"));
        pages.loginPage.enterPassword(data.get("password"));
        pages.loginPage.clickLoginButton();
        pages.loginPage.verifyErrorMessage(data.get("errorMsg"));

    }
}
