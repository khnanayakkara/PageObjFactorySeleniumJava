package base;

import constants.Constant;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utilities.AllureUtil;
import utilities.DbManager;
import utilities.MonitoringMail;
import utilities.ScreenshotSoftAssert;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Properties;
import java.util.function.Function;

public class BasePage {


    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    public String browser;

    public WebDriverWait wait;
    public ScreenshotSoftAssert softAssert;

    protected static final Logger log = LogManager.getLogger(BasePage.class);
    public MonitoringMail mail = new MonitoringMail();
    private static Properties config = new Properties();
    public Pages pages;
    private String runMode;

    @BeforeMethod(alwaysRun = true)
    public void setup(Method method, ITestContext context) {

        // load config file for browser and url
        config = loadConfigFile(Constant.CONFIG_FILE_LOCATION);

        // to get browser name from the jenkins job parameters else from the property file
        if (System.getenv("browser") != null && !System.getenv("browser").isEmpty()) {
            browser = System.getenv("browser");
        } else {
            browser = config.getProperty("browser");
        }
        // get run mode

        if (System.getenv("runmode") != null && !System.getenv("runmode").isEmpty()) {
            runMode = System.getenv("runmode");
        } else {
            runMode = config.getProperty("runmode");
        }

        // Init driver
        WebDriver localDriver;
        if (runMode.equalsIgnoreCase("grid")) {
            localDriver = initGridDriver(browser);
        } else {
            localDriver = initLocalDriver(browser);
        }
        setDriver(localDriver);
        context.setAttribute("driver", getDriver());
        AllureUtil.attachBrowserDetailsAndEnvFile(getDriver(),runMode);

        //load page objects
        pages = new Pages();

        // init webDriver wait
        wait = new WebDriverWait(getDriver(), Duration.ofSeconds(Constant.ELEMENT_LOAD_TIMEOUT));
        log.info("WebDriver wait default time set to : " + Constant.ELEMENT_LOAD_TIMEOUT);

        // load softAssert
        softAssert = new ScreenshotSoftAssert(getDriver());

        // load web url
        getDriver().get(config.getProperty("testsiteurl"));
        log.info("Navigated to : " + config.getProperty("testsiteurl"));

        getDriver().manage().window().maximize();

        // connect to database
        connectDB();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {

        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            log.error("Soft assert failures occurred: " + e.getMessage());
            throw e;
        } finally {
            WebDriver localDriver = getDriver();
            if (localDriver != null) {
                log.info("Quitting driver for thread: " + Thread.currentThread().getId());
                localDriver.quit();
                driver.remove();
            } else {
                log.warn("Driver was already null for thread: " + Thread.currentThread().getId());
            }
            log.info("Test execution completed for thread: " + Thread.currentThread().getId());
        }
    }

    public WebDriver getDriver() {
        return driver.get();
    }

    public void setDriver(WebDriver driverInstance) {
        driver.set(driverInstance);
    }

    private WebDriver initLocalDriver(String brow) {

        WebDriver webDriver = null;
        if (brow.equals("chrome")) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--disable-notifications");
            options.addArguments("--disable-save-password-bubble");
            options.addArguments("--incognito");
            webDriver = new ChromeDriver(options);
            log.info("chrome launched !!! ");

        } else if (brow.equalsIgnoreCase("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            webDriver = new FirefoxDriver();
            log.info("firefox launched !!! ");

        } else if (brow.equalsIgnoreCase("edge")) {
            WebDriverManager.edgedriver().setup();
            webDriver = new EdgeDriver();
            log.info("edge launched !!! ");

        } else if (brow.equalsIgnoreCase("safari")) {
            // No setup needed for SafariDriver
            webDriver = new SafariDriver();
            log.info("safari launched !!! ");

        } else if (brow.equalsIgnoreCase("opera")) {
            WebDriverManager.operadriver().setup();
            ;
            log.info("opera launched !!! ");
        } else if (brow.equalsIgnoreCase("ie")) {
            // No setup needed for SafariDriver
            WebDriverManager.iedriver().setup();
            webDriver = new InternetExplorerDriver();
            log.info("internet explorer launched !!! ");
        }

        return webDriver;
    }

    private WebDriver initGridDriver(String brow) {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        if (brow.equalsIgnoreCase("chrome")) {
            capabilities.setBrowserName("chrome");
        } else if (brow.equalsIgnoreCase("firefox")) {
            capabilities.setBrowserName("firefox");
        } else if (brow.equalsIgnoreCase("edge")) {
            capabilities.setBrowserName("MicrosoftEdge");
        } else {
            throw new RuntimeException("Browser not supported for Grid: " + brow);
        }

        try {
            String gridUrl = config.getProperty("gridUrl");
            RemoteWebDriver remoteDriver = new RemoteWebDriver(new URL(gridUrl), capabilities);
            log.info(brow + " launched on Selenium Grid at: " + gridUrl);
            return remoteDriver;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Grid URL is invalid or Selenium Grid not running");
        }
    }

    private Properties loadConfigFile(String filePath) {

        try {
            Properties configFile = new Properties();
            FileInputStream fis = new FileInputStream(filePath);
            configFile.load(fis);
            log.info("config file loaded !!! ");
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

    /**
     * Generic fluent wait for an element.
     *
     * @param element      web element
     * @param timeoutSec   total timeout in seconds
     * @param pollingSec   polling interval in seconds
     * @return WebElement when it becomes visible and interactable
     */
    public WebElement fluentWait(WebElement element, int timeoutSec, int pollingSec) {
        FluentWait<WebDriver> wait = new FluentWait<>(getDriver())
                .withTimeout(Duration.ofSeconds(timeoutSec))
                .pollingEvery(Duration.ofSeconds(pollingSec))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);

        return wait.until(new Function<WebDriver, WebElement>() {
            @Override
            public WebElement apply(WebDriver driver) {
                if (element.isDisplayed() && element.isEnabled()) {
                    return element;
                }
                return null;
            }
        });
    }

}
