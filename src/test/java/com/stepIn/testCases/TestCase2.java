package com.stepIn.testCases;

/*import com.citi.testCase.ChromeDriver;
import com.citi.testCase.TakesScreenshot;
import com.citi.testCase.WebDriver;*/
import com.stepIn.pages.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;


public class TestCase2 {
	CommonMethods common = new CommonMethods();
	
	
  @Test
  public void teststep1() {
	  System.out.println("test step1 executed");
	  
	  //Once video will be scroll down to center the page take screenshot 
	  System.setProperty("webdriver.chrome.driver", "Utils/Drivers/chromedriver.exe");
	  WebDriver driver = new ChromeDriver();
		driver.get("http://www.google.com/");
		File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		
		try {
			FileUtils.copyFile(scrFile, new File("testNgMavenExample\\screenshots\\screenshot.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Need the get method response from random video and click on video link 
		String randomVideoName="";
		WebDriverWait wait = new WebDriverWait(driver, 30);
		String xpath="//a[contains(text(),"+ "\""+randomVideoName+"\"" +")]";	
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).click();
		//driver.findElement(By.xpath("//a[contains(text(),'DSTC 2019 | Khimananad Upreti')]")).click();
		
		driver.findElement(By.cssSelector("button.ytp-button.ytp-settings-button")).click();
		// Actions actions =new Actions(driver);
		 WebElement element=driver.findElement(By.cssSelector("button.ytp-button.ytp-settings-button"));
		 element.sendKeys(Keys.ARROW_DOWN);
		 element.sendKeys(Keys.ARROW_DOWN);
		 element.sendKeys(Keys.ENTER);
		 element.sendKeys(Keys.ARROW_UP);
		 element.sendKeys(Keys.ARROW_UP);
		 element.sendKeys(Keys.ARROW_UP);
		 element.sendKeys(Keys.ENTER);
		 
		 
		
		List <WebElement> upNextVideosList= driver.findElements(By.cssSelector("div.style-scope ytd-watch-next-secondary-results-renderer ytd-compact-autoplay-renderer.style-scope.ytd-watch-next-secondary-results-renderer"));
		   List<String> upNextYoutubevideoList =new ArrayList<String>();
			for(int i=0;upNextVideosList.size()>i;i++) {
				String videoList=upNextVideosList.get(i).getText().trim();
				System.out.println(videoList);
				upNextYoutubevideoList.add(videoList);
			}
		
		//div.style-scope ytd-watch-next-secondary-results-renderer
  }
  
  @Test
  public void teststep2() {
	  System.out.println("test step2 executed");
  }
}
