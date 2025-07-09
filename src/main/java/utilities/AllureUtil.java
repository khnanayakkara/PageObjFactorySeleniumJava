package utilities;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.util.Date;

public class AllureUtil {

    @Attachment(value = "Failure Screenshot", type = "image/png")
    public static void captureScreenShot(WebDriver driver) {
        byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Allure.addAttachment("Screenshot " + new Date().toString().replace(":", "_").replace(" ", "_") , new ByteArrayInputStream(screenshotBytes));
    }

}
