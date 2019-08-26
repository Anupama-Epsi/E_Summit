
package com.stepIn.testCases;
/*import com.citi.testCase.ChromeDriver;
import com.citi.testCase.TakesScreenshot;
import com.citi.testCase.WebDriver;*/
//import com.stepIn.pages.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;




public class TestCase2{
	
	String videoresponse;
	
 @Test
  public void teststep1() {
	  System.out.println("test step1 executed");
	  String Url = "https://www.youtube.com";
		String browserName = "Chrome";
		String chromeDriverPath = System.getProperty("user.dir") + "\\chromedriver.exe";
		System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, chromeDriverPath);
		WebDriver driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(5000, TimeUnit.MILLISECONDS);
		driver.get(Url);
		WebDriverWait explicitWait = new WebDriverWait(driver, 60);
		explicitWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id=\"masthead-container\"]//div[@id=\"logo-icon-container\"]")));
		//search
		String searchTerm = "step-inforum";
		driver.findElement(By.xpath("//input[@id=\"search\"]")).sendKeys(searchTerm);
		driver.findElement(By.xpath("//button[@id=\"search-icon-legacy\"]")).click();
		String channelName = "step-in forum";
		WebElement channel = null;
		List<WebElement> channels = driver.findElements(By.xpath("//h3[@id=\"channel-title\"]"));
		for(WebElement tempChannel: channels) {
			if(tempChannel.getText().toLowerCase().equals(channelName.toLowerCase())) {
				channel = tempChannel;
				break;
			}
		}
		channel.click();
		WebElement tab = null;
		List<WebElement> tabs = driver.findElements(By.xpath("//div[@id=\"tabsContent\"]//paper-tab"));
		for(WebElement tempTab:tabs){
			if (tempTab.getText().toUpperCase().equals("VIDEOS")) {
				tab = tempTab;
				tab.click();
			}
		}
		 try {
			 
				

				URL url = new URL("http://54.169.34.162:5252/video");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");

				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}

				BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

				String output;
				while ((output = br.readLine()) != null) {
					//System.out.println(output);
					 videoresponse = output;
				}

				conn.disconnect();

			  } catch (MalformedURLException e) {

				e.printStackTrace();

			  } catch (IOException e) {

				e.printStackTrace();

			  }

		 String xpath1="//a[contains(text()"+
					","+
					"\""+
					videoresponse+
					"\"" +
					")]";
		 System.out.println(xpath1);
	        List<WebElement> element = driver.findElements(By.xpath(xpath1));
	       // System.out.println(xpath1);
	        if(element!=null)
	        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
	  //Once video will be scroll down to center the page take screenshot 
		/*
		 * System.setProperty("webdriver.chrome.driver",
		 * "Utils/Drivers/chromedriver.exe"); WebDriver driver = new ChromeDriver();
		 * driver.get("http://www.google.com/");
		 */
		File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		
		try {
			FileUtils.copyFile(scrFile, new File("testNgMavenExample\\screenshots\\screenshot.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Need the get method response from random video and click on video link 
		WebDriverWait wait = new WebDriverWait(driver, 30);
		/*
		 * String randomVideoName="";
		 * 
		 * String xpath2="//a[contains(text()"+ ","+ "\""+ videoresponse+ "\"" + ")]";
		 * System.out.println(xpath2);
		 */
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath1))).click();
		//driver.findElement(By.xpath("//a[contains(text(),'DSTC 2019 | Khimananad Upreti')]")).click();
		
		driver.findElement(By.cssSelector("button.ytp-button.ytp-settings-button")).click();
		// Actions actions =new Actions(driver);
		 WebElement element1=driver.findElement(By.cssSelector("button.ytp-button.ytp-settings-button"));
		 Actions action=new Actions(driver);
     	action.moveToElement(element1).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
     	action.moveToElement(element1).sendKeys(Keys.ARROW_UP).sendKeys(Keys.ARROW_UP).sendKeys(Keys.ARROW_UP).sendKeys(Keys.ENTER).build().perform();
     
		 
		 
		
     	List <WebElement> upNextVideosList= driver.findElements(By.cssSelector("div.style-scope ytd-watch-next-secondary-results-renderer ytd-compact-autoplay-renderer.style-scope.ytd-watch-next-secondary-results-renderer"));
		   List<String> upNextYoutubevideoList =new ArrayList<String>();
			for(int i=0;upNextVideosList.size()>i;i++) {
				String videoList=upNextVideosList.get(i).getText().trim();
				System.out.println(videoList);
				if(i==4) {
				upNextYoutubevideoList.add(videoList);
			}
			}
		
		//div.style-scope ytd-watch-next-secondary-results-renderer
  }
  

}
