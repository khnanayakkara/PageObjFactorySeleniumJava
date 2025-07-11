package com.khn.testcases;

import base.BasePage;
import org.testng.annotations.Test;
import utilities.DataUtil;
import utilities.JsonFile;

import java.util.HashMap;

public class LoginTest extends BasePage {


    @Test(dataProvider = "testData", dataProviderClass = DataUtil.class)
    @JsonFile("src/test/resources/testData/incorrectUserNamePassword.json")
    public void verifyIncorrectUernameAndPassword(HashMap<String, String> data) {
        pages.loginPage.enterUsername(data.get("username"));
        pages.loginPage.enterPassword(data.get("password"));
        pages.loginPage.clickLoginButton();
        pages.loginPage.verifyErrorMessage(data.get("errorMsg"));
    }

    @Test(dataProvider = "testData", dataProviderClass = DataUtil.class)
    @JsonFile("src/test/resources/testData/missingUsernameAndPassword.json")
    public void missingUsernameAndPassword(HashMap<String, String> data) {
        pages.loginPage.enterUsername(data.get("username"));
        pages.loginPage.enterPassword(data.get("password"));
        pages.loginPage.verifyLoginButtonDisabled();

    }
}