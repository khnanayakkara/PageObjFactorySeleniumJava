package listeners;

import base.BasePage;
import jakarta.mail.MessagingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.testng.*;
import utilities.AllureUtil;
import utilities.MonitoringMail;
import utilities.TestConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;

public class CustomListeners extends BasePage implements ITestListener, ISuiteListener {

    public Log log = LogFactory.getLog(CustomListeners.class);

    public void onTestFailure(ITestResult result) {

        log.error("Test failed: " + result.getName());
        Reporter.log("Capturing screenshot...");

        WebDriver driver = (WebDriver) result.getTestContext().getAttribute("driver");
        AllureUtil.captureScreenShot(driver);
    }

    public void onTestSuccess(ITestResult result) {
    }

    public void onFinish(ISuite suite) {
        // Generate allure report
        generateAllureReport();
        // send email
//        generateEmail();

    }

    public void generateEmail() {
        MonitoringMail mail = new MonitoringMail();
        String messageBody = null;
        try {
            ;
//            messageBody = "http://" + InetAddress.getLocalHost().getHostAddress() + ":63342/SeleniumTestNG/selenium-testng/allure-report/index.html";
            messageBody = "Allure Report: http://" + InetAddress.getLocalHost().getHostAddress() + java.nio.file.Paths.get("").toAbsolutePath().getFileName()+"/selenium-testng/allure-report/index.html?";
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        System.out.println(messageBody);

        try {
            mail.sendMail(
                    TestConfig.server,
                    TestConfig.from,
                    TestConfig.to,
                    TestConfig.subject,
                    messageBody,
                    TestConfig.attachmentPath,
                    TestConfig.attachmentName
            );
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void generateAllureReport() {
        log.debug("Generating Allure Report...!!!");

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("allure", "generate", "--clean");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                log.debug("[Allure CLI] " + line);
            }
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                log.debug("Allure report generated successfully...!!!");
                log.debug("Allure Report : http://" + InetAddress.getLocalHost().getHostAddress() + ":63342/" + Paths.get("").toAbsolutePath().getFileName() + "/allure-report/index.html");
            } else {
                log.debug("Allure report generation failed. Exit code: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
