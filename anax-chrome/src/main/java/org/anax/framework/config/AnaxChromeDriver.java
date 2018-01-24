package org.anax.framework.config;

import org.anax.framework.configuration.AnaxDriver;
import org.anax.framework.controllers.WebController;
import org.anax.framework.controllers.WebDriverWebController;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;


@Configuration
public class AnaxChromeDriver {

    @Value("${anax.target.url:http://www.google.com}")
    String targetUrl;
    @Value("${anax.remote.host:NOT_CONFIGURED}")
    String remoteHost;
    @Value("${anax.remote.port:NOT_CONFIGURED}")
    String remotePort;
    @Value("${anax.chrome.startsMaximized:true}")
    Boolean startMaximized;


    @ConditionalOnMissingBean
    @Bean
    public AnaxDriver defaultAnaxDriver(@Value("${anax.localdriver:true}") Boolean useLocal) {
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        ChromeOptions options;

        if (useLocal) {

            ChromeDriverService service = new ChromeDriverService.Builder().build();
            options = new ChromeOptions();
            if (startMaximized) {
                String x = (System.getProperty("os.name").toLowerCase().contains("mac")) ? "--start-fullscreen" : "--start-maximized";
                options.addArguments(x);
            }
            options.merge(capabilities);

            return () -> {
                ChromeDriver driver = new ChromeDriver(service, options);
                driver.get(targetUrl);
                return driver;
            };
        } else {
             // adds screenshot capability to a default webdriver.
            return () -> {
                Augmenter augmenter = new Augmenter();
                augmenter.augment(new RemoteWebDriver(
                    new URL("http://" + remoteHost + ":" + remotePort + "/wd/hub"),
                    capabilities));
                WebDriver driver = (WebDriver) augmenter;
                driver.get(targetUrl);
                return driver;
            };
        }
    }


}
