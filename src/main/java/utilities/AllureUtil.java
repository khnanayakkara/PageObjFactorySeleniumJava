package utilities;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class AllureUtil{

    @Attachment(value = "Failure Screenshot", type = "image/png")
    public static void captureScreenShot(WebDriver driver) {
        byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Allure.addAttachment("Screenshot " + new Date().toString().replace(":", "_").replace(" ", "_") , new ByteArrayInputStream(screenshotBytes));
    }

    public static void attachBrowserDetailsAndEnvFile(WebDriver driver, String runMode) {
        try {
            Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
            String browserName = caps.getBrowserName();
            String browserVersion = caps.getBrowserVersion();
            String platform = String.valueOf(caps.getPlatformName());

            // Attach to Allure as attachments
            Allure.addAttachment("Browser Name", browserName);
            Allure.addAttachment("Browser Version", browserVersion);
            Allure.addAttachment("Platform", platform);
            Allure.addAttachment("Run Mode",runMode);

            // Create environment.properties file for Allure
            createAllureEnvironmentFile(browserName, browserVersion, platform,runMode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createAllureEnvironmentFile(String browserName, String browserVersion, String platform, String runMode) {
        Properties props = new Properties();
        props.setProperty("Browser", browserName);
        props.setProperty("Browser Version", browserVersion);
        props.setProperty("Platform", platform);
        props.setProperty("Run Mode", runMode);

        try {
            props.store(new FileWriter("allure-results/environment.properties"), "Allure Environment Properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
