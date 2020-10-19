package com.andrew121410.genzprofessor.manager;

import com.andrew121410.genzprofessor.objects.ACheggRequest;
import com.andrew121410.genzprofessor.objects.CheggRequestResult;
import io.github.bonigarcia.wdm.WebDriverManager;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class FirefoxManager {

    private CheggRequestManager cheggRequestManager;

    private BrowserMobProxy browserMobProxy;
    private FirefoxDriver firefoxDriver;

    public FirefoxManager(CheggRequestManager cheggRequestManager) {
        this.cheggRequestManager = cheggRequestManager;
        setupSelenium();
    }

    private void setupProxy() {
        BrowserMobProxy proxy = new BrowserMobProxyServer();
        proxy.addHeaders(this.cheggRequestManager.createHeaders());
        proxy.start(0);
    }

    private void setupSelenium() {
        WebDriverManager.firefoxdriver().setup();
        setupProxy();
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(this.browserMobProxy);
        FirefoxBinary firefoxBinary = new FirefoxBinary();
//        firefoxBinary.addCommandLineOptions("--headless");
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setProxy(seleniumProxy);
        firefoxOptions.setBinary(firefoxBinary);
        File firefoxProfileFile = new File("firefox.profile");
        if (!firefoxProfileFile.exists()) firefoxProfileFile.mkdir();
        FirefoxProfile firefoxProfile = new FirefoxProfile(firefoxProfileFile);
        firefoxProfile.setPreference("dom.webdriver.enabled", false);
        firefoxProfile.setPreference("useAutomationExtension", false);
        firefoxProfile.setPreference("general.useragent.override", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36");
        firefoxOptions.setProfile(firefoxProfile);
        this.firefoxDriver = new FirefoxDriver(firefoxOptions);
        this.firefoxDriver.manage().deleteAllCookies();
    }

    public CheggRequestResult processLink(ACheggRequest aCheggRequest) {
        load(aCheggRequest.getLink());
        List<File> files = Collections.singletonList(savePicture());
        quit();
        return new CheggRequestResult(files, CheggRequestResult.Result.SUCCESS);
    }

    private void load(String link) {
        firefoxDriver.get(link);
        waitForLoad(firefoxDriver);
    }

    private void waitForLoad(WebDriver driver) {
        new WebDriverWait(driver, 30).until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
    }

    private File savePicture() {
        WebElement webElement = this.firefoxDriver.findElement(new By.ByTagName("body"));
//        File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        return webElement.getScreenshotAs(OutputType.FILE);
    }

    public void quit() {
        if (this.firefoxDriver != null) {
            this.browserMobProxy.stop();
            this.firefoxDriver.manage().deleteAllCookies();
            this.firefoxDriver.quit();
        }
    }
}
