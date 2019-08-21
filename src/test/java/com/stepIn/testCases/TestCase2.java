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
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;


public class TestCase2 {
	CommonMethods common = new CommonMethods();
	
	
  @Test
  public void teststep1() {
	  System.out.println("test step1 executed");
	  
	  //Once video will be scroll down to center the page take screenshot 
	  WebDriver driver = new ChromeDriver();
		driver.get("http://www.google.com/");
		File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		
		try {
			FileUtils.copyFile(scrFile, new File("c:\\tmp\\screenshot.png"));
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
		
		
		
		List <WebElement> upNextVideosList= new ArrayList();
		upNextVideosList.
  }
  
  @Test
  public void teststep2() {
	  System.out.println("test step2 executed");
  }
}
