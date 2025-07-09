package utilities;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.asserts.SoftAssert;

import java.io.ByteArrayInputStream;
import java.util.Date;

public class ScreenshotSoftAssert extends SoftAssert {

    private WebDriver driver;

    public ScreenshotSoftAssert(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public void onAssertFailure(org.testng.asserts.IAssert<?> assertCommand, AssertionError ex) {
        if (driver != null) {
            byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Screenshot-softAssert " + new Date().toString().replace(":", "_").replace(" ", "_"),  new ByteArrayInputStream(screenshotBytes));
        }
        super.onAssertFailure(assertCommand, ex);
    }
}