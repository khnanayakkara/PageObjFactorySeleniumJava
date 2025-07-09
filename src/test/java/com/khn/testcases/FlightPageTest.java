package com.khn.testcases;

import base.BasePage;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.actions.HomePage;

public class FlightPageTest extends BasePage{

    @Test
    public void clickonFlght(){
        HomePage home = new HomePage(driver);
        home.gotoFlights();
        softAssert.assertEquals(1,1);
        softAssert.assertEquals(2,3);
        Assert.assertEquals("a","b");
        softAssert.assertEquals(3,3);
    }
}
