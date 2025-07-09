package com.khn.testcases;

import base.BasePage;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.actions.HomePage;
import utilities.DataUtil;
import utilities.JsonFile;

import java.util.HashMap;
import java.util.Map;

public class FlightPageTest extends BasePage{

    @Test(dataProvider = "userData", dataProviderClass = DataUtil.class)
    @JsonFile("src/test/resources/testData/loginTestdata.json")
    public void loginTest(HashMap<String, String> data) {
        System.out.println("Username: " + data.get("username"));
        System.out.println("Password: " + data.get("password"));
        System.out.println("Role: " + data.get("role"));
//
//        HomePage home = new HomePage(driver);
//        home.gotoFlights();
//        softAssert.assertEquals(1,1);
//        softAssert.assertEquals(2,3);
//        Assert.assertEquals("a","b");
//        softAssert.assertEquals(3,3);
    }
}
