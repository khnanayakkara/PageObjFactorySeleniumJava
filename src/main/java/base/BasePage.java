package base;

import constants.Constant;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import utilities.DbManager;
import utilities.MonitoringMail;
import utilities.ScreenshotSoftAssert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Properties;

public class BasePage {


    public WebDriver driver;
    public String browser;

    public WebDriverWait wait;
    public ScreenshotSoftAssert softAssert;

    protected static final Logger log = LogManager.getLogger(BasePage.class);
    public MonitoringMail mail = new MonitoringMail();
    private static Properties config = new Properties();
    public Pages pages;

    @BeforeSuite
    public void cleanFolders() {
        deleteDir("allure-results");
        deleteDir("allure-report");
        log.info("Allure folders cleaned!");
    }

    @BeforeMethod(alwaysRun = true)
    public void setup(Method method, ITestContext context) {

        if (driver == null) {

            // load config file for browser and url
            config = loadConfigFile(Constant.CONFIG_FILE_LOCATION);

            // to get browser name from the jenkins job parameters else from the property file
            if (System.getenv("browser") != null && !System.getenv("browser").isEmpty()) {
                browser = System.getenv("browser");
            } else {
                browser = config.getProperty("browser");
            }

            // load webDriver based on the browser
            driver = initBrowser(browser);
            context.setAttribute("driver", driver);

            //load page objects
            pages = new Pages(driver);

            // init webDriver wait
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            log.debug("WebDriver wait default time set to : " + Constant.ELEMENT_LOAD_TIMEOUT);

            // load softAssert
            softAssert = new ScreenshotSoftAssert(driver);

            // load web url
            driver.get(config.getProperty("testsiteurl"));
            log.debug("Navigated to : " + config.getProperty("testsiteurl"));

            driver.manage().window().maximize();

            // connect to database
            connectDB();
        }

    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {

        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            log.error("Soft assert failures occurred: " + e.getMessage());
            throw e; // Re-throw so TestNG marks test as failed
        } finally {
            if (driver != null) {
                driver.quit();
                driver = null;
            }
            log.debug("Test execution completed!!!");
        }
    }

    private WebDriver initBrowser(String brow) {

        WebDriver webDriver = null;

        if (brow.equals("chrome")) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--disable-notifications");
            options.addArguments("--disable-save-password-bubble");
            options.addArguments("--incognito");
            webDriver = new ChromeDriver(options);
            log.debug("chrome launched !!! ");

        } else if (brow.equalsIgnoreCase("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            webDriver = new FirefoxDriver();
            log.debug("firefox launched !!! ");

        } else if (brow.equalsIgnoreCase("edge")) {
            WebDriverManager.edgedriver().setup();
            webDriver = new EdgeDriver();
            log.debug("edge launched !!! ");

        } else if (brow.equalsIgnoreCase("safari")) {
            // No setup needed for SafariDriver
            webDriver = new SafariDriver();
            log.debug("safari launched !!! ");

        } else if (brow.equalsIgnoreCase("opera")) {
            WebDriverManager.operadriver().setup();
            ;
            log.debug("opera launched !!! ");
        } else if (brow.equalsIgnoreCase("ie")) {
            // No setup needed for SafariDriver
            WebDriverManager.iedriver().setup();
            webDriver = new InternetExplorerDriver();
            log.debug("internet explorer launched !!! ");
        }

        return webDriver;
    }

    private Properties loadConfigFile(String filePath) {

        try {
            Properties configFile = new Properties();
            FileInputStream fis = new FileInputStream(filePath);
            configFile.load(fis);
            log.debug("config file loaded !!! ");
            return configFile;

        } catch (IOException e) {
            throw new RuntimeException("Failed to load config files: " + e.getMessage(), e);
        }
    }

    private void connectDB() {
        try {
            DbManager.setMysqlDbConnection();
            log.info("DB Connection established !!!");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private void deleteDir(String filePath) {

        File dir = new File(filePath);
        try {
            FileUtils.deleteDirectory(dir);
            log.info("Deleted directory: " + dir.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void click(WebElement element) {

        try {
            log.info("Clicking on an Element : " + element);
            element.click();
        } catch (Throwable t) {

            log.error("Error while Clicking on an Element : " + element + " error message : " + t.getMessage());
            Assert.fail(t.getMessage());

        }
    }

    public void type(WebElement element, String value) {

        try {
            element.sendKeys(value);
            log.info("typing in an Element : " + element + " entered the value as : " + value);
        } catch (Throwable t) {
            log.error("Error while typing in an Element : " + element + " error message : " + t.getMessage());
            Assert.fail(t.getMessage());

        }

    }

    public void select(WebElement element, String value) {

        try {
            Select select = new Select(element);
            select.selectByVisibleText(value);
            log.info("Selecting an Element : " + element + " selected the value as : " + value);
        } catch (Throwable t) {

            log.error("Error while selecting an Element : " + element + " error message : " + t.getMessage());
            Assert.fail(t.getMessage());

        }

    }

}
