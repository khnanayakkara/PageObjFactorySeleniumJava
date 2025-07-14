package utilities;

import base.BasePage;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

public class TestUtil extends BasePage {
    public static String screenShotPath;
    public static String screenShotName;

    public void captureScreenShotElementHighlight(WebElement webElement) {
        // highlight web element in the page
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("arguments[0].style.border='3px solid red'", webElement);

        // Capture screenshot of the page
        Date date = new Date();
        File pageScreenShotCommon = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
        try {
            screenShotPath = "./target/surefire-reports";
            screenShotName = new Date().toString().replace(":", "_").replace(" ", "_") + ".jpg";
            FileUtils.copyFile(pageScreenShotCommon, new File(screenShotPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void deleteDir(String filePath) {

        File dir = new File(filePath);
        try {
            FileUtils.deleteDirectory(dir);
            log.info("Deleted directory: " + dir.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
