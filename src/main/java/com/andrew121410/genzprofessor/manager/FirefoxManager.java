package com.andrew121410.genzprofessor.manager;

import com.andrew121410.genzprofessor.GenZProfessor;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Deprecated
public class FirefoxManager extends Thread {

    private GenZProfessor genZProfessor;
    private WebDriver driver;
    private File tempFolder;
    private boolean isRunning;

    public FirefoxManager(GenZProfessor genZProfessor) {
        this.genZProfessor = genZProfessor;
        this.tempFolder = new File("cache");
        if (this.tempFolder.exists()) {
            for (File file : this.tempFolder.listFiles()) file.delete();
        }
        this.tempFolder.mkdir();
    }

    public void setup() {
        WebDriverManager.firefoxdriver().setup();
        FirefoxBinary firefoxBinary = new FirefoxBinary();
//        firefoxBinary.addCommandLineOptions("--headless");
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setBinary(firefoxBinary);
        File firefoxProfileFile = new File("firefox.profile");
        if (!firefoxProfileFile.exists()) firefoxProfileFile.mkdir();
        FirefoxProfile firefoxProfile = new FirefoxProfile(firefoxProfileFile);
        firefoxProfile.setPreference("dom.webdriver.enabled", false);
        firefoxProfile.setPreference("useAutomationExtension", false);
//        firefoxProfile.setPreference("general.useragent.override", "Mozilla/5.0 (Windows NT 10.0; Win64; X64) AppleWebKit/537.36 (KHTML, Like Gecko) Chrome/85.0.4183.121 Safari/537.36 Edg/85.0.564.63");
        firefoxOptions.setProfile(firefoxProfile);
        this.driver = new FirefoxDriver(firefoxOptions);
        this.driver.manage().deleteAllCookies();
        loginIntoToChegg();
    }

    private void loginIntoToChegg() {
        String email = this.genZProfessor.getConfigManager().getMainConfig().getCheggEmail();
        String password = this.genZProfessor.getConfigManager().getMainConfig().getCheggPassword();

        load("https://www.chegg.com/auth?action=login");
        WebElement emailElement = this.driver.findElement(By.id("emailForSignIn"));
        WebElement passwordElement = this.driver.findElement(By.id("passwordForSignIn"));

        emailElement.click();
        emailElement.clear();
        for (int i = 0; i < email.length(); i++) {
            emailElement.sendKeys(String.valueOf(email.charAt(i)));
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        passwordElement.click();
        passwordElement.clear();
        for (int i = 0; i < password.length(); i++) {
            passwordElement.sendKeys(String.valueOf(password.charAt(i)));
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        List<WebElement> buttonList = this.driver.findElements(By.tagName("button"));
        WebElement realButton = null;
        for (WebElement webElement : buttonList) {
            if (webElement.getAttribute("type").equalsIgnoreCase("submit") && webElement.getAttribute("name").equalsIgnoreCase("login") && webElement.getAttribute("class").contains("login-button")) {
                realButton = webElement;
            }
        }
        if (realButton == null) {
            throw new NullPointerException("Couldn't find login button when logging in");
        }
        realButton.click();
        //Debug
        GenZProfessor.getInstance().getJda().getGuildById("724458074688979046").getTextChannelById("724458075506868296").sendFile(savePicture()).queue();
    }

    public void quit() {
        if (this.driver != null) {
            this.driver.quit();
        }
    }

    private void load(String link) {
        driver.get(link);
        waitForLoad(driver);
    }

    private File savePicture() {
//        WebElement webElement = this.driver.findElement(new By.ByTagName("body"));
        File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        return file;
    }

    private List<File> getAllImages() {
        WebElement answersListElement = this.driver.findElement(By.className("answers-list"));
        List<String> urls = answersListElement.findElements(By.tagName("img")).stream().map(webElement -> webElement.getAttribute("src")).collect(Collectors.toList());
        List<File> files = new ArrayList<>();
        int a = 0;
        for (String url : urls) {
            try {
                URL urlObject = new URL(url);
                BufferedImage saveImage = ImageIO.read(urlObject);
                File file = new File(this.tempFolder, a + Instant.now().getNano() + ".png");
                ImageIO.write(saveImage, "png", file);
                files.add(file);
                a++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return files;
    }

    private File getAnswerHtml() {
        WebElement webElement = this.driver.findElement(By.className("answers-list"));
        if (webElement == null) {
            System.out.println("webElement answer-body was null");
            return null;
        }
        File file = new File(this.tempFolder, "answer.html");
        String htmlCode = webElement.getAttribute("innerHTML");
        if (htmlCode == null) throw new NullPointerException("innerHTML is null");
        try (FileWriter fileWriter = new FileWriter(file)) {
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(htmlCode);
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public void processLink(String link, Consumer<List<File>> consumer) {
        this.isRunning = true;
        load(link);
        List<File> files = new ArrayList<>(getAllImages());
        File file = getAnswerHtml();
        if (file != null) files.add(file);
        consumer.accept(files);
        this.isRunning = false;
    }

    void waitForLoad(WebDriver driver) {
        new WebDriverWait(driver, 30).until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
    }

    public boolean isRunning() {
        return isRunning;
    }
}
