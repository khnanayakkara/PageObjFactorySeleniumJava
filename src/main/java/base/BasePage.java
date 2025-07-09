package base;

import io.github.bonigarcia.wdm.WebDriverManager;
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
import utilities.DbManager;
import utilities.ExcelReader;
import utilities.MonitoringMail;
import utilities.ScreenshotSoftAssert;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Properties;

public class BasePage {


    //Webdriver - donw
//     Properties - done
//     Logs
//     ExtentReports
//     DB
//     Excel
//     Mail
//     jenkins

    public WebDriver driver;
    private static Properties config = new Properties();
    private FileInputStream fis;
    public static ExcelReader excel = new ExcelReader("src/test/resources/excel/TestData.xlsx");
    public WebDriverWait wait;
    public WebElement dropdown;
    protected static final Logger log = LogManager.getLogger(BasePage.class);
    public String browser;
    public MonitoringMail mail = new MonitoringMail();
    public ScreenshotSoftAssert softAssert;


    @BeforeMethod
    public void setup(Method method, ITestContext context) {

        if (driver == null) {

            // load config file for browser and url
            config = loadConfigFile();

            // to get browser name from the jenkins job parameters else from the property file
            if (System.getenv("browser") != null && !System.getenv("browser").isEmpty()) {
                browser = System.getenv("browser");
            } else {
                browser = config.getProperty("browser");
            }

            // load webdriver based on the browser
            driver = initBrowser(browser);
            context.setAttribute("driver", driver);

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
    @AfterMethod
    public void tearDown() {

        // get softAssert output
        softAssert.assertAll();

        if (driver != null) {
            driver.quit();
        }
        log.debug("test execution completed !!!");
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

    private Properties loadConfigFile() {

        try {
            Properties configFile = new Properties();
            fis = new FileInputStream(Constant.configFileLocation);
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
