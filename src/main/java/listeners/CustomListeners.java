package listeners;

import base.BasePage;
import jakarta.mail.MessagingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utilities.AllureUtil;
import utilities.MonitoringMail;
import utilities.TestConfig;
import utilities.TestUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;

public class CustomListeners extends BasePage implements ITestListener, ISuiteListener {

    public Log log = LogFactory.getLog(CustomListeners.class);

    public void onTestFailure(ITestResult result) {

        log.error("❌ Test FAILED: " + result.getName());
        WebDriver driver = (WebDriver) result.getTestContext().getAttribute("driver");
        AllureUtil.captureScreenShot(driver);
    }

    public void onTestSuccess(ITestResult result) {
        log.info("✅ Test PASSED: " + result.getName());
    }

    public void onTestSkipped(ITestResult result) {
        log.warn("⚠️ Test SKIPPED: " + result.getName());
    }

    public void onStart(ISuite suite) {
        log.info("🚀 Suite started: " + suite.getName());
        TestUtil.deleteDir("allure-results");
        TestUtil.deleteDir("allure-report");
        log.info("Allure folders cleaned!");
    }

    public void onFinish(ISuite suite) {
        log.info("🏁 Suite finished: " + suite.getName());

        // Generate allure report
        log.info("Generating Allure Report...");
        generateAllureReport();

        // send email
//        generateEmail();

    }

    private boolean isAllureAvailable() {
        try {
            ProcessBuilder pb = new ProcessBuilder("allure", "--version");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            log.warn("Allure CLI check failed: " + e.getMessage());
            return false;
        }

    }

    private void generateEmail() {
        MonitoringMail mail = new MonitoringMail();
        String messageBody;
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            String projectName = Paths.get("").toAbsolutePath().getFileName().toString();
            messageBody = "Allure Report: http://" + hostAddress + "/" + projectName + "/allure-report/index.html";
            log.info("Email message body: " + messageBody);
        } catch (UnknownHostException e) {
            throw new RuntimeException("Failed to get host address for email body", e);
        }

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
            log.info("📧 Email sent successfully!");
        } catch (MessagingException e) {
            log.error("Failed to send email: ", e);
        }
    }

    private void generateAllureReport() {

        if (!isAllureAvailable()) {
            log.warn("⚠️ Allure CLI not found! Skipping report generation.");
            return;
        }

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("allure", "generate", "--clean");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("[Allure CLI] " + line);
            }
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                String reportUrl = "http://" + InetAddress.getLocalHost().getHostAddress() +
                        ":63342/" + Paths.get("").toAbsolutePath().getFileName() + "/allure-report/index.html";
                log.info("✅ Allure report generated successfully!");
                log.info("📄 Allure Report: " + reportUrl);
            } else {
                log.info("❌ Allure report generation failed. Exit code: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
